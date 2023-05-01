package main.adaptation;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.Modifier;

import main.adaptation.interfaces.IAdapter;
import main.adaptation.interfaces.IRUASTNode;
import main.util.Utile;
import main.adaptation.interfaces.IRUAST;

/**
 * Adapte l'AST de JDT en notre struture.
 * Cela nous permettra en effet de fusionner facilement les arbres.
 */
public class JDTtoRUASTAdapter extends ASTVisitor implements IAdapter {
    private Map<String, List<IRUAST>> groupes;
    private int variantId;
    private File currentFile;

    public JDTtoRUASTAdapter(int variant) {
        this.variantId = variant;
    }

    public void insert(String groupe, IRUAST tree) {
        if (!groupes.containsKey(groupe)) {
            groupes.put(groupe, new ArrayList<IRUAST>());
        }
        groupes.get(groupe).add(tree);
    }

    /**
     * COnstruit un arbre RUAST à partir de l'unité de compilation
     * d'un fichier Java.
     */
    public IRUAST adapt(CompilationUnit cu) {
        groupes = new HashMap<>();
        cu.accept(this);
        if (groupes.get("class") == null || groupes.get("class").isEmpty()) {
            // Utile.DEBUG_ON = true;
            Utile.debug_print("groupes[" + variantId + "]: " + groupes);
            Utile.debug_print(currentFile);
            return null;
        }

        return groupes.get("class").get(0);
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        Utile.debug_print("Type: " + node);
        Set<Integer> variants = new HashSet<>();
        variants.add(variantId);
        IRUASTNode root = new RUASTNode(node, 0, variants, RUASTNodeType.TYPE_DEFINITION);
        // des classes peuvent avoir les mêmes noms dans des packages differents
        // (heritages)
        // Le nom d'une class sera donc combiner avec celui de son parent + package ?
        root.setName(typeDeclarationName(node));
        IRUAST parent = null;
        IRUAST ruastTree = new RUASTTree(root, parent, new ArrayList<>());
        insert("class", ruastTree);
        return super.visit(node);
    }

    /**
     * Inclure les details des parametres dans le nom des methodes ?
     * Deux constructeurs ayant des signatures differentes ne sont pas identiques.
     * 
     * @param node
     * @return
     */
    private String typeDeclarationName(TypeDeclaration node) {
        String superClass = "Object";
        if (node.getSuperclassType() != null) {
            superClass = node.getSuperclassType().toString();
        }
        return node.getName().toString() + ">" + superClass;
    }

    /**
     * 
     */
    @Override
    public boolean visit(MethodDeclaration node) {
        if (methodShouldBeSkipped(node)) {
            return super.visit(node);
        }
        Set<Integer> variants = new HashSet<>();
        variants.add(variantId);
        IRUASTNode root = new RUASTNode(node, 0, variants, RUASTNodeType.METHOD);
        root.setName(methodDeclarationName(node));
        ASTNode classNode = node.getParent();
        IRUAST parent = findThroughClasses(classNode); // cherche dans les classes
        if (parent == null) {
            Utile.debug_print(groupes);
        }
        Utile.assertionCheck(parent != null, "le parent doit avoir ete visite [parcours en profondeur]");
        IRUAST ruastTree = new RUASTTree(root, parent, new ArrayList<>());
        insert("method", ruastTree);
        return super.visit(node);
    }

    /**
     * Construit une chaine qui servira du nom pour la methode.
     * En effet prendre tout simplement le nom la methode peut introduire
     * de l'ambiguite dans le cas ou la méthode à fait l'objet d'un ou plusieurs
     * surcharges. C'est pourquoi en plus du nom, nous introduirons d'autres
     * informations
     * sur la methode à savoir l'accesseur, le type de retour, les types des
     * arguments si ils existents.
     * Par exemple pour la méthode presente, le nom calculé sera:
     * "private-String-methodDeclarationName(MethodDeclaration)""
     */
    public static String methodDeclarationName(MethodDeclaration methodDeclaration) {
        StringBuilder methodSignature = new StringBuilder();

        // Récupérer les accesseurs de la méthode (s'ils existent)
        List<IExtendedModifier> modifiers = methodDeclaration.modifiers();
        if (modifiers != null) {
            for (IExtendedModifier modifier : modifiers) {
                if (modifier instanceof Modifier) {
                    methodSignature.append(((Modifier) modifier).getKeyword().toString()).append("-");
                } else if (modifier instanceof Annotation) {
                    methodSignature.append(((Annotation) modifier).toString()).append("-");
                }
            }
        }

        // Récupérer le type de retour
        Type returnType = methodDeclaration.getReturnType2();
        if (returnType != null) {
            methodSignature.append(returnType.toString()).append("-");
        }

        // Récupération du le nom de la méthode
        String methodName = methodDeclaration.getName().toString();
        methodSignature.append(methodName).append("(");

        // Récupération des paramètres et traitement
        List<SingleVariableDeclaration> parameters = methodDeclaration.parameters();
        if (parameters != null) {
            for (SingleVariableDeclaration parameter : parameters) {
                // Récupérer le type et le nom du paramètre
                Type parameterType = parameter.getType();
                String parameterName = parameter.getName().toString();

                // Ajouter le type et le nom du paramètre à la signature de la méthode
                methodSignature.append(parameterType.toString()).append(",");
            }

            // Retirer la virgule finale de la liste de paramètres
            if (parameters.size() > 0) {
                methodSignature.deleteCharAt(methodSignature.length() - 1);
            }
        }

        methodSignature.append(")");

        return methodSignature.toString();
    }

    /**
     * Quand on visite les champs des classes, l'idée est d'ajouter dans la
     * collection
     * des attributs dans <b>groupes</b> (liste associée à la clé "field" dans
     * groupes). On doit en conséquence
     * construitre l'arbre <b>RUATS</b> qui lui correspond. Cela revient à préciser:
     * <ol>
     * <li><b>ses enfantsS</b> (des <b>RUATS</b>): aucun dans le cas d'un
     * attribut.</li>
     * <li>
     * <b>son père:</b> La visite de l'AST de JDT etant en profondeur, le parent du
     * noeud courant
     * est forcement visité. Cela signfie qu'on a déjà construit le RUAST
     * correspondant
     * à son père. On peut facilement le retrouver dans le groupe des méthodes (clé
     * "method" dans groupe).
     * </li>
     * </ol>
     */
    @Override
    public boolean visit(FieldDeclaration node) {
        if (fieldShouldBeSkipped(node)) {
            return super.visit(node);
        }
        Set<Integer> variants = new HashSet<>();
        variants.add(variantId);
        IRUASTNode root = new RUASTNode(node, 0, variants, RUASTNodeType.FIELD);
        root.setName(fieldDeclarationName(node));
        ASTNode classNode = node.getParent();
        IRUAST parent = findThroughClasses(classNode);
        Utile.assertionCheck(parent != null, "le parent doit avoir ete visite [parcours en profondeur]");
        IRUAST ruastTree = new RUASTTree(root, parent, new ArrayList<>());
        insert("field", ruastTree);
        return super.visit(node);
    }

    /**
     * Calcul de l'identifiant de la variable.
     * 
     * @param node
     * @return
     */
    private String fieldDeclarationName(FieldDeclaration node) {
        return node.fragments().get(0).toString();
    }

    @Override
    public boolean visit(Block node) {
        if (blocShouldBeSkipped(node)) {
            return super.visit(node);
        }

        ASTNode methodNode = node.getParent();
        // on ignore le cas du corps d'une classe.
        // C'est le corps des methodes qui nous interesse (Statements).
        if (methodNode.getNodeType() != ASTNode.METHOD_DECLARATION) {
            return super.visit(node);
        }
        for (Object statement : node.statements()) {
            IRUAST parent = findThroughMethods(methodNode); // cherche parmi les methodes

            Set<Integer> variants = new HashSet<>();
            variants.add(variantId);
            IRUASTNode root = new RUASTNode((ASTNode) statement, 0, variants, RUASTNodeType.STATEMENT);
            if (parent == null) {
                Utile.debug_print(node);
                Utile.debug_print(node.getParent());
            }
            Utile.assertionCheck(parent != null, "le parent doit avoir ete visite [parcours en profondeur]");
            IRUAST ruastTree = new RUASTTree(root, parent, new ArrayList<>());
            root.setName(statement.toString());
            insert("statement", ruastTree);
        }

        return super.visit(node);
    }

    /**
     * On doit trouver l'arbre RUAST contenant un noeud en parametre.
     * Cette méthode est utilisée pour retrouver les parents des noeuds
     * (voir visit(FieldDeclaration|MethodDeclaration)).
     * 
     * @param nodeToFind: le noeud à retrouver dans le dictionnaire groupes.
     * @param groupe:     une clé dans le dictionnaire groupes.
     * @return
     */
    private IRUAST findInGroupes(ASTNode nodeToFind, String groupe) {
        List<IRUAST> candidatesClasses = groupes.get(groupe);
        for (IRUAST candidat : candidatesClasses) {
            if (candidat.getRoot().getJdtNode() == nodeToFind) {
                return candidat;
            }
        }
        return null;
    }

    private IRUAST findThroughMethods(ASTNode node) {
        return findInGroupes(node, "method");
    }

    private IRUAST findThroughClasses(ASTNode node) {
        return findInGroupes(node, "class");
    }

    /**
     * Construit un arbre RUATS à partir des sources d'un projet?
     * 
     * @param variantPath represente ici le chemin vers la racine d'une
     *                    applicaiton Java.
     * @return Un arbre RUAST decrivant le projet (Variant -> [CLass -> [Field] &&
     *         [Method -> Statement] ])
     */
    @Override
    public IRUAST adapt(String variantPath) {
        List<File> files = getAllJavaFiles(variantPath);
        Stream<File> stream = files.stream();
        List<IRUAST> classesRuast = stream.map(e -> {
            CompilationUnit cu = getCompilationUnit(e);
            currentFile = e;
            return this.adapt(cu);
        }).filter(ruast -> ruast != null).collect(Collectors.toList());

        // la racine est un noeud de type VARIANT
        Set<Integer> variants = new HashSet<>();
        variants.add(variantId);
        IRUASTNode variantRoot = new RUASTNode(null, 0, variants, RUASTNodeType.VARIANT);
        variantRoot.setName("Variant");
        // toutes les classes sont les enfants de la racine
        IRUAST ruast = new RUASTTree(variantRoot, null, classesRuast);
        return ruast;
    }

    private CompilationUnit getCompilationUnit(File file) {
        char[] source = null;
        try {
            FileReader reader = new FileReader(file);
            source = new char[(int) file.length()];
            reader.read(source);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(source);
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        return cu;
    }

    private List<File> getAllJavaFiles(String variantPath) {
        return getAllJavaFilesAux(new File(variantPath));
    }

    private List<File> getAllJavaFilesAux(File f) {
        List<File> files = new ArrayList<>();
        File[] list = f.listFiles();
        if (list == null)
            return files;
        for (File file : list) {
            if (file.isDirectory()) {
                files.addAll(getAllJavaFilesAux(file));
            } else {
                if (file.getName().endsWith(".java")) {
                    files.add(file);
                }
            }
        }
        return files;
    }

    /**
     * Verifie si le noeud est dans une classe anonyme ou pas.
     * Rappel: Nous faisons abstraction du type des instructions.
     * - cas1: Si une methode est dans une classe anonyme, elle correspond à
     * une instruction et nous faisons abstraction des instructions pour le moment.
     * - cas 2: Si c'est un bloc d'instruction, c'est pareil, inutile de chercher la
     * methode
     * parent parceque celle ci ne sera pas collecter dans groupes.
     * 
     * @param node
     * @return
     */
    private boolean nodeIsInAnonymousClass(ASTNode node) {
        ASTNode parent = node.getParent();
        while (parent != null) {
            if (parent.getNodeType() == ASTNode.ANONYMOUS_CLASS_DECLARATION) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    private boolean methodShouldBeSkipped(MethodDeclaration node) {
        return methodIsInStatement(node) || nodeIsInAnonymousClass(node);
    }

    private boolean methodIsInStatement(MethodDeclaration node) {
        ASTNode parent = node.getParent();
        while (parent != null) {
            if (parent.getNodeType() == ASTNode.METHOD_DECLARATION) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;

    }

    private boolean blocShouldBeSkipped(Block node) {
        return nodeIsInStatement(node) || nodeIsInAnonymousClass(node);
    }

    private boolean fieldShouldBeSkipped(FieldDeclaration node) {
        return nodeIsInAnonymousClass(node);
    }

    private boolean nodeIsInStatement(ASTNode node) {
        ASTNode parent = node.getParent();
        while (parent != null) {
            if (parent.getNodeType() == ASTNode.BLOCK) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }
}
package main.adaptation;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
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
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.osgi.framework.debug.Debug;

import main.adaptation.interfaces.IAdapter;
import main.adaptation.interfaces.IRUASTNode;
import main.util.Utile;
import main.adaptation.interfaces.IRUAST;

/**
 * Le but de cette est d'adapter l'AST de JDT en notre struture
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
        // si le groupe n'existe pas, initialiser à liste vide
        if (!groupes.containsKey(groupe)) {
            groupes.put(groupe, new ArrayList<IRUAST>());
        }
        // sinon inserer tree
        groupes.get(groupe).add(tree);
    }

    public void checkRelation() {
        IRUAST method = groupes.get("class").get(0);
       Utile.debug_print("method " + method + ": " + method.getChildren());
    }

    public IRUAST adapt(CompilationUnit cu) {
        groupes = new HashMap<>();
        cu.accept(this);
        if (groupes.get("class") == null || groupes.get("class").isEmpty()) {
        //Utile.DEBUG_ON = true;
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
        root.setName(node.getName().toString());
        IRUAST parent = null;
        IRUAST ruastTree = new RUASTTree(root, parent, new ArrayList<>());
        insert("class", ruastTree);
        return super.visit(node);
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        if (methodShouldBeSkipped(node)) {
            return super.visit(node);
        }
        Set<Integer> variants = new HashSet<>();
        variants.add(variantId);
        IRUASTNode root = new RUASTNode(node, 0, variants, RUASTNodeType.METHOD);
        root.setName(node.getName().toString());
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

    @Override
    public boolean visit(FieldDeclaration node) {
        if (fieldShouldBeSkipped(node)) {
            return super.visit(node);
        }
        Set<Integer> variants = new HashSet<>();
        variants.add(variantId);
        IRUASTNode root = new RUASTNode(node, 0, variants, RUASTNodeType.FIELD);
        root.setName(node.toString());
        ASTNode classNode = node.getParent();
        IRUAST parent = findThroughClasses(classNode);
        if (parent == null) {
            Utile.debug_print(node);
            Utile.debug_print(node.getParent());
        }
        Utile.assertionCheck(parent != null, "le parent doit avoir ete visite [parcours en profondeur]");
        IRUAST ruastTree = new RUASTTree(root, parent, new ArrayList<>());
        insert("field", ruastTree);
        return super.visit(node);
    }

    @Override
    public boolean visit(Block node) {
        if ( blocShouldBeSkipped(node)) {
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
            IRUASTNode root = new RUASTNode(node, 0, variants, RUASTNodeType.STATEMENT);
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
        variantRoot.setName("variant");
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


    /* Cas vicieux */

    /**
     * Verifie si le noeud est dans une classe anonyme ou pas.
     * - cas1: Si une methode est dans une classe anonyme, elle correspond à
     * une instruction et nous faisons abstraction des instructions pour le moment.
     * - cas 2: Si c'est un bloc d'instruction, c'est pareil, inutile de chercher la methode
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
package main.adaptation;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

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
        System.out.println("method " + method + ": " + method.getChildren());
    }

    public IRUAST adapt(CompilationUnit cu) {
        groupes = new HashMap<>();
        cu.accept(this);
        return groupes.get("class").get(0);
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        VariantsSet variants = new VariantsSet();
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
        VariantsSet variants = new VariantsSet();
        variants.add(variantId);
        IRUASTNode root = new RUASTNode(node, 0, variants, RUASTNodeType.METHOD);
        root.setName(node.getName().toString());
        ASTNode classNode = node.getParent();
        IRUAST parent = findThroughClasses(classNode); // cherche dans les classes
        Utile.assertionCheck(parent != null, "le parent doit avoir ete visite [parcours en profondeur]");
        IRUAST ruastTree = new RUASTTree(root, parent, new ArrayList<>());
        insert("method", ruastTree);
        return super.visit(node);
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        VariantsSet variants = new VariantsSet();
        variants.add(variantId);
        IRUASTNode root = new RUASTNode(node, 0, variants, RUASTNodeType.FIELD);
        root.setName(node.toString());
        ASTNode classNode = node.getParent();
        IRUAST parent = findThroughClasses(classNode); // cherche dans les classes
        Utile.assertionCheck(parent != null, "le parent doit avoir ete visite [parcours en profondeur]");
        IRUAST ruastTree = new RUASTTree(root, parent, new ArrayList<>());
        insert("field", ruastTree);
        return super.visit(node);
    }

    @Override
    public boolean visit(Block node) {
        for (Object statement : node.statements()) {
            ASTNode methodNode = node.getParent();
            if (methodNode.getNodeType() != ASTNode.METHOD_DECLARATION) {
                continue;
            }
            IRUAST parent = findThroughMethods(methodNode); // cherche parmi les methodes

            VariantsSet variants = new VariantsSet();
            variants.add(variantId);
            IRUASTNode root = new RUASTNode(node, 0, variants, RUASTNodeType.STATEMENT);
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
        List<IRUAST> classesRuast = files.stream().map(e -> {
            CompilationUnit cu = getCompilationUnit(e);
            return this.adapt(cu);
        }).collect(Collectors.toList());
        // la racine est un noeud de type VARIANT
        VariantsSet variants = new VariantsSet();
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
}
package adaptation;

import java.beans.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import adaptation.interfaces.IAdapter;
import adaptation.interfaces.IRUASTNode;
import adaptation.interfaces.IRUAST;

/**
 * Le but de cette est d'adapter l'AST de JDT en notre struture
 * Cela nous permettra en effet de fusionner facilement les arbres.
 */
public class JDTtoRUASTAdapter extends ASTVisitor implements IAdapter {
    static Integer VARIANT_ID = 0;
    private Map<String, List<IRUAST>> groupes = new HashMap<>();

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
        cu.accept(this);
        // System.out.println(groupes);
        checkRelation();
        return null;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        IRUASTNode root = new RUASTNode(node, 0, VARIANT_ID, RUASTNodeType.CLASS);
        root.setName(node.getName().toString());
        IRUAST parent = null;
        IRUAST ruastTree = new RUASTTree(root, parent, new ArrayList<>());
        insert("class", ruastTree);
        return super.visit(node);
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        IRUASTNode root = new RUASTNode(node, 0, VARIANT_ID, RUASTNodeType.METHOD);
        root.setName(node.getName().toString());
        ASTNode classNode = node.getParent();
        IRUAST parent = findThroughClasses(classNode); // cherche dans les classes
        assertionCheck(parent != null, "le parent doit avoir ete visite [parcours en profondeur]");
        IRUAST ruastTree = new RUASTTree(root, parent, new ArrayList<>());
        insert("method", ruastTree);
        return super.visit(node);
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        IRUASTNode root = new RUASTNode(node, 0, VARIANT_ID, RUASTNodeType.FIELD);
        root.setName(node.toString());
        ASTNode classNode = node.getParent();
        IRUAST parent = findThroughClasses(classNode); // cherche dans les classes
        assertionCheck(parent != null, "le parent doit avoir ete visite [parcours en profondeur]");
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
            IRUASTNode root = new RUASTNode(node, 0, VARIANT_ID, RUASTNodeType.STATEMENT);
            assertionCheck(parent != null, "le parent doit avoir ete visite [parcours en profondeur]");
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

    private void assertionCheck(boolean b, String msg) {
        if (!b)
            throw new AssertionError(msg);
    }
}
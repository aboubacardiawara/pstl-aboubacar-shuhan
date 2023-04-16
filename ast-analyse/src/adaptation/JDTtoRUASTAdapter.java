package adaptation;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;

import adaptation.interfaces.IAdapter;
import adaptation.interfaces.IRUASTNode;
import adaptation.interfaces.IRUAST;

/**
 * Le but de cette est d'adapter l'AST de JDT en notre struture
 * Cela nous permettra en effet de fusionner facilement les arbres.
 */
public class JDTtoRUASTAdapter extends ASTVisitor implements IAdapter {
    static Integer VARIANT_ID = 0;
    // private IRUAST adaptedTree = new RUASTTree(null, new ArrayList<IRUAST>());
    private Map<String, List<IRUAST>> groupes = new HashMap<>();

    public void insert(String groupe, IRUAST tree) {
        // si le groupe n'existe pas, initialiser à liste vide
        if (!groupes.containsKey(groupe)) {
            groupes.put(groupe, new ArrayList<IRUAST>());
        }
        // sinon inserer tree
        groupes.get(groupe).add(tree);
    }

    public IRUAST adapt(CompilationUnit cu) {
        cu.accept(this);
        System.out.println(groupes);
        return null;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        IRUASTNode root = new RUASTNode(node, 0, VARIANT_ID, RUASTNodeType.CLASS);
        root.setName(node.getName().toString());
        IRUAST ruastTree = new RUASTTree(root, new ArrayList<>());
        insert("class", ruastTree);
        return super.visit(node);
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        // System.out.println(getSignature(node));
        IRUASTNode root = new RUASTNode(node, 0, VARIANT_ID, RUASTNodeType.METHOD);
        root.setName(node.getName().toString());
        IRUAST ruastTree = new RUASTTree(root, new ArrayList<>());
        insert("method", ruastTree);
        return super.visit(node);
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        IRUASTNode root = new RUASTNode(node, 0, VARIANT_ID, RUASTNodeType.FIELD);
        root.setName(node.toString());
        IRUAST ruastTree = new RUASTTree(root, new ArrayList<>());
        insert("field", ruastTree);
        return super.visit(node);
    }

    @Override
    public boolean visit(Block node) {
        for (Object statement : node.statements()) {
            IRUASTNode root = new RUASTNode(node, 0, VARIANT_ID, RUASTNodeType.STATEMENT);
            IRUAST ruastTree = new RUASTTree(root, new ArrayList<>());
            root.setName(statement.toString());
            insert("statement", ruastTree);
        }

        return super.visit(node);
    }
}
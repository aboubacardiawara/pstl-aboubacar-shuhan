package adaptation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import adaptation.interfaces.IAdapter;
import adaptation.interfaces.IRUASTNode;
import adaptation.interfaces.IRUAST;

/**
 * Le but de cette est d'adapter l'AST de JDT en notre struture
 * Cela nous permettra en effet de fusionner facilement les arbres.
 */
public class JDTtoRUASTAdapter
        implements IAdapter {
    static Integer VARIANT_ID = 0;

    public IRUAST adapt(CompilationUnit cu) {
        IRUASTNode root = new RUASTNode(cu, 0, VARIANT_ID, RUASTNodeType.VARIANT);
        List<IRUAST> newChildren = new ArrayList<>();
        IRUAST adaptedTree = new RUASTTree(root, newChildren);

        // Visiteur d'AST pour parcourir l'AST JDT
        ASTVisitor visitor = new ASTVisitor() {

            // Visite d'une classe
            public boolean visit(TypeDeclaration node) {
                String className = node.getName().getFullyQualifiedName();
                IRUASTNode classNode = new RUASTNode(node, 0, VARIANT_ID, RUASTNodeType.CLASS);
                classNode.setName(className);
                IRUAST child = null;
                newChildren.add(child);
                return true;
            }

            // Visite d'une méthode
            public boolean visit(MethodDeclaration node) {
                String methodName = node.getName().getFullyQualifiedName();
                IRUASTNode methodNode = new RUASTNode(node, 0, VARIANT_ID, RUASTNodeType.METHOD);
                methodNode.setName(methodName);
                IRUAST classNode = findClassNode(newChildren, node);
                classNode.addChild(new RUASTTree(methodNode, new ArrayList<IRUAST>()));
                return true;
            }
        };

        cu.accept(visitor);

        return adaptedTree;
    }

    /*
     * Trouve le noeud de classe parent d'une méthode
     */
    private IRUAST findClassNode(List<IRUAST> trees, MethodDeclaration node) {
        String className = node.resolveBinding().getDeclaringClass().getQualifiedName();
        for (IRUAST classTree : trees) {
            // if (classNode.getType().equals("class") && classNode.getName().equals(className)) {
            //     return classNode;
            // }
        }
        return null;
    }

}
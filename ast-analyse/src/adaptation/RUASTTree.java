package adaptation;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import adaptation.interfaces.IRUASTNode;
import adaptation.interfaces.IRUAST;

/**
 * Ceci est une representaiton compacte de l'AST d'un code java.base/
 * Nous voulons introduire une abstraction sur le type des noeuds,
 * Cela dans le but de simplifier le processus de fusion.
 */
public class RUASTTree implements IRUAST {

    private IRUASTNode root;
    private List<IRUAST> children;

    public RUASTTree(IRUASTNode adaptedRoot, List<IRUAST> adaptedChildren) {
        root = adaptedRoot;
        children = adaptedChildren;
    }

    @Override
    public IRUASTNode getRoot() {
        return root;
    }

    @Override
    public List<IRUAST> getChildren() {
        return children;
    }

    @Override
    public void addChild(IRUAST node) {
        children.add(node);
    }

}
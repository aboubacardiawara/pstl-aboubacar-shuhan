package main.adaptation;

import java.util.List;

import main.adaptation.interfaces.IRUASTNode;
import main.adaptation.interfaces.IRUAST;

/**
 * Ceci est une representaiton compacte de l'AST d'un code java.base/
 * Nous voulons introduire une abstraction sur le type des noeuds,
 * Cela dans le but de simplifier le processus de fusion.
 */
public class RUASTTree implements IRUAST {

    private IRUASTNode root;
    private List<IRUAST> children;
    private IRUAST parent;

    public RUASTTree(IRUASTNode adaptedRoot, IRUAST adaptedParent, List<IRUAST> adaptedChildren) {
        root = adaptedRoot;
        children = adaptedChildren;
        parent = adaptedParent;
        if (adaptedParent != null) {
            adaptedParent.addChild(this);
        }
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

    @Override
    public String toString() {
        return root.toString();
    }

    @Override
    public IRUAST getParent() {
        return parent;
    }

	@Override
	public boolean isLeaf() {
		RUASTNodeType rootType = this.getRoot().getType();
		return rootType == RUASTNodeType.FIELD | rootType == RUASTNodeType.STATEMENT ;
	}

	@Override
	public String getName() {
		return this.root.getName();
	}

}
package main.adaptation;

import org.eclipse.jdt.core.dom.ASTNode;

import main.adaptation.interfaces.IRUASTNode;

public class RUASTNode implements IRUASTNode {

    protected String name;
    protected ASTNode jdtnode;
    protected int id;
    protected VariantsSet variants;
    private RUASTNodeType type;

    public RUASTNode(ASTNode node, int nodeId, Integer variant, RUASTNodeType nodeType) {
        jdtnode = node;
        id = nodeId;
        variants = new VariantsSet();
        variants.add(variant);
        type = nodeType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public VariantsSet getVariants() {
        return variants;
    }

    @Override
    public RUASTNodeType getType() {
        return type;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public ASTNode getJdtNode() {
        return jdtnode;
    }
}
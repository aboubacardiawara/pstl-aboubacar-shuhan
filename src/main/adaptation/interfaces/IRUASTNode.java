package main.adaptation.interfaces;

import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;

import main.adaptation.RUASTNodeType;
import main.adaptation.VariantsSet;

public interface IRUASTNode {

    public String getName();

    public int getId();

    public VariantsSet getVariants();

    public RUASTNodeType getType();

    public void setName(String className);

    public ASTNode getJdtNode();
}
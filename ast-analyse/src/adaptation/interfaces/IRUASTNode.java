package adaptation.interfaces;

import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;

import adaptation.RUASTNodeType;

public interface IRUASTNode {

    public String getName();

    public int getId();

    public Set<Integer> getVariants();

    public RUASTNodeType getType();

    public void setName(String className);

    public ASTNode getJdtNode();
}
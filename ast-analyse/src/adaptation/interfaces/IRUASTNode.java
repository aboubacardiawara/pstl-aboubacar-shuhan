package adaptation.interfaces;

import java.util.Set;

import adaptation.RUASTNodeType;

public interface IRUASTNode {
    public String getName();

    public int getId();

    public Set<Integer> getVariants();

    public RUASTNodeType getType();

    public void setName(String className);
       
}
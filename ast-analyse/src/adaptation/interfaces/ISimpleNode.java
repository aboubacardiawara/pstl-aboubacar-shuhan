package adaptation.interfaces;

import java.util.Set;

import adaptation.SimpleNodeType;

public interface ISimpleNode {
    public String getName();
    public int getId();
    public Set<Integer> getVariants(); 
    public SimpleNodeType getType();
    

}

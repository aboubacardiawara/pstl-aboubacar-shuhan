package adaptation;

import java.util.Set;

public interface SimpleNodeI {
    public String getName();
    public int getId();
    public Set<Integer> getVariants(); 
    public SimpleNodeType getType();
    

}

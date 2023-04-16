package adaptation.interfaces;

import java.util.List;

public interface IRUAST {

    public IRUASTNode getRoot();

    public void addChild(IRUAST classNode);

    public List<IRUAST> getChildren();

    public IRUAST getParent();

}
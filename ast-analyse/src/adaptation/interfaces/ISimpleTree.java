package adaptation.interfaces;

import java.util.List;

import adaptation.SimpleNode;

public interface ISimpleTree {
    public SimpleNode getRoot();
    public List<SimpleNode> getChildren();
}
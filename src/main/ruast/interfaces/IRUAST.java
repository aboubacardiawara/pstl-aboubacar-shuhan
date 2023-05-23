package main.ruast.interfaces;

import java.util.List;
import java.util.Set;

import main.ruast.impl.RUASTTree;

public interface IRUAST extends IForgeData {

    public IRUASTNode getRoot();

    public void addChild(IRUAST classNode);

    public List<IRUAST> getChildren();

    public IRUAST getParent();

    public boolean isLeaf();

    public String getName();

    public void setParent(RUASTTree tree);

    public Set<Integer> getVariants();

    public int size();

    public IForgeData getForgeData();
}
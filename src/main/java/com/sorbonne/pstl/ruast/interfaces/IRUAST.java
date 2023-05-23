package com.sorbonne.pstl.ruast.interfaces;

import java.util.List;
import java.util.Set;


public interface IRUAST extends IForgeData {

    public IRUASTNode getRoot();

    public void addChild(IRUAST classNode);

    public List<IRUAST> getChildren();

    public IRUAST getParent();

    public boolean isLeaf();

    public String getName();

    public void setParent(IRUAST tree);

    public Set<Integer> getVariants();

    public int size();

    public IForgeData getForgeData();
}
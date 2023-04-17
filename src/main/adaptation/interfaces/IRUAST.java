package main.adaptation.interfaces;

import java.util.List;

import main.adaptation.RUASTTree;

public interface IRUAST {

    public IRUASTNode getRoot();

    public void addChild(IRUAST classNode);

    public List<IRUAST> getChildren();

    public IRUAST getParent();

	public boolean isLeaf();

	public String getName();

    public void setParent(RUASTTree tree);

}
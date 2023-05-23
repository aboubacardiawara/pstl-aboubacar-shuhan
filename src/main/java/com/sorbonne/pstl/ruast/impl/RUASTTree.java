package com.sorbonne.pstl.ruast.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.sorbonne.pstl.ruast.interfaces.IForgeData;
import com.sorbonne.pstl.ruast.interfaces.IRUAST;
import com.sorbonne.pstl.ruast.interfaces.IRUASTNode;


/**
 * Ceci est une representaiton compacte de l'AST d'un code java.base/
 * Nous voulons introduire une abstraction sur le type des noeuds,
 * Cela dans le but de simplifier le processus de fusion.
 */
public class RUASTTree implements IRUAST {

    private IRUASTNode root;
    private List<IRUAST> children;
    private IRUAST parent;
    private IForgeData forgeData;

    public RUASTTree(IRUASTNode adaptedRoot, IRUAST adaptedParent, List<IRUAST> adaptedChildren) {
        this.forgeData = new ForgeData();
        root = adaptedRoot;
        children = new ArrayList<>();
        adaptedChildren.stream().forEach(child -> addChild(child));
        parent = adaptedParent;
        if (adaptedParent != null) {
            adaptedParent.addChild(this);
        }
    }

    @Override
    public IRUASTNode getRoot() {
        return root;
    }

    @Override
    public List<IRUAST> getChildren() {
        return children;
    }

    @Override
    public void addChild(IRUAST tree) {
        children.add(tree);
        tree.setParent(this);
    }

    @Override
    public String toString() {
        return root.toString();
    }

    @Override
    public IRUAST getParent() {
        return parent;
    }

    @Override
    public boolean isLeaf() {
        RUASTNodeType rootType = this.getRoot().getType();
        return rootType == RUASTNodeType.FIELD | rootType == RUASTNodeType.STATEMENT;
    }

    @Override
    public String getName() {
        return this.root.getName();
    }

    @Override
    public void setParent(IRUAST tree) {
        this.parent = tree;
    }

    @Override
    public Set<Integer> getVariants() {
        return this.root.getVariants();
    }

    @Override
    public int size() {
        return sizeAux(this);
    }

    private int sizeAux(RUASTTree ruastTree) {
        if (ruastTree.isLeaf()) {
            return 1;
        }
        int sizeSubtrees = 0;
        for (IRUAST subtree : ruastTree.getChildren()) {
            sizeSubtrees += subtree.size();
        }

        return 1 + sizeSubtrees;
    }

    @Override
    public IForgeData getForgeData() {
        return forgeData;
    }

    @Override
    public int setStartLine(int startLine) {
        return this.forgeData.setStartLine(startLine);
    }

    @Override
    public int setEndLine(int endLine) {
        return this.forgeData.setEndLine(endLine);
    }

    @Override
    public int getStartLine() {
        return this.forgeData.getStartLine();
    }

    @Override
    public int getEndLine() {
        return this.forgeData.getEndLine();
    }

    @Override
    public int getStartColumn() {
        return this.forgeData.getStartColumn();
    }

    @Override
    public int getEndColumn() {
        return this.forgeData.getEndColumn();
    }

    @Override
    public int setStartColumn(int startColumn) {
        return this.forgeData.setStartColumn(startColumn);
    }

    @Override
    public int setEndColumn(int endColumn) {
        return this.forgeData.setEndColumn(endColumn);
    }

    @Override
    public String fileName() {
        return this.forgeData.fileName();
    }

    @Override
    public void setFileName(String fileName) {
        this.forgeData.setFileName(fileName);
    }

}
package com.sorbonne.pstl.ruast.impl;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;

import com.sorbonne.pstl.ruast.interfaces.IRUASTNode;
import com.sorbonne.pstl.util.Utile;


public class RUASTNode implements IRUASTNode {

    protected String name;
    protected ASTNode jdtnode;
    protected int id;
    protected Set<Integer> variants;
    private RUASTNodeType type;
    protected Integer block;

    public RUASTNode(ASTNode node, int nodeId, Set<Integer> variant, RUASTNodeType nodeType) {
        jdtnode = node;
        id = nodeId;
        Utile.assertionCheck(variant != null, "Le variant ne doit pas etre null");
        variants = new HashSet<>(variant);
        type = nodeType;
        block = null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Set<Integer> getVariants() {
        return variants;
    }

    @Override
    public RUASTNodeType getType() {
        return type;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public ASTNode getJdtNode() {
        return jdtnode;
    }

    @Override
    public Integer getBlock() {
        return block;
    }

    @Override
    public void setBlock(Integer id_block) {
        this.block = id_block;
    }

}
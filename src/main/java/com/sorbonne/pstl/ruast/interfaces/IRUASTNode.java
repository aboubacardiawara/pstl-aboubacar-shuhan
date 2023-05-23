package com.sorbonne.pstl.ruast.interfaces;

import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;

import com.sorbonne.pstl.ruast.impl.RUASTNodeType;


public interface IRUASTNode {

    public String getName();

    public int getId();

    public Set<Integer> getVariants();

    public RUASTNodeType getType();

    public void setName(String className);

    public ASTNode getJdtNode();

    public Integer getBlock();

    public void setBlock(Integer id_block);

}
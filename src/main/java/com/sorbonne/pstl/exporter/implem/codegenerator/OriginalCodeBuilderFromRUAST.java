package com.sorbonne.pstl.exporter.implem.codeGeneration;

import com.sorbonne.pstl.ruast.interfaces.IRUAST;

public class OriginalCodeBuilderFromRUAST {

    public static String buildFrom(IRUAST ruast) {
        return ruast.getRoot().getJdtNode().toString();
    }
}
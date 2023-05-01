package main.exporter.implem.codeGeneration;

import main.adaptation.interfaces.IRUAST;

public class OriginalCodeBuilderFromRUAST {

    public static String buildFrom(IRUAST ruast) {
        return ruast.getRoot().getJdtNode().toString();
    }
}
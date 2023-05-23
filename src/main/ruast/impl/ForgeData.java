package main.ruast.impl;

import main.ruast.interfaces.IForgeData;

public class ForgeData implements IForgeData {

    private int startLine;
    private int endLine;
    private int startColumn;
    private int endColumn;
    private String fileName;

    @Override
    public int setStartLine(int startLine) {
        this.startLine = startLine;
        return this.startLine;
    }

    @Override
    public int setEndLine(int endLine) {
        this.endLine = endLine;
        return this.endLine;
    }

    @Override
    public int getStartLine() {
        return this.startLine;
    }

    @Override
    public int getEndLine() {
        return this.endLine;
    }

    @Override
    public int getStartColumn() {
        return this.startColumn;
    }

    @Override
    public int getEndColumn() {
        return this.endColumn;
    }

    @Override
    public int setStartColumn(int startColumn) {
        this.startColumn = startColumn;
        return this.startColumn;
    }

    @Override
    public int setEndColumn(int endColumn) {
        this.endColumn = endColumn;
        return this.endColumn;
    }

    @Override
    public String fileName() {
        return this.fileName;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

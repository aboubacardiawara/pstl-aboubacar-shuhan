package com.sorbonne.pstl.ruast.interfaces;

public interface IForgeData {
    public int setStartLine(int startLine);

    public int setEndLine(int endLine);

    public int getStartLine();

    public int getEndLine();

    public int getStartColumn();

    public int getEndColumn();

    public int setStartColumn(int startColumn);

    public int setEndColumn(int endColumn);

    public String fileName();

    public void setFileName(String fileName);
}

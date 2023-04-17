package main.adaptation.interfaces;

import org.eclipse.jdt.core.dom.CompilationUnit;

public interface IAdapter {

    public IRUAST adapt(CompilationUnit tree);
    public IRUAST adapt(String variantPath);

}

package main.visitor;

import java.util.List;

import org.eclipse.jdt.core.dom.*;

public class MethodVisitor extends ASTVisitor {
    
    @Override
    public boolean visit(MethodDeclaration node) {
        
        // Get the structural properties of the MethodDeclaration node
        List<StructuralPropertyDescriptor> properties = node.structuralPropertiesForType();
        
        // Print out each property's name
        for (StructuralPropertyDescriptor property : properties) {
            System.out.println(">> " + property.getId());
        }
        
        // Continue visiting the node's children
        return true;
    }
    
    public static void main(String[] args) {
        
        // Parse a Java source file into an AST
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource("public class MyClass { public void myMethod() {} }".toCharArray());
        CompilationUnit unit = (CompilationUnit) parser.createAST(null);
        
        // Visit the AST using a MethodVisitor
        MethodVisitor visitor = new MethodVisitor();
        unit.accept(visitor);
    }
}

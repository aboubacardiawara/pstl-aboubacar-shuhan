package main.visitor;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class Greater extends ASTVisitor {
	
	@Override
	public boolean visit(TypeDeclaration node) {
		System.out.println(node.getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(Block node) {
		for (Object o : node.statements()) {
			System.out.println("Statement: " + o);
		}
		return super.visit(node);
	}

}

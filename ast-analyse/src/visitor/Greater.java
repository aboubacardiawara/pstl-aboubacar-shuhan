package visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class Greater extends ASTVisitor {
	
	public boolean visit(PackageDeclaration node) {
		System.out.println();
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		System.out.println(node.getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		System.out.println(node.getName());
		return super.visit(node);
	}

}

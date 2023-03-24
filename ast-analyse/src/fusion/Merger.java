package fusion;

import java.awt.TexturePaint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class Merger {

	/**
	 * prend deux ast renvoie une liste d'ast après fusion
	 * 
	 * @param n1
	 * @param n2
	 * @return
	 */
	public  CompilationUnit fusion(CompilationUnit cu1, CompilationUnit cu2) {
        // On crée un nouvel objet AST
        AST ast = AST.newAST(AST.JLS3);

        // On crée une nouvelle CompilationUnit vide avec le nouvel AST
        CompilationUnit newCu = (CompilationUnit) ast.createInstance(CompilationUnit.class);

        // On copie les informations communes des deux CompilationUnit dans la nouvelle CompilationUnit
        newCu.setPackage(cu1.getPackage());
        newCu.imports().addAll(cu1.imports());
        newCu.types().addAll(cu1.types());

        // On ajoute les éléments spécifiques de la deuxième CompilationUnit dans la nouvelle CompilationUnit
        newCu.types().addAll(cu2.types());

        return newCu;
    }

	private String getMethodSignature(MethodDeclaration method) {
		StringBuilder signature = new StringBuilder();
		signature.append(method.getModifiers());
		signature.append(" ");
		signature.append(method.getReturnType2().toString());
		signature.append(" ");
		signature.append(method.getName().toString());
		signature.append("(");
		for (Object param : method.parameters()) {
			SingleVariableDeclaration variable = (SingleVariableDeclaration) param;
			signature.append(variable.getType().toString());
			signature.append(", ");
		}
		if (method.parameters().size() > 0) {
			signature.setLength(signature.length() - 2);
		}
		signature.append(")");
		return signature.toString();
	}

	public void replaceMethodDeclarations(TypeDeclaration typeDeclaration, List<MethodDeclaration> methodDeclarations) {
		// Supprimer toutes les déclarations de méthode existantes
		MethodDeclaration[] existingMethodDeclarations = typeDeclaration.getMethods();
		for (MethodDeclaration existingMethodDeclaration : existingMethodDeclarations) {
			existingMethodDeclaration.delete();
		}

		// Ajouter les nouvelles déclarations de méthode
		for (MethodDeclaration methodDeclaration : methodDeclarations) {
			typeDeclaration.bodyDeclarations().add(methodDeclaration);
		}
	}


}

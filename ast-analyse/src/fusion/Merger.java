package fusion;

import java.awt.TexturePaint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
	public List<ASTNode> merge(ASTNode n1, ASTNode n2) {

		List<ASTNode> res = new ArrayList<>();
		if (n1 instanceof MethodDeclaration && n2 instanceof MethodDeclaration) {

			// si les deux ont la même signature
			if (getMethodSignature((MethodDeclaration) n1).equals(getMethodSignature((MethodDeclaration) n2))) {
				res.add(n1);
				return res;
			} else {
				res.add(n1);
				res.add(n2);
				return res;
			}
		} else if (n1 instanceof TypeDeclaration && n2 instanceof TypeDeclaration) {
			TypeDeclaration c1 = (TypeDeclaration) n1;
			TypeDeclaration c2 = (TypeDeclaration) n2;
			if (c1.getName().equals(c2.getName())) {
				MethodDeclaration[] allMethod1 = c1.getMethods();
				MethodDeclaration[] allMethod2 = c2.getMethods();

				// Union des methodes.
				Map<String, MethodDeclaration> newMethods = new HashMap<>();
				for (MethodDeclaration method : allMethod1) {
					newMethods.put(getMethodSignature(method), method);
				}
				for (MethodDeclaration method : allMethod2) {
					newMethods.put(getMethodSignature(method), method);
				}
				
				replaceMethodDeclarations(c2, (List<MethodDeclaration>) newMethods.values());
				
				res.add(c1);
				return res;
			} else {
				res.add(c1);
				res.add(c2);
				return res;
			}

		} else {
			if (n1 instanceof CompilationUnit && n2 instanceof CompilationUnit) {
				
			}
			return merge(n1, n2);
		}
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

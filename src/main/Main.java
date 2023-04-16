package main;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import main.adaptation.JDTtoRUASTAdapter;
import main.adaptation.interfaces.IAdapter;
import main.adaptation.interfaces.IRUAST;

public class Main {

	private static CompilationUnit getCompilationUnit(File file) {
		char[] source = null;
		try {
			FileReader reader = new FileReader(file);
			source = new char[(int) file.length()];
			reader.read(source);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(source);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		return cu;
	}

	/**
	 * Fusion des classes `Account` des 4 premières variantes.
	 */
	private static void playGroundFusionClassesAccount() {
		int fileCount = 4;
		String rootDirectoryString = "./bank-variants/";
		// files path
		List<String> filesPath = new ArrayList<>();
		for (int i = 0; i < fileCount; i++) {
			String variant = "Variant0000" + (i + 1) + "/";
			String pkg = "bs/";
			String path = rootDirectoryString + variant + pkg + "Account.java";
			filesPath.add(path);
		}

		List<File> files = filesPath.stream().map(path -> new File(path)).collect(Collectors.toList());
		List<CompilationUnit> asts = files.stream().map(Main::getCompilationUnit).collect(Collectors.toList());
		IAdapter adapter = new JDTtoRUASTAdapter();
		List<IRUAST> adaptedAst = asts.stream().map(ast -> adapter.adapt(ast)).collect(Collectors.toList());
	}

	public static void main(String[] args) {
		playGroundFusionClassesAccount();
	}

}
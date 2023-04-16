import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.channels.NonWritableChannelException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

import adaptation.JDTtoRUASTAdapter;
import adaptation.RUASTNode;
import adaptation.RUASTTree;
import adaptation.interfaces.IRUASTNode;
import adaptation.interfaces.IAdapter;
import adaptation.interfaces.IRUAST;
import fusion.Merger;
import visitor.Greater;

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

	public static void main(String[] args) {
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
		System.out.println(adaptedAst.get(0));
	}

	private static void playGround() {
		String I1 = "if (ammount <= balance)";
		String I2 = "if (ammount <= balance + limit)";
		String I3 = "if (ammount <=  balance)";
		System.out.println(I1.equals(I3));
	}

}
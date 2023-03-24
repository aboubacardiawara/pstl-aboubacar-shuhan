import java.io.File;
import java.io.FileReader;
import java.nio.channels.NonWritableChannelException;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

import fusion.Merger;
import visitor.Greater;

public class Main {

	public static void main(String[] args) {
		String rootDirectoryString = "./bank-variants/";
		String pkg = "bs/bs/";
		String filePath = rootDirectoryString + "Variant00001/" + pkg + "RandomClass.java";
		String filePath2 = rootDirectoryString + "Variant00001/" + pkg + "RandomClass2.java";
		File file = new File(filePath);
		File file2 = new File(filePath2);
		CompilationUnit cu = getCompilationUnit(file);
		CompilationUnit cu2 = getCompilationUnit(file2);
		
		Merger merger = new Merger();
		
		ASTNode n1 = cu.getRoot();
		ASTNode n2 = cu2.getRoot();
		//System.out.println();
		CompilationUnit result = merger.fusion(cu, cu2);
		
		result.accept(new Greater());
	}

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
}
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import visitor.Greater;

public class Main {

	public static void main(String[] args) {
		String rootDirectoryString = "./bank-variants/";
		String pkg = "bs/bs/";  
		String filePath= rootDirectoryString + "Variant00001/" + pkg + "Account.java";
		File file = new File(filePath);
		char[] source = null;
		try {
			FileReader reader = new FileReader(file);
			source = new char[(int) file.length()]; // create char[] array to store file contents
			reader.read(source);
			reader.close(); // close the FileReader object
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // read file contents into the char[] array
        // do something with the char[] array, such as print it
        System.out.println(source);
        
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(source);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.accept(new ASTVisitor() {
			public boolean visit(MethodDeclaration node) {
				System.out.println("we just visit a method");
				return false;
				
			}
		});
		
	}
}
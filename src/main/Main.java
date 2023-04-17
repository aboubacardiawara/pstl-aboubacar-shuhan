package main;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import main.adaptation.JDTtoRUASTAdapter;
import main.adaptation.RUASTTree;
import main.adaptation.interfaces.IAdapter;
import main.adaptation.interfaces.IRUAST;
import main.fusion.IMerger;
import main.fusion.Merger;

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
	 * Adaptation des classes `Account` des 4 premières variantes.
	 */
	private static void playGroundAdaptationClassesAccount() {
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

	/**
	 * Adaptation des classes `Account` des 4 premières variantes.
	 */
	private static void playGroundFusionAccount1And2() {
		int fileCount = 2;
		String rootDirectoryString = "./bank-variants/";
		// files path
		List<String> filesPath = new ArrayList<>();
		for (int i = 0; i < fileCount; i++) {
			String variant = "Variant0000" + (i + 1) + "/";
			String pkg = "bs/";
			String path = rootDirectoryString + variant + pkg + "Account.java";
			filesPath.add(path);
		}

		// 1. Getting AST
		List<File> files = 
				filesPath.stream()
				.map(path -> new File(path))
				.collect(Collectors.toList());
		
		List<CompilationUnit> asts = 
				files.stream().map(
				Main::getCompilationUnit)
				.collect(Collectors.toList());
		
		// 2. Adaptation
		IAdapter adapter = new JDTtoRUASTAdapter();
		List<IRUAST> adaptedAst = 
				asts.stream()
				.map(ast -> adapter.adapt(ast))
				.collect(Collectors.toList());
		
		// 3. Merging
		IMerger merger = new Merger();
		IRUAST mergedTree = adaptedAst.stream().reduce(
				adaptedAst.get(0), (ruast1, ruast2) -> merger.merge(ruast1, ruast2)
				);
		
		System.out.println(mergedTree.getChildren().size());
	}
	
	/**
	 * Exemple de construction du RUAST d'un variant.
	 * Le RUAST d'un variant est un arbre qui a:
	 * - pour racine un noeud de type VARIANT
	 * - pour enfants les RUASTs correspondants aux classes
	 * du projet.
	 */
	private static void exempleConstructionRUATSVariant() {
		System.out.println("construction du RUAST d'un variant");
		String pathProduit1 = "./bank-variants/Variant00001/";
		String pathProduit2 = "./bank-variants/Variant00002/";

		// Liste de tous les fichiers JAVA de ce repertoire et des sous-repertoires
		List<File> files1 = Main.listJavaFiles(new File(pathProduit1)).stream().collect(Collectors.toList());
		List<File> files2 = Main.listJavaFiles(new File(pathProduit1)).stream().collect(Collectors.toList());

		List<IRUAST> s1 = files1.stream().map(e -> {
			CompilationUnit cu = Main.getCompilationUnit(e);
			IAdapter adapter = new JDTtoRUASTAdapter();
			return adapter.adapt(cu);
		}).collect(Collectors.toList());

		List<IRUAST> s2 = files2.stream().map(e -> {
			CompilationUnit cu = Main.getCompilationUnit(e);
			IAdapter adapter = new JDTtoRUASTAdapter();
			return adapter.adapt(cu);
		}).collect(Collectors.toList());

		//IRUAST r1 = new RUASTTree(null, null, s2)

		//List<IRUAST> ruastVariant = (new Merger()).mergeSequences(r1, r2);
		//System.out.println(ruastVariant.size());
	}
	private static Collection<? extends File> listJavaFiles(File f) {
		List<File> files = new ArrayList<>();
		File[] list = f.listFiles();
		if (list == null) return files;
		for (File file : list) {
			if (file.isDirectory()) {
				files.addAll(Main.listJavaFiles(file));
			} else {
				if (file.getName().endsWith(".java")) {
					files.add(file);
				}
			}
		}
		return files;
	}

	private static void lightExample() {
		String pathProduit1 = "./bank-variants/Variant00001/";
		String pathProduit2 = "./bank-variants/Variant00002/";
		String pathProduit3 = "./bank-variants/Variant00003/";
		String pathProduit4 = "./bank-variants/Variant00004/";

		IRUAST ruast1 = new JDTtoRUASTAdapter().adapt(pathProduit1);
		IRUAST ruast2 = new JDTtoRUASTAdapter().adapt(pathProduit2);
		IRUAST ruast3 = new JDTtoRUASTAdapter().adapt(pathProduit3);
		IRUAST ruast4 = new JDTtoRUASTAdapter().adapt(pathProduit4);

		IMerger merger = new Merger();
		IRUAST mergedTree12 = merger.merge(ruast1, ruast2);
		IRUAST mergedTree123 = merger.merge(mergedTree12, ruast3);
		IRUAST mergedTree1234 = merger.merge(mergedTree123, ruast4);

		System.out.println(mergedTree1234.getChildren().get(0).getChildren().size());
	}
	public static void main(String[] args) {
		lightExample();
	}

}
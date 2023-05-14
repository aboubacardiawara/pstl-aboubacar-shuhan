package main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import main.adaptation.JDTtoRUASTAdapter;
import main.adaptation.interfaces.IRUAST;
import main.exporter.IExporter;
import main.exporter.implem.codeGeneration.blocCodeGenerator.FeatureCodeExporter;
import main.fusion.Merger;
import main.identificationblocs.BlocsIdentifier;

public class Main {
	private static int VARIANT_ID = 1;
	private static String GENERATION_PATH;

	private static List<IRUAST> paralleleAdaptation(List<String> filesPath) {
		return filesPath.stream()
				.parallel()
				.map(path -> new JDTtoRUASTAdapter(VARIANT_ID++).adapt(path))
				.collect(Collectors.toList());
	}

	private static void exampleParallele() {
		List<String> filesPath = project();

		long startTime = System.currentTimeMillis();
		List<IRUAST> ruasts = paralleleAdaptation(filesPath);
		long endTime = System.currentTimeMillis();
		System.out.println("Duration (adaptation): " + (endTime - startTime) + " (ms)");

		startTime = System.currentTimeMillis();
		IRUAST mergedTree = ruasts.subList(1, ruasts.size()).stream().reduce(
				ruasts.get(0),
				(ruast1, ruast2) -> new Merger().merge(ruast1, ruast2));

		endTime = System.currentTimeMillis();
		System.out.println("Duration (merge): " + (endTime - startTime) + " (ms)");	
		startTime = System.currentTimeMillis();
		BlocsIdentifier blocsIdentifier = new BlocsIdentifier();
		blocsIdentifier.findBlocs(mergedTree);
		endTime = System.currentTimeMillis();
		System.out.println("Duration (identification bloc): " + (endTime - startTime) + " (ms)");
		
		List<Integer> toGen = new ArrayList<>();
		IExporter codegenerator = new FeatureCodeExporter(GENERATION_PATH,
				blocsIdentifier.getDependanciesManager(), toGen);
		codegenerator.generateMaximalCode();
		startTime = System.currentTimeMillis();
		codegenerator.export(mergedTree);
		endTime = System.currentTimeMillis();
		System.out.println("Duration (code generation): " + (endTime - startTime) + " (ms)");
	}

	private static List<String> project() {
		return banques();
	}

	private static List<String> notepad() {
		String notePad = "C:/Users/aboub_bmdb7gr/Downloads/Variant-Notepad";
		List<String> filesPath = new ArrayList<>();
		filesPath.add(notePad + "/Notepad-Copy");
		
		filesPath.add(notePad + "/Notepad-Cut");
		filesPath.add(notePad + "/Notepad-Cut-Find");
		filesPath.add(notePad + "/Notepad-Find");
		//filesPath.add(notePad + "/Notepad-Full");
		filesPath.add(notePad + "/Notepad-Undo-Redo");
		
		GENERATION_PATH = "generatedcode/notepad";
		return filesPath;
	}

	private static List<String> argouml() {
		List<String> filesPath = new ArrayList<>();
		for (int i = 1; i <= 9; i++) {
			String variantPath = "D:/cours/sorbonne/master/m1/s6/pstl/argouml/argouml/Variant000" + (i) + "/";
			filesPath.add(variantPath);
		}
		String variant10Path = "D:/cours/sorbonne/master/m1/s6/pstl/argouml/argouml/Variant0010/";
		filesPath.add(variant10Path);

		GENERATION_PATH = "generatedcode/argouml";
		return filesPath;
	}

	private static List<String> banques() {
		List<String> filesPath = new ArrayList<>();
		for (int i = 1; i <= 4; i++) {
			String variantPath = "./bank-variants/Variant0000" + (i) + "/";
			filesPath.add(variantPath);
		}

		GENERATION_PATH = "generatedcode/bank";
		return filesPath;
	}

	public static void main(String[] args) {
		exampleParallele();
	}

}
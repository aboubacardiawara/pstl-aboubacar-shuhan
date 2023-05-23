package main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import main.adaptation.JDTtoRUASTAdapter;
import main.exporter.IExporter;
import main.exporter.implem.codeGeneration.blocCodeGenerator.FeatureCodeExporter;
import main.fusion.Merger;
import main.identificationblocs.BlocsIdentifier;
import main.ruast.interfaces.IRUAST;

public class Main {
	private static int VARIANT_ID = 1;
	private static String GENERATION_PATH;
	private static int NB_XP = 5;

	private static List<IRUAST> paralleleAdaptation(List<String> filesPath) {
		return filesPath.stream()
				.parallel()
				.map(path -> new JDTtoRUASTAdapter(VARIANT_ID++).adapt(path))
				.collect(Collectors.toList());
	}

	private static void exampleParallele() {
		List<String> filesPath = project();

		// ADAPTATION
		List<IRUAST> ruasts = paralleleAdaptation(filesPath);

		// FUSION
		IRUAST mergedTree = ruasts.subList(1, ruasts.size()).stream().reduce(
				ruasts.get(0),
				(ruast1, ruast2) -> new Merger().merge(ruast1, ruast2));

		BlocsIdentifier blocsIdentifier = new BlocsIdentifier();
		blocsIdentifier.findBlocs(mergedTree);

		// print dependancies
		Finder finder = new Finder(mergedTree);
		finder.findByBloc(19).forEach(ruast -> {
			System.out.println(ruast.getRoot());
		});

		// GENERATE CODE
		List<Integer> toGen = new ArrayList<>();
		IExporter codegenerator = new FeatureCodeExporter(GENERATION_PATH,
				blocsIdentifier.getDependanciesManager(), toGen);
		codegenerator.generateMaximalCode();
		codegenerator.export(mergedTree);
		
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

	private static void experimentation() {
		mesureDureeCodeGen();
	}

	private static List<IRUAST> mesureDureeAdaptation() {
		List<String> filesPath = project();

		// duree adaptation
		long startTime = System.currentTimeMillis();
		List<IRUAST> ruast = new ArrayList<>();
		for (int i = 0; i < NB_XP; i++)
			ruast = paralleleAdaptation(filesPath);
		long endTime = System.currentTimeMillis();
		int dureeMoynne = (int) ((endTime - startTime) / NB_XP);
		System.out.println("Duree moyenne (adaptation): " + dureeMoynne + " (ms)");

		return ruast;
	}

	private static IRUAST mesureDureeFusion() {

		List<IRUAST> ruasts = mesureDureeAdaptation();

		// debut fusion
		long startTime = System.currentTimeMillis();
		IRUAST ruast = null;
		for (int i = 0; i < NB_XP; i++)
			ruast = fusion(ruasts);
		long endTime = System.currentTimeMillis();
		int dureeMoynne = (int) ((endTime - startTime) / NB_XP);
		System.out.println("Duree moyenne (fusion): " + dureeMoynne + " (ms)");

		return ruast;
	}

	

	private static IRUAST fusion(List<IRUAST> ruasts) {
		IRUAST mergedTree = ruasts.subList(1, ruasts.size()).stream().reduce(
				ruasts.get(0),
				(ruast1, ruast2) -> new Merger().merge(ruast1, ruast2));
		return mergedTree;
	}

	private static BlocsIdentifier mesureIdentificationBlocs() {
		
		IRUAST ruastMax = mesureDureeFusion();
		BlocsIdentifier blocsIdentifier = null;

		// debut 
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < NB_XP; i++) {
			blocsIdentifier = new BlocsIdentifier();
			blocsIdentifier.findBlocs(ruastMax);
		}

		long endTime = System.currentTimeMillis();
		int dureeMoynne = (int) ((endTime - startTime) / NB_XP);
		System.out.println("Duree moyenne (identification bloc): " + dureeMoynne + " (ms)");
		return blocsIdentifier;
	}

	private static void mesureDureeCodeGen() {
		BlocsIdentifier blocsIdentifier = mesureIdentificationBlocs();

		// debut generation de code
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < NB_XP; i++)
			codeGeneration(((BlocsIdentifier) blocsIdentifier).getRuast(), blocsIdentifier);
		long endTime = System.currentTimeMillis();
		int dureeMoynne = (int) ((endTime - startTime) / NB_XP);
		System.out.println("Duree moyenne (codegen): " + dureeMoynne + " (ms)");
	}

	private static void codeGeneration(IRUAST mergedTree, BlocsIdentifier blocsIdentifier) {

		List<Integer> toGen = new ArrayList<>();
		IExporter codegenerator = new FeatureCodeExporter(GENERATION_PATH,
				blocsIdentifier.getDependanciesManager(), toGen);
		codegenerator.generateMaximalCode();

		codegenerator.export(mergedTree);
	}

	
}
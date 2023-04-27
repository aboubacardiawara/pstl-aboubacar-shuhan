package main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import main.adaptation.JDTtoRUASTAdapter;
import main.adaptation.interfaces.IRUAST;
import main.exporter.IExporter;
import main.exporter.implem.DotExporter;
import main.exporter.implem.DoteExportWithColor;
import main.fusion.Merger;
import main.identificationblocs.BlocsIdentifier;

public class Main {
	private static int VARIANT_ID = 1;
	private String exportFile = null;

	private static List<IRUAST> sequentialAdaptation(List<String> filesPath) {
		return filesPath.stream()
				.map(path -> new JDTtoRUASTAdapter(VARIANT_ID++).adapt(path))
				.collect(Collectors.toList());
	}

	private static List<IRUAST> paralleleAdaptation(List<String> filesPath) {
		return filesPath.stream()
				.map(path -> new JDTtoRUASTAdapter(VARIANT_ID++).adapt(path))
				.collect(Collectors.toList());
	}

	private static void exampleSequential() {
		List<String> filesPath = project();

		List<IRUAST> ruasts = sequentialAdaptation(filesPath);

		IRUAST mergedTree = ruasts.stream().reduce(
				ruasts.get(0),
				(ruast1, ruast2) -> new Merger().merge(ruast1, ruast2));

		new BlocsIdentifier().findBlocs(mergedTree);

		Finder finder = new Finder(mergedTree);
		List<IRUAST> res = finder.findByBloc(6);
		res.forEach(ruast -> System.out.println(ruast.getName()));
	}

	private static void exampleParallele() {
		List<String> filesPath = project();

		long startTime = System.currentTimeMillis();
		List<IRUAST> ruasts = paralleleAdaptation(filesPath);
		long endTime = System.currentTimeMillis();
		System.out.println("Duration (adaptation): " + (endTime - startTime) + " (ms)");

		IRUAST mergedTree = ruasts.stream().reduce(
				ruasts.get(0),
				(ruast1, ruast2) -> new Merger().merge(ruast1, ruast2));

		new BlocsIdentifier().findBlocs(mergedTree);
		IExporter exporter = new DoteExportWithColor("exported/banques.dot");
		exporter.export(mergedTree);
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
		filesPath.add(notePad + "/Notepad-Full");
		filesPath.add(notePad + "/Notepad-Undo-Redo");
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
		return filesPath;
	}

	private static List<String> banques() {
		List<String> filesPath = new ArrayList<>();
		for (int i = 1; i <= 4; i++) {
			String variantPath = "./bank-variants/Variant0000" + (i) + "/";
			filesPath.add(variantPath);
		}
		return filesPath;
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		exampleParallele();
		long endTime = System.currentTimeMillis();
		System.out.println("Duration(parallele): " + (endTime - startTime) + " (ms)");

	}

}
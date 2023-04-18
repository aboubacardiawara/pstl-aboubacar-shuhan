package main;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import main.adaptation.JDTtoRUASTAdapter;
import main.adaptation.interfaces.IRUAST;
import main.fusion.Merger;
import main.identificationblocs.BlocsIdentifier;

public class Main {
	private static int VARIANT_ID = 1;

	private static void lightExample() {
		List<String> filesPath = argouml();

		List<IRUAST> ruasts = filesPath.stream()
				.map(path -> new JDTtoRUASTAdapter(VARIANT_ID++).adapt(path))
				.collect(Collectors.toList());

		IRUAST mergedTree = ruasts.stream().reduce(
				ruasts.get(0),
				(ruast1, ruast2) -> new Merger().merge(ruast1, ruast2));

		new BlocsIdentifier().findBlocs(mergedTree);

		System.out.println("taille: " + mergedTree.size());
	}

	private static List<String> notepad() {
		String notePad = "C:/Users/aboub_bmdb7gr/Downloads/Variant-Notepad";
		List<String> filesPath = new ArrayList<>();
		filesPath.add(notePad + "/Notepad-Find");
		filesPath.add(notePad + "/Notepad-Full");
		filesPath.add(notePad + "/Notepad-Cut");
		filesPath.add(notePad + "/Notepad-Copy");
		filesPath.add(notePad + "/Notepad-Cut-Find");
		filesPath.add(notePad + "/Notepad-Undo-Redo");

		return filesPath;
	}

	private static List<String> argouml() {
		List<String> filesPath = new ArrayList<>();
		for (int i = 1; i <= 2; i++) {
			String variantPath = "D:/cours/sorbonne/master/m1/s6/pstl/argouml/argouml/Variant000" + (i) + "/";
			filesPath.add(variantPath);
		}
		return filesPath;
	}

	private static List<String> banques() {
		List<String> filesPath = new ArrayList<>();
		for (int i = 1; i <= 8; i++) {
			String variantPath = "./bank-variants/Variant0000" + (i) + "/";
			filesPath.add(variantPath);
		}
		return filesPath;
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		lightExample();
		long endTime = System.currentTimeMillis();
		System.out.println("Duration: " + (endTime-startTime) +" (ms)");
	}

}
package main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import main.adaptation.JDTtoRUASTAdapter;
import main.adaptation.interfaces.IRUAST;
import main.fusion.Merger;
import main.identificationblocs.BlocsIdentifier;

public class Main {
	private static int VARIANT_ID = 1;

	private static void lightExample() {
		List<String> filesPath = new ArrayList<>();
		for (int i = 1; i <= 8; i++) {
			String variantPath = "./bank-variants/Variant0000" + (i) + "/";
			filesPath.add(variantPath);
		}

		List<IRUAST> ruasts = filesPath.stream()
				.map(path -> new JDTtoRUASTAdapter(VARIANT_ID++).adapt(path))
				.collect(Collectors.toList());

		IRUAST mergedTree = ruasts.stream().reduce(
				ruasts.get(0),
				(ruast1, ruast2) -> new Merger().merge(ruast1, ruast2));

		new BlocsIdentifier().findBlocs(mergedTree);

		System.out.println("taille: " + mergedTree.size());
	}

	public static void main(String[] args) {
		lightExample();
	}

}
package main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import main.adaptation.JDTtoRUASTAdapter;
import main.adaptation.VariantsSet;
import main.adaptation.interfaces.IRUAST;
import main.fusion.Merger;

public class Main {
	private static int VARIANT_ID = 1;

	private static void lightExample() {
		List<String> filesPath = new ArrayList<>();
		for (int i = 1; i <= 9; i++) {
			String variantPath = "./bank-variants/Variant0000" + (i) + "/";
			filesPath.add(variantPath);
		}

		List<IRUAST> ruasts = filesPath.stream()
				.map(path -> new JDTtoRUASTAdapter(VARIANT_ID++).adapt(path))
				.collect(Collectors.toList());

		IRUAST mergedTree = ruasts.stream().reduce(
				ruasts.get(0),
				(ruast1, ruast2) -> new Merger().merge(ruast1, ruast2));

		System.out.println(mergedTree.getChildren().get(0).getChildren().size());
	}

	private static void exempleVariant() {
		VariantsSet v = new VariantsSet();
		v.add(2);
		System.out.println(v);
	}

	public static void main(String[] args) {
		lightExample();
	}

}
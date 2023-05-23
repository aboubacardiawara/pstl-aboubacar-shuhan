package com.sorbonne.pstl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sorbonne.pstl.adaptation.JDTtoRUASTAdapter;
import com.sorbonne.pstl.exporter.IExporter;
import com.sorbonne.pstl.exporter.implem.codeGeneration.blocCodeGenerator.FeatureCodeExporter;
import com.sorbonne.pstl.fusion.Merger;
import com.sorbonne.pstl.identificationblocs.BlocsIdentifier;
import com.sorbonne.pstl.ruast.interfaces.IRUAST;


public class App {
	private static int VARIANT_ID = 1;
	private static String GENERATION_PATH;
	private static String CONFIG_FILE = "configuration.json";

	private static void featuresExtraction(List<String> variantsPaths) {
		
		// 1. ETAPE D'ADAPTATION DES ASTS EN RUASTS
		List<IRUAST> ruasts = 
			variantsPaths
			.stream()
			.map(path -> new JDTtoRUASTAdapter(VARIANT_ID++).adapt(path))
			.collect(Collectors.toList());

		// 2. ETAPE DE FUSION DES RUASTS
		IRUAST mergedTree = ruasts.subList(1, ruasts.size()).stream().reduce(
				ruasts.get(0),
				(ruast1, ruast2) -> new Merger().merge(ruast1, ruast2));

		BlocsIdentifier blocsIdentifier = new BlocsIdentifier();
		blocsIdentifier.findBlocs(mergedTree);

		// 3. ETAPE D'IDENTIFICATION DES BLOCS ET DES CONTRAINTES
		Finder finder = new Finder(mergedTree);
		finder.findByBloc(19).forEach(ruast -> {
			System.out.println(ruast.getRoot());
		});

		// 4. ETAPE DE GENERATION DU CODE
		List<Integer> toGen = new ArrayList<>();
		IExporter codegenerator = new FeatureCodeExporter(GENERATION_PATH,
				blocsIdentifier.getDependanciesManager(), toGen);
		codegenerator.generateMaximalCode();
		codegenerator.export(mergedTree);
		
	}

	
	public static void main(String[] args) {
		featuresExtraction(variantsPaths)
	}

}
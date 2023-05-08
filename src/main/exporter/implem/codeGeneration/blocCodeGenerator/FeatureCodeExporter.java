package main.exporter.implem.codeGeneration.blocCodeGenerator;
import java.util.List;

import main.adaptation.interfaces.IRUAST;
import main.exporter.implem.codeGeneration.JAVACodeExporter;
import main.identificationblocs.DependanciesManager;

public class FeatureCodeExporter extends JAVACodeExporter {

    protected List<Integer> blocToGenerate;
    private DependanciesManager dependanciesManager;

    public FeatureCodeExporter(String folderPath, DependanciesManager dependanciesManager, List<Integer> blocs) {
        super(folderPath);
        this.blocToGenerate = blocs;
        this.dependanciesManager = dependanciesManager;
    }

    /**
     * Verifie si le code asscoié à un noeud soit etre genere.
     * C'est le cas son le bloc correspond à la fonctionnalité à generer.
     * Ou encore si la fonctionnalité à generer en depend.
     * @param node
     * @return {boolean}
     */
    @Override
    protected boolean shouldBeGenerated(IRUAST node) {
        if (shoulGenAllFeatures) return true;
        
        int nodeBloc = node.getRoot().getBlock();
        return this.blocToGenerate.contains(nodeBloc)
         || blocToGenerate.stream().anyMatch(
            bloc -> this.dependanciesManager.areDependant(bloc, nodeBloc));
    }

    @Override
    public void generateMaximalCode() {
        this.shoulGenAllFeatures = true;
    }
}

package main.exporter.implem.codeGeneration.blocCodeGenerator;
import main.adaptation.interfaces.IRUAST;
import main.exporter.implem.codeGeneration.JAVACodeExporter;
import main.identificationblocs.DependanciesManager;

public class FeatureCodeExporter extends JAVACodeExporter {

    protected int blocToGenerate;
    private DependanciesManager dependanciesManager;

    public FeatureCodeExporter(String folderPath, DependanciesManager dependanciesManager, int bloc) {
        super(folderPath);
        this.blocToGenerate = bloc;
        this.dependanciesManager = dependanciesManager;
    }

    /**
     * Verifie si le code asscoié à un noeud soit etre genere.
     * C'est le cas son le bloc correspond à la fonctionnalité à generer.
     * Ou encore si la fonctionnalité à generer en depend.
     * @param node
     * @return {boolean}
     */
    protected boolean shouldBeGenerated(IRUAST node) {
        int nodeBloc = node.getRoot().getBlock();
        return this.blocToGenerate == nodeBloc
         || this.dependanciesManager.areDependant(blocToGenerate, nodeBloc);
    }

}

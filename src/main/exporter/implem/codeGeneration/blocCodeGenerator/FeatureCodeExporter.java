package main.exporter.implem.codeGeneration.blocCodeGenerator;
import main.adaptation.interfaces.IRUAST;
import main.exporter.implem.codeGeneration.JAVACodeExporter;

public class FeatureCodeExporter extends JAVACodeExporter {

    protected int blocToGenerate;

    public FeatureCodeExporter(String folderPath, int bloc) {
        super(folderPath);
        this.blocToGenerate = bloc;
    }

    /**
     * Verifie si le code asscoié à un noeud soit etre genere.
     * C'est le cas son le bloc correspond à la fonctionnalité à generer.
     * Ou encore si la fonctionnalité à generer en depend.
     * @param node
     * @return {boolean}
     */
    protected boolean shouldBeGenerated(IRUAST node) {
        return this.blocToGenerate == node.getRoot().getBlock();
    }

}

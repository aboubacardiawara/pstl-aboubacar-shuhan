package com.sorbonne.pstl.exporter.implem.codegenerator.blocCodeGenerator;

import java.util.List;

import com.sorbonne.pstl.exporter.implem.codegenerator.JAVACodeExporter;
import com.sorbonne.pstl.identificationblocs.DependanciesManager;
import com.sorbonne.pstl.ruast.interfaces.IRUAST;

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
     * 
     * @param node
     * @return {boolean}
     */
    @Override
    protected boolean shouldBeGenerated(IRUAST node) {
        if (shoulGenAllFeatures)
            return true;

        int nodeBloc = node.getRoot().getBlock();
        boolean res = this.blocToGenerate.contains(nodeBloc)
                || (currentBlocDependToBlocsToGenerate(nodeBloc)
                        && currentBlocIsNotMutuallyExclusiveToBlocsToGenerate(nodeBloc));
        return res;
    }

    /**
     * Verifie si le bloc courant n'est pas mutuellement exclusif avec
     * au moins un des blocs à generer.
     * 
     * @param nodeBloc
     * @return
     */
    private boolean currentBlocIsNotMutuallyExclusiveToBlocsToGenerate(int nodeBloc) {
        return blocToGenerate.stream().noneMatch(
                bloc -> this.dependanciesManager.areMutex(bloc, nodeBloc));
    }

    private boolean currentBlocDependToBlocsToGenerate(int nodeBloc) {
        return blocToGenerate.stream().anyMatch(
                bloc -> this.dependanciesManager.areDependant(bloc, nodeBloc));
    }

    @Override
    public void generateMaximalCode() {
        this.shoulGenAllFeatures = true;
    }
}

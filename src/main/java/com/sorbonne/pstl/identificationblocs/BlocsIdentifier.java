package com.sorbonne.pstl.identificationblocs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.sorbonne.pstl.ruast.interfaces.IRUAST;


public class BlocsIdentifier {

    private Integer id = 0;
    private DependanciesManager dependanciesManager;
    private IRUAST ruast;


    public BlocsIdentifier() {
        this.dependanciesManager = new DependanciesManager();
    }


    private Integer getNextIdForBloc() {
        return id++;
    }

    public IRUAST findBlocs(IRUAST ruast) {
        Map<Set<Integer>, Integer> env = new HashMap<>();
        this.ruast = ruast;
        findBlocsAux(ruast, env);
        // env sera exploité pour definir les relation entre les blocs
        this.dependanciesManager.resolveDependancies(env);
        return ruast;
    }

    protected void findBlocsAux(IRUAST ruast, Map<Set<Integer>, Integer> env) {
        Set<Integer> currenctVariant = ruast.getVariants();
        if (!env.containsKey(currenctVariant)) {
            Integer newBloc = getNextIdForBloc();
            env.put(currenctVariant, newBloc);
        }
        int bp = env.get(currenctVariant);
        ruast.getRoot().setBlock(bp);

        for (IRUAST child : ruast.getChildren()) {
            findBlocsAux(child, env);
        }
    }

    public DependanciesManager getDependanciesManager() {
        return this.dependanciesManager;
    }
    
    public IRUAST getRuast() {
        return this.ruast;
    }

}

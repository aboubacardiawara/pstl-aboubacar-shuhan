package main.identificationblocs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import main.adaptation.interfaces.IRUAST;

public class BlocsIdentifier {

    private Integer id = 0;
    private DependanciesManager dependanciesManager;


    public BlocsIdentifier() {
        this.dependanciesManager = new DependanciesManager();
    }


    private Integer getNextIdForBloc() {
        return id++;
    }

    public IRUAST findBlocs(IRUAST ruast) {
        Map<Set<Integer>, Integer> env = new HashMap<>();
        findBlocsAux(ruast, env);
        System.out.println("Features[" + env.size() + "]: " + env);
        System.out.println(this.dependanciesManager.getRelations());
        return ruast;
    }

    protected int findBlocsAux(IRUAST ruast, Map<Set<Integer>, Integer> env) {
        Set<Integer> currenctVariant = ruast.getVariants();
        if (!env.containsKey(currenctVariant)) {
            Integer newBloc = getNextIdForBloc();
            // dependancies manager
            this.dependanciesManager.newBloc(newBloc);
            env.put(currenctVariant, newBloc);
        }

        // si le bloc (bf) est different de celui (bp) du parent , 
        // alors bf depend de bp.
        int bp = env.get(currenctVariant);

        ruast.getRoot().setBlock(bp);

        for (IRUAST child : ruast.getChildren()) {
            int bf = findBlocsAux(child, env);
            if (bp != bf) {
                this.dependanciesManager.addDependancieRelation(bf, bp);
            }
        }
        return bp;
    }

    public DependanciesManager getDependanciesManager() {
        return this.dependanciesManager;
    }

}

package main.identificationblocs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import main.adaptation.interfaces.IRUAST;

public class BlocsIdentifier {

    private Integer id = 1;

    private Integer getNextIdForBloc() {
        return id++;
    }

    public IRUAST findBlocs(IRUAST ruast) {
        Map<Set<Integer>, Integer> env = new HashMap<>();
        findBlocsAux(ruast, env);
        System.out.println("Features[" + env.size() + "]: " + env);
        return ruast;
    }

    private void findBlocsAux(IRUAST ruast, Map<Set<Integer>, Integer> env) {
        Set<Integer> currenctVariant = ruast.getVariants();
        if (!env.containsKey(currenctVariant)) {
            env.put(currenctVariant, getNextIdForBloc());
        }
        ruast.getRoot().setBlock(env.get(currenctVariant));

        for (IRUAST child : ruast.getChildren()) {
            findBlocsAux(child, env);
        }
    }

}

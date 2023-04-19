package main;

import java.util.ArrayList;
import java.util.List;

import main.adaptation.interfaces.IRUAST;

public class Finder {

    private IRUAST ruast;

    public Finder(IRUAST ruast) {
        this.ruast = ruast;
    }


    public List<IRUAST> findByBloc(int blocId) {
        List<IRUAST> matched = new ArrayList<>();
        findByBlocAux(this.ruast, blocId, matched);
        return matched;
    }


    private List<IRUAST> findByBlocAux(IRUAST tree, int blocId, List<IRUAST> collectedTree) {
        if (tree.getRoot().getBlock() == blocId) {
            collectedTree.add(tree);
        }

        for (IRUAST child: tree.getChildren()) {
            findByBlocAux(child, blocId, collectedTree);
        }
        return collectedTree;
    }
    
}

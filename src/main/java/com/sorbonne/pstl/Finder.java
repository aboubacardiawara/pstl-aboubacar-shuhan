package com.sorbonne.pstl;

import java.util.ArrayList;
import java.util.List;

import com.sorbonne.pstl.ruast.impl.RUASTNodeType;
import com.sorbonne.pstl.ruast.interfaces.IRUAST;


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

    public List<IRUAST> findNodeByNameAndType(String name, RUASTNodeType type) {
        List<IRUAST> matched = new ArrayList<>();
        return findNodeByNameAndTypeAux(this.ruast, matched, name, type);
    }

    private List<IRUAST> findNodeByNameAndTypeAux(IRUAST tree, List<IRUAST> matched, String name,
            RUASTNodeType type) {
        if (tree.getName().equals(name)) {
            if (type != null && tree.getRoot().getType() == type) {
                matched.add(tree);
            } else {
                matched.add(tree);
            }
        }
        for (IRUAST child : tree.getChildren()) {
            findNodeByNameAndTypeAux(child, matched, name, type);
        }
        return matched;
    }

    private List<IRUAST> findByBlocAux(IRUAST tree, int blocId, List<IRUAST> collectedTree) {
        if (tree.getRoot().getBlock() == blocId) {
            collectedTree.add(tree);
        }

        for (IRUAST child : tree.getChildren()) {
            findByBlocAux(child, blocId, collectedTree);
        }
        return collectedTree;
    }

}

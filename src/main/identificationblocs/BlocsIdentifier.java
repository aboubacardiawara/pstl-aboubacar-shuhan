package main.identificationblocs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import main.adaptation.interfaces.IRUAST;

public class BlocsIdentifier {

    private Integer id = 1;

    /*
     * 
     * /*
     * Prend un arbre RUAST et identifie les noeuds auxquels les blocs
     * appartiennent.
     * 
     * @param ruast
     * 
     * @return
     * /
     * public IRUAST process(IRUAST ruast) {
     * HashMap<VariantsSet, Integer> block_infos = new HashMap<>();
     * IRUASTNode root = ruast.getRoot();
     * aux_node(ruast, block_infos, root);
     * return ruast;
     * }
     * 
     * private void aux_node(IRUAST ruast, HashMap<VariantsSet, Integer>
     * block_infos, IRUASTNode node) {
     * VariantsSet block = node.getVariants();
     * if (block_infos.containsKey(block)) {
     * int id_block = block_infos.get(block);
     * node.setBlock(id_block);
     * } else {
     * block_infos.put(block, getNextIdForBloc());
     * node.setBlock(getNextIdForBloc());
     * for (VariantsSet existing_block : block_infos.keySet()) {
     * // dependence
     * if (subset(block, existing_block)) {
     * 
     * }
     * // mutex
     * if (intersection(block, existing_block).isEmpty()) {
     * 
     * }
     * }
     * }
     * for (IRUAST child : ruast.getChildren()) {
     * aux_node(child, block_infos, child.getRoot());
     * }
     * 
     * }
     * 
     * 
     * private boolean subset(VariantsSet a, VariantsSet b) {
     * return b.containsAll(a);
     * }
     * 
     * private VariantsSet intersection(VariantsSet a, VariantsSet b) {
     * VariantsSet result = new VariantsSet();
     * for (String variant : a) {
     * if (b.contains(variant)) {
     * result.add(variant);
     * }
     * }
     * return result;
     * }
     */

    private Integer getNextIdForBloc() {
        return id++;
    }

    public IRUAST findBlocs(IRUAST ruast) {
        Map<Set<Integer>, Integer> env = new HashMap<>();
        findBlocsAux(ruast, env);
        System.out.println("blocs: " + env);
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

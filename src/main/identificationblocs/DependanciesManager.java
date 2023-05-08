package main.identificationblocs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DependanciesManager implements IDependanciesManager {

    private List<Set<Integer>> relations = new ArrayList<>();

    public void newBloc(int bloc) {
        // bloc 1 is at position 2
        // when adding bloc 2, relation size should be 1
        assert this.relations.size() == bloc: "expanding relations size";
        this.relations.add(new HashSet<>(bloc)); 
    }

    /**
     * Verifie si deux blocs sont dependants
     */
    @Override
    public boolean areDependant(int bloc1, int bloc2) {
        return this.relations.get(bloc1).contains(bloc2);
    }

    @Override
    public boolean areMutex(int bloc1, int bloc2) {
        return false;
    }

    /**
     * bloc2 depend du bloc 1.
     * Cela dit, pour genere bloc 2, il faut generer bloc 1.
     */
    @Override
    public void addDependancieRelation(int bloc1, int bloc2) {
        this.relations.get(bloc1).add(bloc2);
    }

    @Override
    public void addMutexRelation(int bloc1, int bloc2) {
        throw new UnsupportedOperationException("Unimplemented method 'addDependancieRelation'");
    }

    public  List<Set<Integer>> getRelations() {
        return this.relations;
    }


}

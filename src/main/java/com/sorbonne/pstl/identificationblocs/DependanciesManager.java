package com.sorbonne.pstl.identificationblocs;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DependanciesManager implements IDependanciesManager {

    private static final int ROOT_BLOC = -2;
    private Map<Integer, Set<Integer>> dependencyRelations = new HashMap<>();
    private Map<Integer, Set<Integer>> mutexRelations = new HashMap<>();

   
    /**
     * Verifie si deux blocs sont dependants
     */
    @Override
    public boolean areDependant(int bloc1, int bloc2) {
        if (bloc1 == bloc2) return true;
        return this.dependencyRelations.get(bloc1).contains(bloc2);
    }
    
    /**
     * Verifie si deux blocs sont mutux
     */
    @Override
    public boolean areMutex(int bloc1, int bloc2) {
        return this.mutexRelations.get(bloc1).contains(bloc2);
    }

    /**
     * Nous disposons d'une association entre les variants vers des blocs.
     * - Un bloc b1 depend d'un bloc b2 si b1 et b2 si s1 est inclus dans s2
     *  s1 etant l'ensemble des variantes de b1 et s2 l'ensemble des variantes de b2
     * - Un bloc b1 est mutuellement exclusif avec un bloc b2 si b1 et b2 sont disjoints
     *  
     * @param env
     */
    public void resolveDependancies(Map<Set<Integer>, Integer> env) {
        int nbBlocs = env.values().stream().max(Integer::compare).get() + 1;
        for (int i = 0; i < nbBlocs; i++) {
            this.dependencyRelations.put(i, new HashSet<>());
            this.mutexRelations.put(i, new HashSet<>());
        }
        for (Set<Integer> variants1 : env.keySet()) {
            for (Set<Integer> variants2 : env.keySet()) {
                if (variants1 != variants2) {
                    int bloc1 = env.get(variants1);
                    int bloc2 = env.get(variants2);

                    // bloc1 depend de bloc2 ?
                    if (variants2.containsAll(variants1)) {
                        addDependancy(bloc1, bloc2);
                    }
                    // bloc1 et 2 sont mutex ?
                    if (variants1.stream().noneMatch(variants2::contains)) {
                        addMutex(bloc1, bloc2);
                    }
                } 
            }
        }
    }

    private void addMutex(int bloc1, int bloc2) {
        this.mutexRelations.get(bloc1).add(bloc2);
    }

    private void addDependancy(int bloc1, int bloc2) {
        this.dependencyRelations.get(bloc1).add(bloc2);
    }

    // getters
    public Map<Integer, Set<Integer>> getDependencyRelations() {
        return this.dependencyRelations;
    }

    public Map<Integer, Set<Integer>> getMutexRelations() {
        return this.mutexRelations;
    }

    public int getBlocsCount() {
        return this.dependencyRelations.size();
    }

    public Map<Integer, Set<Integer>> getDependancies() {
        return dependencyRelations;
    }

    @Override
    public int getParentOf(int bloc) {
        if (this.dependencyRelations.get(bloc).size() == 0) 
            return ROOT_BLOC;
        return this.dependencyRelations.get(bloc).stream().findFirst().get();
    }

    @Override
    public int blocsCount() {
        return this.dependencyRelations.size();
    }

    @Override
    public Collection<Integer> getmutexOf(int bloc1) {
        return this.mutexRelations.get(bloc1);
    }
    
    @Override
    public Collection<Integer> getDependanciesOf(int bloc1) {
        return this.dependencyRelations.get(bloc1);
    }
}

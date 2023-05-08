package main.identificationblocs;

public interface IDependanciesManager {

    public void addDependancieRelation(int bloc1, int bloc2);

    public void addMutexRelation(int bloc1, int bloc2);

    public boolean areDependant(int bloc1, int bloc2);

    public boolean areMutex(int bloc1, int bloc2);

}

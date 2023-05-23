package com.sorbonne.pstl.identificationblocs;

public interface IDependanciesManager {

    public boolean areDependant(int bloc1, int bloc2);

    public boolean areMutex(int bloc1, int bloc2);

}

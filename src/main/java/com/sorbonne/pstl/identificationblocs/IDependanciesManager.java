package com.sorbonne.pstl.identificationblocs;

import java.util.Collection;

public interface IDependanciesManager {

    public boolean areDependant(int bloc1, int bloc2);

    public boolean areMutex(int bloc1, int bloc2);

    public int getParentOf(int bloc);

    public int blocsCount();

    public Collection<Integer> getmutexOf(int bloc1);

    public Collection getDependanciesOf(int bloc1);

}

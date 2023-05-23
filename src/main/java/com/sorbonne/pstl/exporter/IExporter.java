package com.sorbonne.pstl.exporter;

import com.sorbonne.pstl.ruast.interfaces.IRUAST;

public interface IExporter {
    public void export(IRUAST ruast);

    public void generateMaximalCode();
}

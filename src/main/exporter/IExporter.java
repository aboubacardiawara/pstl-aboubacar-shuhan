package main.exporter;

import main.ruast.interfaces.IRUAST;

public interface IExporter {
    public void export(IRUAST ruast);

    public void generateMaximalCode();
}

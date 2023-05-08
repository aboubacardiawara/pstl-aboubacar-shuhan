package main.exporter;

import main.adaptation.interfaces.IRUAST;

public interface IExporter {
    public void export(IRUAST ruast);

    public void generateMaximalCode();
}

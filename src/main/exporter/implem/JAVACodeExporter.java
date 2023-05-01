package main.exporter.implem;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.runtime.Path;

import main.adaptation.interfaces.IRUAST;
import main.exporter.IExporter;

public class JAVACodeExporter implements IExporter {

    protected String folderPath;

    public JAVACodeExporter(String folderPath) {
        super();
        this.folderPath = folderPath;
    }

    @Override
    public void export(IRUAST ruast) {
        createOnlyClassFiles();
    }

    /**
     * 
     */
    private void createOnlyClassFiles() {
        createFolder();

    }

    private void createFolder() {
        try {
            java.nio.file.Path path = Paths.get(this.folderPath);
            Files.createDirectory(path);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

}

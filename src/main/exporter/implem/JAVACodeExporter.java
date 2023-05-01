package main.exporter.implem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.core.internal.resources.Folder;
import java.nio.file.Path;

import main.adaptation.RUASTNodeType;
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
        createOnlyClassFiles(ruast);
    }

    /**
     * 
     */
    private void createOnlyClassFiles(IRUAST ruast) {
        createFolder();
        createFileFromRUAST(ruast.getChildren().get(0));
    }

    private void createFileFromRUAST(IRUAST ruast) {
        assert ruast.getRoot().getType() == RUASTNodeType.TYPE_DEFINITION : "Should be a type definition";
        String fileName = "RandomJavaClass.java";
        Path filePath = Paths.get(this.folderPath + "/" + fileName);
        try {
            Files.createFile(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create folder and juste files
     */
    private void createFolder() {
        try {
            Path path = Paths.get(this.folderPath);
            Files.createDirectory(path);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

}

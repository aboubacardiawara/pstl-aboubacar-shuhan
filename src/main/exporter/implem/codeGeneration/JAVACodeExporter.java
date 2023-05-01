package main.exporter.implem.codeGeneration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.core.internal.resources.Folder;
import java.nio.file.Path;

import main.adaptation.RUASTNodeType;
import main.adaptation.interfaces.IRUAST;
import main.exporter.IExporter;
import main.util.Utile;

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
        ruast.getChildren().forEach(
                child -> createFileFromRUAST(child));
    }

    private void createFileFromRUAST(IRUAST ruast) {
        assert ruast.getRoot().getType() == RUASTNodeType.TYPE_DEFINITION : "Should be a type definition";
        String fileName = Utile.buildClassName(ruast) + ".java";
        Path filePath = Paths.get(this.folderPath + "/" + fileName);
        try {
            Files.deleteIfExists(filePath);
            Files.createFile(filePath);
            writeSourceCode(filePath, ruast);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeSourceCode(Path filePath, IRUAST ruast) throws IOException {
        Writer writer = new FileWriter(filePath.toString());
        String javaCode = CodeBuilderFromRUAST.buildFrom(ruast);
        writer.write(javaCode);
        writer.close();
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

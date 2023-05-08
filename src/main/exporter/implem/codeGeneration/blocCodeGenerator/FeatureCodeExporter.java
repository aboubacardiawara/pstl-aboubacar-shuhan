package main.exporter.implem.codeGeneration.blocCodeGenerator;

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
import main.exporter.implem.codeGeneration.CodeBuilderFromRUAST;
import main.exporter.implem.codeGeneration.JAVACodeExporter;
import main.util.Utile;

public class FeatureCodeExporter extends JAVACodeExporter {

    protected int blocToGenerate;

    public FeatureCodeExporter(String folderPath, int bloc) {
        super(folderPath);
        this.blocToGenerate = bloc;
    }


    protected void writeSourceCode(Path filePath, IRUAST ruast) throws IOException {
        
    }


}

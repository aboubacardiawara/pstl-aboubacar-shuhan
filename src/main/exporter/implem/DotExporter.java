package main.exporter.implem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import main.adaptation.interfaces.IRUAST;
import main.adaptation.interfaces.IRUASTNode;
import main.exporter.IExporter;

public class DotExporter implements IExporter {

    protected static String DEFAULT_PATH = "exported/RUAST.dot";
    protected String filePath;

    public DotExporter(String filePath) {
        super();
        this.filePath = filePath;
    }

    public DotExporter() {
        super();
    }

    public String getFilePath() {
        if (filePath == null) {
            return DEFAULT_PATH;
        }
        return filePath;
    }

    @Override
    public void export(IRUAST ruast) {
        try {
            Writer writer = new FileWriter(getFilePath());
            writer.write(ruastToString(ruast));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String ruastToString(IRUAST ruast) {
        StringBuilder sb = new StringBuilder();

        // Entête du fichier DOT
        sb.append("digraph RUAST {\n");
        sb.append("  node [shape=box];\n");

        // Parcours récursif de l'arbre
        buildDot(ruast, sb);

        // Fermeture du fichier DOT
        sb.append("}");

        return sb.toString();
    }

    protected void buildDot(IRUAST ruast, StringBuilder sb) {
        // Écriture du noeud courant
        String nodeName = escapeQuotes(ruast.getName());
        sb.append("  \"").append(nodeName).append("\"");
        sb.append(" [label=\"").append(nodeName).append("\"];\n");

        // Parcours des enfants du noeud courant
        for (IRUAST child : ruast.getChildren()) {
            // Écriture de l'arc entre le noeud courant et son enfant
            String childName = escapeQuotes(child.getName());
            sb.append("  \"").append(nodeName).append("\"");
            sb.append(" -> ");
            sb.append("\"").append(childName).append("\"");
            sb.append(";\n");

            // Appel récursif sur l'enfant courant
            buildDot(child, sb);
        }
    }

    protected String escapeQuotes(String str) {
        return str.replace("\"", "\\\"");
    }

}

package main.exporter.implem;

import main.adaptation.interfaces.IRUAST;

public class DoteExportWithColor
        extends DotExporter {
    public DoteExportWithColor(String filePath) {
        super(filePath);
    }

    @Override
    protected void buildDot(IRUAST ruast, StringBuilder sb) {
        // Écriture du noeud courant
        String nodeName = escapeQuotes(ruast.getName());
        String blockColor = getBlockColor(ruast.getRoot().getBlock()); // Récupère la couleur du bloc
        sb.append("  \"").append(nodeName).append("\"");
        sb.append(" [label=\"").append(nodeName).append("\", color=\"").append(blockColor).append("\"];\n");

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

    protected String getBlockColor(Integer block) {
        // Retourne une couleur différente pour chaque bloc
        String[] colors = new String[]{
            "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", "#80b1d3", "#fdb462", "#b3de69", "#fccde5", "#d9d9d9", "#bc80bd",
            "#ccebc5", "#ffed6f", "#1f78b4", "#33a02c", "#e31a1c", "#f03b20", "#fb9a99", "#e5c494", "#a6cee3", "#1f78b4",
            "#b2df8a", "#fdbf6f", "#cab2d6", "#6a3d9a", "#ff7f00", "#fb9a99", "#e41a1c", "#1b9e77", "#d95f02", "#7570b3",
            "#e7298a", "#66a61e", "#e6ab02", "#a6761d", "#666666", "#1b9e77", "#d95f02", "#7570b3", "#e7298a", "#66a61e",
            "#e6ab02", "#a6761d", "#666666", "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", "#fb9a99", "#e31a1c", "#fdbf6f",
            "#ff7f00", "#cab2d6", "#6a3d9a", "#ffff99", "#b15928"
        };

        return colors[block];
    }
}

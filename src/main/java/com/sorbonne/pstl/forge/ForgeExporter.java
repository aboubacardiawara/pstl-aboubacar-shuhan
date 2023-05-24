package com.sorbonne.pstl.forge;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sorbonne.pstl.exporter.IExporter;
import com.sorbonne.pstl.ruast.interfaces.IRUAST;
import com.sorbonne.pstl.identificationblocs.IDependanciesManager;

public class ForgeExporter {
    protected IDependanciesManager dependanciesManager;
    protected String path;

    public ForgeExporter(IDependanciesManager dependanciesManager, String path) {
        this.dependanciesManager = dependanciesManager;
        this.path = path + "/.mobioseforge/";
    }

    public void export(IRUAST ruast) {
        try {
            createForgeConfigFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
        writeFeatureMapFile();
        writeMapsFile();
    }

    public void createForgeConfigFiles() throws Exception {
        File folder = new File(path);

        // delete folder if it exists
        if (folder.exists()) {
            folder.delete();
        }

        // create folder
        folder.mkdir();

        // create two files fm.forge and maps.json
        File fmFile = new File(path + "fm.json");
        File mapsFile = new File(path + "maps.json");

        fmFile.createNewFile();
        mapsFile.createNewFile();
    }

    public void writeFeatureMapFile() {
        FMBuilder fmBuilder = new FMBuilder();
        fmBuilder.setDependeniesManager(dependanciesManager);
        JSONObject fmObject = fmBuilder.build();
    
        String fmFile = path + "fm.json";
    
        try (FileWriter writer = new FileWriter(fmFile)) {
            writer.write(fmObject.toString());
            System.out.println("JSONObject has been written to the file.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONArray buildFeaturesJsonArray() {
        JSONArray featuresArray = new JSONArray();
        for (int bloc = 0; bloc < dependanciesManager.blocsCount(); bloc++) {
            JSONObject featureObject = buildFeatureJsonObject(bloc);
            featuresArray.add(featureObject);
        }
        return featuresArray;

    }

    protected JSONObject buildCoreJsonObject() {
        JSONObject coreObject = new JSONObject();
        coreObject.put("key", "-2");
        coreObject.put("name", "Feature Model");
        coreObject.put("type", "Core");
        coreObject.put("parent", "-1");
        coreObject.put("parentRelation", "Normal");
        coreObject.put("presence", "Mandatory");
        coreObject.put("lgFile", "");
        coreObject.put("role", "");
        coreObject.put("hexColor", "#fff");
        coreObject.put("help", "");
        coreObject.put("nodeWeight", -1);

        return coreObject;
    }

    protected JSONObject buildFeatureJsonObject(int bloc) {
        JSONObject featureObject = new JSONObject();
        String name = "Bloc " + bloc;
        String presence = "Optional";
        int parentId = dependanciesManager.getParentOf(bloc);

        if (bloc == 0) {
            name = "Base";
            presence = "Manadatory";
        }
        featureObject.put("key", bloc);
        featureObject.put("name", name);
        featureObject.put("type", "Functionality feature");
        featureObject.put("parent", String.valueOf(parentId));
        featureObject.put("parentRelation", "Normal");
        featureObject.put("presence", presence);
        featureObject.put("lgFile", "");
        featureObject.put("role", "");
        featureObject.put("hexColor", "#ff2600");
        featureObject.put("help", "");

        return featureObject;
    }

    protected void writeMapsFile() {
    
    }
}
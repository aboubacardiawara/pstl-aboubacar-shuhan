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
    private IRUAST ruast;

    public ForgeExporter(IDependanciesManager dependanciesManager, String path) {
        this.dependanciesManager = dependanciesManager;
        this.path = path + "/.mobioos-forge/";
    }

    public void export(IRUAST ruast) {
        this.ruast = ruast;
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
        File fmFile = new File(path + "features.fm.forge");
        File mapsFile = new File(path + "features.maps.forge");

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


    protected void writeMapsFile() {
        MAPSBuilder mapsBuilder = new MAPSBuilder(dependanciesManager, ruast);
        JSONObject mapObject = mapsBuilder.build();

        String mapsFile = path + "maps.json";

        try (FileWriter writer = new FileWriter(mapsFile)) {
            writer.write(mapObject.toString());
            System.out.println("JSONObject has been written to the file.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
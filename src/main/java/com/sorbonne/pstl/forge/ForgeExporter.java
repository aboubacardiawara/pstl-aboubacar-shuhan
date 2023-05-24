package com.sorbonne.pstl.forge;

import java.io.File;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import com.sorbonne.pstl.exporter.IExporter;
import com.sorbonne.pstl.ruast.interfaces.IRUAST;
import com.sorbonne.pstl.identificationblocs.IDependanciesManager;

public class ForgeExporter
{
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
    /*
     "key":"-2",
      "name":"Feature Model",
      "type":"Core",
      "parent":"-1",
      "parentRelation":"Normal",
      "presence":"Mandatory",
      "lgFile":"",
      "role":"",
      "hexColor":"#fff",
      "help":"",
      "nodeWeight":-1
     */

    public void writeFeatureMapFile() {
        JSONObject fmObject = new JSONObject();
        JSONObject coreObject = buildCoreObject();

        fmObject.put("core", coreObject);
        System.out.println(fmObject);
    }

    protected JSONObject buildCoreObject() {
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
}
package com.sorbonne.pstl.forge;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sorbonne.pstl.identificationblocs.IDependanciesManager;
import com.sorbonne.pstl.ruast.interfaces.IRUAST;

public class MAPSBuilder {


    private IDependanciesManager dependanciesManager;


    public MAPSBuilder(IDependanciesManager dependanciesManager, IRUAST ruastMax) {
        this.dependanciesManager = dependanciesManager;
    }
    

    public JSONObject build() {
        JSONObject fmObject = new JSONObject();
        JSONArray deletionPropagations = deletionPropagationsObject();
        JSONArray replacementPropagations = replacementPropagationsObject();
        JSONArray ressourcePropagations = ressourcePropagationsObject();
        JSONArray pathPropagations = pathPropagationsObject();
        JSONArray fileRessourcesArray = new JSONArray();

        fmObject.put("deletionPropagations", deletionPropagations);
        fmObject.put("replacementPropagations", replacementPropagations);
        fmObject.put("ressourcePropagations", ressourcePropagations);
        fmObject.put("pathPropagations", pathPropagations);

        return fmObject;
    }


    private JSONArray pathPropagationsObject() {
        JSONArray pathPropagationsArray = new JSONArray();
        return pathPropagationsArray;
    }


    private JSONArray ressourcePropagationsObject() {
        JSONArray ressourcePropagationsArray = new JSONArray();
        return ressourcePropagationsArray;
    }


    private JSONArray deletionPropagationsObject() {
        JSONArray deletionPropagationsArray = new JSONArray();
        return deletionPropagationsArray;
    }


    private JSONArray replacementPropagationsObject() {
        JSONArray replacementPropagationsArray = new JSONArray();
        return replacementPropagationsArray;
    }
    
}

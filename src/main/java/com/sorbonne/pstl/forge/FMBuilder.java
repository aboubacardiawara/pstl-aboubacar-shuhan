package com.sorbonne.pstl.forge;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sorbonne.pstl.identificationblocs.IDependanciesManager;

/**
 * FMBuilder
 */
public class FMBuilder {

    private IDependanciesManager dependanciesManager;

    public void setDependeniesManager(IDependanciesManager dependanciesManager) {
        this.dependanciesManager = dependanciesManager;
    }

    public JSONObject build() {
        JSONObject fmObject = new JSONObject();
        JSONObject coreObject = buildCoreJsonObject();
        JSONArray constraintsArray = buildConstraintsJsonArray();
        JSONArray featuresArray = buildFeaturesJsonArray();
        JSONArray fileRessourcesArray = new JSONArray();
        JSONArray textRessourcesArray = new JSONArray();
        JSONArray smartAppAssetRessourcesArray = new JSONArray();
        JSONArray colorRessourcesArray = new JSONArray();
        JSONArray referencedModelsArray = new JSONArray();

        fmObject.put("core", coreObject);
        fmObject.put("features", featuresArray);
        fmObject.put("fileResources", fileRessourcesArray);
        fmObject.put("textResources", textRessourcesArray);
        fmObject.put("smartAppAssetResources", smartAppAssetRessourcesArray);
        fmObject.put("colorResources", colorRessourcesArray);
        fmObject.put("referencedModels", referencedModelsArray);
        fmObject.put("constraints", constraintsArray);

        return fmObject;
    }

    private JSONArray buildConstraintsJsonArray() {
        JSONArray constraintsArray = new JSONArray();

        // dependancies constraints
        for (int bloc = 0; bloc < dependanciesManager.blocsCount(); bloc++) {
            final int bloc1 = bloc;
            dependanciesManager.getDependanciesOf(bloc1)
            .stream()
            .forEach(bloc2 -> {
                Object constraintObject = buildDependancie(
                    String.valueOf(bloc1), 
                    String.valueOf(bloc2));
                constraintsArray.add(constraintObject);
            });
        }

        // mutex constraints
        for (int bloc = 0; bloc < dependanciesManager.blocsCount(); bloc++) {
            final int bloc1 = bloc;
            dependanciesManager.getmutexOf(bloc1)
            .stream()
            .forEach(bloc2 -> {
                Object constraintObject = buildMutex(
                    String.valueOf(bloc1), 
                    String.valueOf(bloc2));
                constraintsArray.add(constraintObject);
            });
        }

        return constraintsArray;
    }

    private Object buildConstraint(String string, String leftFeatureKey, String rightFeatureKey, boolean mutex) {
        JSONObject constraintObject = new JSONObject();
        constraintObject.put("type", string);
        JSONObject leftObject = new JSONObject();
        leftObject.put("type", "feature");
        leftObject.put("featureKey", leftFeatureKey);
        constraintObject.put("left", leftObject);

        JSONObject rightObject = new JSONObject();
        if (mutex) {
            /*
             "right":{
            "type":"¬",
            "child":{
               "type":"feature",
               "featureKey":"5"
            }
         }
             */
            JSONObject childObject = new JSONObject();
            childObject.put("type", "feature");
            childObject.put("featureKey", rightFeatureKey);
            rightObject.put("type", "¬");
            rightObject.put("child", childObject);
        } else {
            rightObject.put("type", "feature");
            rightObject.put("featureKey", rightFeatureKey);
        }
        constraintObject.put("right", rightObject);
        return constraintObject;
    }

    private Object buildDependancie(String leftFeatureKey, String rightFeatureKey) {
        return buildConstraint("⇒", leftFeatureKey, rightFeatureKey, false);
    }

    private Object buildMutex(String leftFeatureKey, String rightFeatureKey) {
        return buildConstraint("⇒", leftFeatureKey, rightFeatureKey, true);
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

}
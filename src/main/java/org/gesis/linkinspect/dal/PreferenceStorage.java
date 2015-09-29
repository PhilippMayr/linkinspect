package org.gesis.linkinspect.dal;

import java.util.prefs.Preferences;

/**
 * Class to manage access to system preferences.
 * Is used to store short recurrent information.
 */
public class PreferenceStorage {

    private static final String NODE = "org.gesis.linkinspect.preference.staticPreferenceLoader";
    private static final String SOURCE_SPARQL_KEY = "org.gesis.linkinspect.source";
    private static final String SOURCE_SPARQL_DEFAULT = "";
    private static final String TARGET_SPARQL_KEY = "org.gesis.linkinspect.target";
    private static final String TARGET_SPARQL_DEFAULT = "";
    

    private static PreferenceStorage instance = null;
    private Preferences prefsRoot = null;
    private Preferences myPrefs = null;

    private PreferenceStorage() {
        prefsRoot = Preferences.userRoot();
        myPrefs = prefsRoot.node(NODE);
    }

    public static PreferenceStorage getInstance() {
        if (instance == null) {
            instance = new PreferenceStorage();
        }
        return instance;
    }

    

    public String getSource(){
        return myPrefs.get(SOURCE_SPARQL_KEY, SOURCE_SPARQL_DEFAULT);
    }
    
    public void setSource(String value){
        myPrefs.put(SOURCE_SPARQL_KEY,value);
    }
    
    public String getTarget(){
        return myPrefs.get(TARGET_SPARQL_KEY, TARGET_SPARQL_DEFAULT);
    }
    
    public void setTarget(String value){
        myPrefs.put(TARGET_SPARQL_KEY,value);
    }
}

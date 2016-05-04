/*
 * Copyright (C) 2016 GESIS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, see 
 * http://www.gnu.org/licenses/ .
 */
package org.gesis.linkinspect.dal;

import java.util.prefs.Preferences;

/**
 * @author Felix Bensmann
 * Class to manage access to system preferences.
 * Is used to store short recurrent information.
 * Implements singleton.
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

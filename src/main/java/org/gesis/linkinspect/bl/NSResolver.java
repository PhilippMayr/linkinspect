/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.bl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bensmafx
 */
public class NSResolver {

    private static final String FILEPATH = "namespaces.txt";
    private static final String ABR_RDF = "rdf";
    private static final String FULL_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String ABR_RDFS = "rdfs";
    private static final String FULL_RDFS = "http://www.w3.org/2000/01/rdf-schema#";

    private static NSResolver instance = null;

    private ArrayList<KVP> nsList = null;

    private class KVP {

        public String shortName = null;
        public String longName = null;

        public KVP(String shortName, String longName) {
            this.shortName = shortName;
            this.longName = longName;
        }

    }

    private NSResolver() {
        nsList = new ArrayList<KVP>();
        nsList.add(new KVP(ABR_RDF, FULL_RDF));
        nsList.add(new KVP(ABR_RDFS, FULL_RDFS));

        File file = new File(FILEPATH);
        if (file.exists() && file.isFile()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                while (line != null) {
                    String[] keyValueString = line.split(" ");
                    nsList.add(new KVP(keyValueString[0].trim(), keyValueString[1].trim()));
                    line = reader.readLine();
                }
                reader.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(NSResolver.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(NSResolver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static NSResolver getInstance() {
        if (instance == null) {
            instance = new NSResolver();
        }
        return instance;
    }

    public boolean canBeShoretened(String longName) {
        for (KVP kvp : nsList) {
            if (longName.startsWith(kvp.longName)) {
                return true;
            }
        }
        return false;
    }
    
    
    public String shorten(String longName) {
        for (KVP kvp : nsList) {
            if (longName.startsWith(kvp.longName)) {
                String retVal = longName.replaceFirst(kvp.longName, kvp.shortName+":");
                return retVal;
            }
        }
        return longName;
    }

}

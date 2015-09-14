/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.bl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.gesis.linkinspect.dal.LinkFileReader;
import org.gesis.linkinspect.model.Sample;
import org.gesis.linkinspect.model.Testset;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

/**
 *
 * @author bensmafx
 */
public class Selector {

    private static final String FIRST_N = "First n";
    
    private String[] selectionMethods = null;
    private ArrayList<Statement> selection = null;

    public Selector() {
        selectionMethods = new String[]{FIRST_N};
        selection = new ArrayList<Statement>(); 
    }

    public String[] getSelectionMethods() {
        return selectionMethods;
    }

    public void selectFrom(String method, File file, int sampleCount) throws IOException, RDFParseException, RDFHandlerException, Exception {
        if (method.equals(FIRST_N)) {
            LinkFileReader reader = new LinkFileReader(file);
            reader.startReading();
            selection.clear();
            int cnt =0; 
            while (reader.hasNext()) {
                Statement st = reader.readNext();
                if(cnt < sampleCount){
                    selection.add(st);
                    cnt++;
                }
                else{
                    break;
                }
            }
            if(cnt < sampleCount){
                throw new Exception("Less triples available than requested.");
            }
        }
    }
    
    public Testset generateTestSet() throws IllegalStateException{
        if(selection.isEmpty()){
            throw new IllegalStateException("A selection has not been done.");
        }
        Testset set = new Testset();
        for(int i=0; i< selection.size();i++){
            Sample s = new Sample(selection.get(i)).init(Sample.State.OPEN);
            set.add(s);
        }
        return set;
    }

}

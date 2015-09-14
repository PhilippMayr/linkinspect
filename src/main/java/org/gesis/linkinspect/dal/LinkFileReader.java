/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.dal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;

/**
 *
 * @author bensmafx
 */
public class LinkFileReader {
    
    private Queue<Statement> queue = null;
    private File file = null;
    RDFParser rdfParser= null;
    
    public LinkFileReader(File file){
        this.file = file;
        queue = new LinkedList<Statement>();
        reset();
    
    }
    
    public void startReading() throws IOException, RDFParseException, RDFHandlerException{
        rdfParser.parse(new FileInputStream(file), "");
    }
    
    public boolean hasNext(){
        return !queue.isEmpty();
    }
    
    public Statement readNext(){
        return (Statement) queue.poll();
    }
        
    public void reset(){
        queue.clear();
        rdfParser = Rio.createParser(RDFFormat.NTRIPLES);
        rdfParser.setRDFHandler(new RDFWriterHandler(queue));
    }    
        

    
   

        
    
    
    
}

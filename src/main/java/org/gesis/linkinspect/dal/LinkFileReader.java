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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;
import org.gesis.linkinspect.bl.Selector;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;

/**
 * @author Felix Bensmann
 * Reads a link file
 */
public class LinkFileReader {
    
    private Queue<Statement> queue = null;
    private File file = null;
    private RDFParser rdfParser= null;
    private FileInputStream fis = null;
    
    /**
     * ctor
     * @param file Link file to read
     */
    public LinkFileReader(File file){
        this.file = file;
        queue = new LinkedList<Statement>();
        reset();
    
    }
    
    /**
     * Starts a reading process
     * @throws IOException
     * @throws RDFParseException
     * @throws RDFHandlerException 
     */
    public void startReading() throws IOException, RDFParseException, RDFHandlerException{
        fis = new FileInputStream(file);
        rdfParser.parse(fis, "");
    }
    
    /**
     * Returns true if a statement is available
     * @return 
     */
    public boolean hasNext(){
        return !queue.isEmpty();
    }
    
    /**
     * Extracts the new statement
     * @return 
     */
    public Statement readNext(){
        return (Statement) queue.poll();
    }
        
    /**
     * Resets the reader
     */
    public void reset(){
        close();
        queue.clear();
        rdfParser = Rio.createParser(RDFFormat.NTRIPLES);
        rdfParser.setRDFHandler(new RDFWriterHandler(queue));
    }    

    public void close() {
        try {
            if(fis != null)
                fis.close();
        } catch (IOException ex) {
            LogManager.getLogger(LinkFileReader.class).log(org.apache.logging.log4j.Level.ERROR, ex);  
        }
    }
        

    
   

        
    
    
    
}

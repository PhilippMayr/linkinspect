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
package org.gesis.linkinspect.model;

import java.io.File;

/**
 * @author Felix Bensmann
 * Class to store session settings.
 */
public class SessionSettings {
    
    private LinkFile linkFile = null;
    private String selectMethod = null;
    private int nrOfsamples = -1;
    private String srcSparqlEp = null;
    private String trtSparqlEp = null;
    
    
    public SessionSettings(){
        
    }

    public LinkFile getLinkFile() {
        return linkFile;
    }

    public void setLinkFile(LinkFile linkFile) {
        this.linkFile = linkFile;
    }

    public String getSelectMethod() {
        return selectMethod;
    }

    public void setSelectMethod(String selectMethod) {
        this.selectMethod = selectMethod;
    }

    public int getNrOfsamples() {
        return nrOfsamples;
    }

    public void setNrOfsamples(int nrOfsamples) {
        this.nrOfsamples = nrOfsamples;
    }

    public String getSrcSparqlEp() {
        return srcSparqlEp;
    }

    public void setSrcSparqlEp(String srcSparqlEp) {
        this.srcSparqlEp = srcSparqlEp;
    }

    public String getTrtSparqlEp() {
        return trtSparqlEp;
    }

    public void setTrtSparqlEp(String trtSparqlEp) {
        this.trtSparqlEp = trtSparqlEp;
    }
    
    
    
    
}

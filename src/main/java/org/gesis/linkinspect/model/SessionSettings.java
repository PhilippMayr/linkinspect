/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.model;

import java.io.File;

/**
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

package org.gesis.linkinspect.model;

import java.io.File;

/**
 * Stores metadata about a link file
 */
public class LinkFile {
    
    private File file = null;
    private String linkType = null;
    private long linkCount = -1;
    
    
    /**
     * ctor
     * @param file
     * @param linkType
     * @param linkCount 
     */
    public LinkFile(File file, String linkType, long linkCount){
        this.file=file;
        this.linkType=linkType;
        this.linkCount=linkCount;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public long getLinkCount() {
        return linkCount;
    }

    public void setLinkCount(long linkCount) {
        this.linkCount = linkCount;
    }
    
    
    
    
}

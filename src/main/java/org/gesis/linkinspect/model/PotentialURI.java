package org.gesis.linkinspect.model;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * Represents a value that can possibly be interpreted as a URI.
 */
public class PotentialURI {
    
    protected Value value = null;
    
    
    public boolean isURI(){
        if(value instanceof URI)
            return true;
        return false;
    }
            
    public boolean isLiteral(){
        if(value instanceof Literal)
            return true;
        return false;
    }
            
    public String getValue(){
        return value.stringValue();
    }
    
    
    
}

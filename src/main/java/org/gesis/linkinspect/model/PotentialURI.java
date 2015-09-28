/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.model;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author bensmafx
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

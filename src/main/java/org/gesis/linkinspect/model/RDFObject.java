/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.model;

import org.openrdf.model.Value;

/**
 *
 * @author bensmafx
 */
public class RDFObject extends PotentialURI{
    
    private String origin = null;
    
    
    public RDFObject(Value value, String origin){
        this.value = value;
        this.origin = origin;
    }

    public String getOrigin() {
        return origin;
    }
    
    
    
}

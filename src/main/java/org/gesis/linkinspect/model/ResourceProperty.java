/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.model;

import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author bensmafx
 */
public class ResourceProperty {
    
    private URI predicate = null;
    private boolean forward = true;
    private Value refValue = null;
    
    
    public ResourceProperty(URI predicate, Value refValue, boolean forward){
        this.predicate = predicate;
        this.refValue = refValue;
        this.forward = forward;
    }

    public String getPredicate() {
        if(forward)
            return predicate.stringValue();
        else
            return "is "+predicate.stringValue()+" of";
    }

    public Value getRefValue() {
        return refValue;
    }
    
    
    
    

    
}

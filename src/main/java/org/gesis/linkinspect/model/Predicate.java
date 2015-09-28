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
public class Predicate extends PotentialURI{
    
    private boolean forward = true;
    
    
    public Predicate(Value value, boolean forward){
        this.value = value;
        this.forward = forward;
    }

    public boolean isForward() {
        return forward;
    }
    
    
    
}

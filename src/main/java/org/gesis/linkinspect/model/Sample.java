/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.model;

import org.openrdf.model.Statement;

/**
 *
 * @author bensmafx
 */
public class Sample {

    
    
    public enum State{
        UNDEFINED,
        OPEN,
        CORRECT,
        INCORRECT,
        UNDECIDABLE
    }
    
    
    private Statement statement = null;
    private State state = State.UNDEFINED;
    
    public Sample(Statement stmt){
        this.statement = stmt;
    }
    
    public Sample init(State st){
        this.state=st;
        return this;
    }
    
    public String getLeftResource(){
        return statement.getSubject().stringValue();
    }
    
    public String getRightResource() {
        return statement.getObject().stringValue();
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
    
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.model;

import java.util.Date;
import org.openrdf.model.Statement;

/**
 * Class to store a link-statement and a state.
 */
public class Sample {

    
    /**
     * State of sample
     */
    public enum State{
        UNDEFINED,   //state is undefine, right after instanciation etc.
        OPEN,        //sample was not evaluated yet
        CORRECT,     //the sample is correct
        INCORRECT,   //the sample in incorrect
        UNDECIDABLE  //the sample is undecidable
    }
    
    
    private Statement statement = null;
    private State state = State.UNDEFINED;
    private Date date = null;
    
    public Sample(Statement stmt){
        this.statement = stmt;
    }
    
    /**
     * Sets the initial state of a sample
     * @param st
     * @return 
     */
    public Sample init(State st){
        this.state=st;
        date = new Date();
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
        date = new Date();
    }

    public Date getDate() {
        return date;
    }
    
    
    
    
    
    
}

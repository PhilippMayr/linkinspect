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

import java.util.Date;
import org.openrdf.model.Statement;

/**
 * @author Felix Bensmann
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
    
    @Override
    public String toString(){
        return "Sample: "+statement.toString()+" - "+state.name();
    }
    
    
    
    
}

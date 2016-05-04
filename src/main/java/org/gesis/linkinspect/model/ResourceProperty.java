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

import javafx.scene.control.Alert;

/**
 * @author Felix Bensmann
 * Stores property information
 */
public class ResourceProperty {

    //predicate
    private Predicate predicate = null;
    //object: literal or resource
    private RDFObject refValue = null;
   

    public ResourceProperty(Predicate predicate, RDFObject refValue) {
        this.predicate = predicate;
        this.refValue = refValue;
    }
    
    
    public Predicate getPredicate(){
       return predicate;
    }
    
    
    public RDFObject getRefValue(){
        return refValue;
    }
    
    
    /**
     * Shows an error dialog
     *
     * @param msg
     */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Ooops, there was an error!");
        alert.setContentText(msg);
        alert.showAndWait();
    }

  
    
}

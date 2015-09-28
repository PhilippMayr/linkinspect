/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.gesis.linkinspect.model.RDFObject;
import org.gesis.linkinspect.model.ResourceProperty;

/**
 *
 * @author bensmafx
 */
public class UIFactory4Objects implements Callback<TableColumn<ResourceProperty,RDFObject>,TableCell<ResourceProperty,RDFObject>> {

    public UIFactory4Objects() {
    
    }

    @Override
    public TableCell<ResourceProperty, RDFObject> call(TableColumn<ResourceProperty, RDFObject> param) {
        return new ObjectCell();
    }

   

   
   

   

   
   
    
    
}

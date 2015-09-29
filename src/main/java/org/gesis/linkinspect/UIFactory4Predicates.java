/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.gesis.linkinspect.model.PotentialURI;
import org.gesis.linkinspect.model.Predicate;
import org.gesis.linkinspect.model.ResourceProperty;

/**
 *
 * @author bensmafx
 */
public class UIFactory4Predicates implements Callback<TableColumn<ResourceProperty,Predicate>,TableCell<ResourceProperty,Predicate>> {

    private OnPredicateClickListener listener = null;
    
    public UIFactory4Predicates(OnPredicateClickListener l) {
        listener = l;
    }

    @Override
    public TableCell<ResourceProperty, Predicate> call(TableColumn<ResourceProperty, Predicate> param) {
        return new PredicateCell(listener);
    }

   public void setListener(OnPredicateClickListener listener) {
        this.listener = listener;
    }

   
   

   

   
   
    
    
}

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
 * http://www.gnu.org/licenses/.
 */
package org.gesis.linkinspect;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.gesis.linkinspect.model.RDFObject;
import org.gesis.linkinspect.model.ResourceProperty;

/**
 * @author Felix Bensmann
 * Factory for the creation of cell-objects to populate a table view
 */
public class UIFactory4Objects implements Callback<TableColumn<ResourceProperty,RDFObject>,TableCell<ResourceProperty,RDFObject>> {

    private OnObjectClickListener listener = null;
    
    /**
     * ctor
     * @param l Listener to be handed to the created cells 
     */
    public UIFactory4Objects(OnObjectClickListener l) {
        listener = l;
    }

    /**
     * Is called by the tableview in order to create a new cell
     * @param param
     * @return 
     */
    @Override
    public TableCell<ResourceProperty, RDFObject> call(TableColumn<ResourceProperty, RDFObject> param) {
        return new ObjectCell(listener);
    }

    public void setListener(OnObjectClickListener listener) {
        this.listener = listener;
    }
    
    

   

   
   

   

   
   
    
    
}

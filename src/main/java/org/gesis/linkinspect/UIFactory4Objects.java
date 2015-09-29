package org.gesis.linkinspect;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.gesis.linkinspect.model.RDFObject;
import org.gesis.linkinspect.model.ResourceProperty;

/**
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

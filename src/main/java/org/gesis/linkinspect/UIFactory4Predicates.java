package org.gesis.linkinspect;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.gesis.linkinspect.model.PotentialURI;
import org.gesis.linkinspect.model.Predicate;
import org.gesis.linkinspect.model.ResourceProperty;

/**
 * Factory for the creation of cell-objects to populate a table view
 */
public class UIFactory4Predicates implements Callback<TableColumn<ResourceProperty,Predicate>,TableCell<ResourceProperty,Predicate>> {

    private OnPredicateClickListener listener = null;
    
    /**
     * ctor
     * @param l 
     */
    public UIFactory4Predicates(OnPredicateClickListener l) {
        listener = l;
    }

    /**
     * Is called by the tableview in order to create a new cell
     * @param param
     * @return 
     */
    @Override
    public TableCell<ResourceProperty, Predicate> call(TableColumn<ResourceProperty, Predicate> param) {
        return new PredicateCell(listener);
    }

   public void setListener(OnPredicateClickListener listener) {
        this.listener = listener;
    }

   
   

   

   
   
    
    
}

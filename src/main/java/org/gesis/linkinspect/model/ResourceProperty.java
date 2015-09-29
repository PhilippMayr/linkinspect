package org.gesis.linkinspect.model;

import javafx.scene.control.Alert;

/**
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

package org.gesis.linkinspect.model;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.openrdf.model.Value;

/**
 * Represents an RDF object.
 */
public class RDFObject extends PotentialURI implements ObservableValue{
    
    private String origin = null;
    private String preview = null;
    
    /**
     * ctor
     * @param value
     * @param origin 
     */
    public RDFObject(Value value, String origin){
        this.value = value;
        this.origin = origin;
    }

    /**
     * Returns the direction of the link.
     *
     * @return True, if the link points from the given resource to another,
     * false if the given resource is reference by an external resource.
     */
    public String getOrigin() {
        return origin;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    @Override
    public void addListener(ChangeListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeListener(ChangeListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addListener(InvalidationListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
    
}

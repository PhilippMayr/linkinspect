package org.gesis.linkinspect.model;

import org.openrdf.model.Value;

/**
 * Represents an RDF object.
 */
public class RDFObject extends PotentialURI{
    
    private String origin = null;
    
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
    
    
    
}

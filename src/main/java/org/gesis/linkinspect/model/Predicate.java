package org.gesis.linkinspect.model;

import org.openrdf.model.Value;

/**
 * Represents an RDF predicate
 */
public class Predicate extends PotentialURI {

    private boolean forward = true;

    public Predicate(Value value, boolean forward) {
        this.value = value;
        this.forward = forward;
    }

    /**
     * Returns the direction of the link.
     *
     * @return True, if the link points from the given resource to another,
     * false if the given resource is reference by an external resource.
     */
    public boolean isForward() {
        return forward;
    }

}

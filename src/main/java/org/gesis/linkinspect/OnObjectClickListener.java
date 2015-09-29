package org.gesis.linkinspect;

import org.gesis.linkinspect.model.RDFObject;

/**
 * A listener to stay informed on events on an RDF object
 */
public interface OnObjectClickListener {
    
    public void onObjectClick(RDFObject object);
    public void onOpenExternRequest(RDFObject object);
}

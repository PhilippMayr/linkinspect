package org.gesis.linkinspect;

import org.gesis.linkinspect.model.Predicate;

/**
 * A listener to stay informed on events on an RDF predicate
 */
public interface OnPredicateClickListener {
    
    public void onPredicateClick(Predicate predicate);
    public void onOpenExternRequest(Predicate predicate);
    
}

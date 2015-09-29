/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect;

import org.gesis.linkinspect.model.Predicate;

/**
 * A listener to stay informed on events on an RDF predicate
 */
public interface OnPredicateClickListener {
    
    public void onPredicateClick(Predicate predicate);
    public void onOpenExternRequest(Predicate predicate);
    
}

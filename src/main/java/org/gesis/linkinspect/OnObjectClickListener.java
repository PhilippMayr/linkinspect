/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect;

import org.gesis.linkinspect.model.RDFObject;

/**
 *
 * @author bensmafx
 */
public interface OnObjectClickListener {
    
    public void onObjectClick(RDFObject object);
    public void onOpenExternRequest(RDFObject object);
}

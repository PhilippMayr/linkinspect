/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.model;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * Stores property information
 */
public class ResourceProperty {

    //predicate
    private URI predicate = null;
    //direction; forward = resource has...
    //backward resource is .. of other resourde
    private boolean forward = true;
    //the other resource or literal
    private Value refValue = null;

    public ResourceProperty(URI predicate, Value refValue, boolean forward) {
        this.predicate = predicate;
        this.refValue = refValue;
        this.forward = forward;
    }

    public Labeled getPredicate() {
        String text = null;
        if (forward) {
            text = predicate.stringValue();
        } else {
            text = "is " + predicate.stringValue() + " of";
        }
        Hyperlink hyperlink = new Hyperlink(text);
            hyperlink.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    Hyperlink hyp = (Hyperlink)event.getSource();
                    String str = predicate.stringValue();
                    Desktop desktop = Desktop.getDesktop();
                    java.net.URI uri = java.net.URI.create(str);
                    try {
                        desktop.browse(uri);
                    } catch (IOException ex) {
                        Logger.getLogger(ResourceProperty.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            return hyperlink;
        }
       
    

    public Labeled getRefValue() {
        if (refValue instanceof URI) {
            Hyperlink hyperlink = new Hyperlink(refValue.stringValue());
            hyperlink.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    Hyperlink hyp = (Hyperlink)event.getSource();
                    String str = refValue.stringValue();
                    Desktop desktop = Desktop.getDesktop();
                    java.net.URI uri = java.net.URI.create(str);
                    try {
                        desktop.browse(uri);
                    } catch (IOException ex) {
                        Logger.getLogger(ResourceProperty.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            return hyperlink;
        }
        else{
            return new Label(refValue.stringValue());
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.model;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import org.gesis.linkinspect.ResourceDisplayDialog;
import org.gesis.linkinspect.bl.NSResolver;
import org.gesis.linkinspect.dal.SparqlSource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

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
    //origin of the property e.g. a sparql endpoint
    private String origin = null;

    public ResourceProperty(URI predicate, Value refValue, boolean forward, String origin) {
        this.predicate = predicate;
        this.refValue = refValue;
        this.forward = forward;
        this.origin = origin;
    }

    public Labeled getPredicate() {
        String text = null;
        if (forward) {
            text = NSResolver.getInstance().shorten(predicate.stringValue());
        } else {
            text = "is " + NSResolver.getInstance().shorten(predicate.stringValue()) + " of";
        }
        Hyperlink hyperlink = new Hyperlink(text);
        hyperlink.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
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
            Hyperlink hyperlink = new Hyperlink(NSResolver.getInstance().shorten(refValue.stringValue()));
            hyperlink.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    String str = refValue.stringValue();
                    try {
                        if (SparqlSource.isPresent(origin, refValue.stringValue())) {
                            ResourceDisplayDialog browser = new ResourceDisplayDialog(refValue.stringValue(), origin);
                            browser.showAndWait();
                        } else {
                            Desktop desktop = Desktop.getDesktop();
                            java.net.URI uri = java.net.URI.create(str);
                            try {
                                desktop.browse(uri);
                            } catch (IOException ex) {
                                Logger.getLogger(ResourceProperty.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } catch (Exception ex) {
                        showError(ex.getMessage()+"\nPlease try again.");
                        System.err.println(ex);
                    }

                }
            });
            return hyperlink;
        } else {
            return new Label(refValue.stringValue());
        }
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

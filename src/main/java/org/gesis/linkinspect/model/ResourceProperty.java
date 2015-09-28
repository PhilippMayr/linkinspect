/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

//    public Labeled getPredicate() {
//        String text = null;
//        if (forward) {
//            text = NSResolver.getInstance().shorten(predicate.stringValue());
//        } else {
//            text = "is " + NSResolver.getInstance().shorten(predicate.stringValue()) + " of";
//        }
//        Hyperlink hyperlink = new Hyperlink(text);
//        hyperlink.setTooltip(new Tooltip(predicate.stringValue()));
//        hyperlink.setOnAction(new EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent event) {
//                String str = predicate.stringValue();
//                Desktop desktop = Desktop.getDesktop();
//                java.net.URI uri = java.net.URI.create(str);
//                try {
//                    desktop.browse(uri);
//                } catch (IOException ex) {
//                    Logger.getLogger(ResourceProperty.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        });
//        return hyperlink;
//    }
//
//    public Labeled getRefValue() {
//        if (refValue instanceof URI) {
//            Hyperlink hyperlink = new Hyperlink(NSResolver.getInstance().shorten(refValue.stringValue()));
//            hyperlink.setTooltip(new Tooltip(refValue.stringValue()));
//            hyperlink.setOnAction(new EventHandler<ActionEvent>() {
//
//                @Override
//                public void handle(ActionEvent event) {
//                    String str = refValue.stringValue();
//                    try {
//                        if (SparqlSource.isPresent(origin, refValue.stringValue())) {
//                            ResourceDisplayDialog browser = new ResourceDisplayDialog(refValue.stringValue(), origin);
//                            browser.showAndWait();
//                        } else {
//                            Desktop desktop = Desktop.getDesktop();
//                            java.net.URI uri = java.net.URI.create(str);
//                            try {
//                                desktop.browse(uri);
//                            } catch (IOException ex) {
//                                Logger.getLogger(ResourceProperty.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                        }
//                    } catch (Exception ex) {
//                        showError(ex.getMessage()+"\nPlease try again.");
//                        System.err.println(ex);
//                    }
//
//                }
//            });
//            return hyperlink;
//        } else {
//            return new Label(refValue.stringValue());
//        }
//    }
    
    
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect;

import java.awt.Desktop;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import org.gesis.linkinspect.bl.NSResolver;
import org.gesis.linkinspect.dal.SparqlSource;
import org.gesis.linkinspect.model.PotentialURI;
import org.gesis.linkinspect.model.Predicate;
import org.gesis.linkinspect.model.ResourceProperty;

/**
 *
 * @author bensmafx
 */
public class PredicateCell extends TableCell<ResourceProperty, Predicate> {

    private VBox vb;
    private Hyperlink hyperlink = null;
    private Predicate currentItem = null;

    public PredicateCell() {
        vb = new VBox();
        vb.setAlignment(Pos.CENTER_LEFT);
        hyperlink = new Hyperlink();
        vb.getChildren().add(hyperlink);
        setGraphic(vb);
    }

    @Override
    public void updateItem(Predicate item, boolean empty) { //item ist das rechte
        if (item != null) {
            currentItem = item;
            String text = null;
            if (item.isForward()) {
                text = NSResolver.getInstance().shorten(item.getValue());
            } else {
                text = "is " + NSResolver.getInstance().shorten(item.getValue()) + " of";
            }
            hyperlink.setText(text);
            hyperlink.setTooltip(new Tooltip(item.getValue()));
            hyperlink.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    String str = currentItem.getValue();
                    try {
                        Desktop desktop = Desktop.getDesktop();
                        java.net.URI uri = java.net.URI.create(str);
                        try {
                            desktop.browse(uri);
                        } catch (IOException ex) {
                            Logger.getLogger(ResourceProperty.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } catch (Exception ex) {
                        showError(ex.getMessage() + "\nPlease try again.");
                        System.err.println(ex);
                    }

                }
            });
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

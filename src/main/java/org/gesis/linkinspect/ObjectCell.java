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
import org.gesis.linkinspect.dal.SparqlSource;
import org.gesis.linkinspect.model.RDFObject;
import org.gesis.linkinspect.model.ResourceProperty;

/**
 *
 * @author bensmafx
 */
public class ObjectCell extends TableCell<ResourceProperty, RDFObject> {

    private VBox vb;
    private Labeled labeled = null;
    private RDFObject currentItem = null;

    public ObjectCell() {
        vb = new VBox();
        vb.setAlignment(Pos.CENTER_LEFT);
        labeled = new Hyperlink();
        vb.getChildren().add(labeled);
        setGraphic(vb);
    }

    @Override
    public void updateItem(RDFObject item, boolean empty) { //item ist das rechte
        if (item != null) {
            currentItem = item;
            if (item.isURI()) {
                if (!(labeled instanceof Hyperlink)) {
                    vb.getChildren().remove(labeled);
                    labeled = new Hyperlink();
                    vb.getChildren().add(labeled);
                }
                labeled.setTooltip(new Tooltip(item.getValue()));
                Hyperlink hyperlink = (Hyperlink) labeled;
                hyperlink.setText(item.getValue());
                hyperlink.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        String str = currentItem.getValue();
                        try {
                            if (SparqlSource.isPresent(currentItem.getOrigin(), currentItem.getValue())) {
                                ResourceDisplayDialog browser = new ResourceDisplayDialog(currentItem.getValue(), currentItem.getOrigin());
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
                            showError(ex.getMessage() + "\nPlease try again.");
                            System.err.println(ex);
                        }

                    }
                });

            } else {
                if (!(labeled instanceof Label)) {
                    vb.getChildren().remove(labeled);
                    labeled = new Label();
                    vb.getChildren().add(labeled);
                }
                labeled.setText(item.getValue());
            }

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

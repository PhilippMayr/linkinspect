/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
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
    private OnPredicateClickListener listener = null;

    public PredicateCell(OnPredicateClickListener l) {
        listener = l;
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
                    if (listener != null) {
                        listener.onPredicateClick(currentItem);
                    }
                }
            });
            hyperlink.setContextMenu(prepareContextMenu(true));
        }
    }
    
    
    private ContextMenu prepareContextMenu(boolean withExtern) {
        ContextMenu menu = new ContextMenu();
        MenuItem itemCopyClipboard = new MenuItem("Copy to clipboard");
        itemCopyClipboard.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                StringSelection stringSelection = new StringSelection(currentItem.getValue());
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                clpbrd.setContents(stringSelection, null);
            }
        });
        menu.getItems().add(itemCopyClipboard);

        if (withExtern) {
            MenuItem itemOpenExtern = new MenuItem("Open extern");
            itemOpenExtern.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    if(listener != null){
                    listener.onOpenExternRequest(currentItem);
                    }
                }
            });
            menu.getItems().add(itemOpenExtern);
        }
        return menu;
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

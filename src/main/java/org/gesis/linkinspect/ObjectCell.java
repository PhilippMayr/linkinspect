/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import org.gesis.linkinspect.model.RDFObject;
import org.gesis.linkinspect.model.ResourceProperty;

/**
 * A tableview cell that displays RDF objects
 */
public class ObjectCell extends TableCell<ResourceProperty, RDFObject> {

    //root container of the cell
    private VBox vb;
    //the label/hyperlink
    private Labeled labeled = null;
    //reference to the current rdf object
    private RDFObject currentItem = null;
    //reference to a listener
    private OnObjectClickListener listener = null;

    
    /**
     * ctor
     * @param l 
     */
    public ObjectCell(OnObjectClickListener l) {
        this.listener = l;
        vb = new VBox();
        vb.setAlignment(Pos.CENTER_LEFT);
        labeled = new Hyperlink();
        vb.getChildren().add(labeled);
        setGraphic(vb);
    }

    /**
     * Update the graphic representation, is automaticall called by the tableview
     * @param item
     * @param empty 
     */
    @Override
    public void updateItem(RDFObject item, boolean empty) { //item ist das rechte
        if (item != null) {
            currentItem = item;
            //in case the object is a resource
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
                        if (listener != null) {
                            listener.onObjectClick(currentItem);
                        }
                    }
                });
                labeled.setContextMenu(prepareContextMenu(true));
            }
            //in case the object is a literal
            else {
                if (!(labeled instanceof Label)) {
                    vb.getChildren().remove(labeled);
                    labeled = new Label();
                    vb.getChildren().add(labeled);
                }
                labeled.setText(item.getValue());
                labeled.setContextMenu(prepareContextMenu(false));
            }
            
        }
    }

    
    /**
     * Creates a context menu with a copy-to-clipboard item, and if desired an open-extern-item.
     * @param withExtern
     * @return 
     */
    private ContextMenu prepareContextMenu(boolean withExtern) {
        ContextMenu menu = new ContextMenu();
        //copy to clipboard
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

        //open extern
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

}

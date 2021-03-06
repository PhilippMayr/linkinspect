/*
 * Copyright (C) 2016 GESIS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, see 
 * http://www.gnu.org/licenses/.
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
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.apache.logging.log4j.LogManager;
import org.gesis.linkinspect.model.RDFObject;
import org.gesis.linkinspect.model.ResourceProperty;

/**
 * @author Felix Bensmann
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
    //
    private Label lbPreview = null;

    /**
     * ctor
     *
     * @param l
     */
    public ObjectCell(OnObjectClickListener l) {
        this.listener = l;
        vb = new VBox();
        vb.setAlignment(Pos.CENTER_LEFT);
        labeled = new Hyperlink();
        vb.getChildren().add(labeled);
        lbPreview = new Label();
        lbPreview.setFont(new Font(10f));
        lbPreview.setWrapText(false);
        lbPreview.setTextOverrun(OverrunStyle.ELLIPSIS);
        //vb.getChildren().add(lbPreview);
        setGraphic(vb);
    }

    /**
     * Update the graphic representation, is automaticall called by the
     * tableview
     *
     * @param item
     * @param empty
     */
    @Override
    public void updateItem(RDFObject item, boolean empty) { //item ist das rechte
        super.updateItem((RDFObject) item, empty);
        if (empty) {
            vb.setVisible(false);
            return;
        }
        if (item != null) {
            vb.setVisible(true);
            currentItem = item;
            //in case the object is a resource
            if (item.isURI()) {
                if (!(labeled instanceof Hyperlink)) {
                    vb.getChildren().remove(labeled);
                    labeled = new Hyperlink();
                    vb.getChildren().add(labeled);
                }
                if (!vb.getChildren().contains(lbPreview)) {
                    vb.getChildren().add(lbPreview);
                }
                if (item.getPreview() == null || item.getPreview().equals("")) {
                    lbPreview.setText("- no preview -");
                    lbPreview.setTooltip(null);
                } else {
                    lbPreview.setText(item.getPreview());
                    lbPreview.setTooltip(new Tooltip(item.getPreview()));
                }
                labeled.setTooltip(new Tooltip(item.getValue()));
                //labeled.setTooltip(new Tooltip(item.getPreview()));
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
            } //in case the object is a literal
            else {
                if (!(labeled instanceof Label)) {
                    vb.getChildren().remove(labeled);
                    labeled = new Label();
                    vb.getChildren().add(labeled);
                }
                labeled.setText(item.getValue());
                labeled.setContextMenu(prepareContextMenu(false));
                if (vb.getChildren().contains(lbPreview)) {
                    vb.getChildren().remove(lbPreview);
                }
            }

        }
    }

    /**
     * Creates a context menu with a copy-to-clipboard item, and if desired an
     * open-extern-item.
     *
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
                    if (listener != null) {
                        LogManager.getLogger(ObjectCell.class).log(org.apache.logging.log4j.Level.DEBUG, "Fire \"open extern\" event.");
                        listener.onOpenExternRequest(currentItem);
                    }
                }
            });
            menu.getItems().add(itemOpenExtern);
        }
        return menu;
    }

}

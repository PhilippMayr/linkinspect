package org.gesis.linkinspect;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.gesis.linkinspect.bl.NSResolver;
import org.gesis.linkinspect.model.Predicate;
import org.gesis.linkinspect.model.ResourceProperty;

/**
 * A tableview cell that displays RDF predicates
 */
public class PredicateCell extends TableCell<ResourceProperty, Predicate> {

    //surrounding root container
    private VBox vb;
    //the hyperlink displaying the predicate's URI
    private Hyperlink hyperlink = null;
    //the current item 
    private Predicate currentItem = null;
    //reference to a listener
    private OnPredicateClickListener listener = null;

    /**
     * ctor
     *
     * @param l
     */
    public PredicateCell(OnPredicateClickListener l) {
        listener = l;
        vb = new VBox();
        vb.setAlignment(Pos.CENTER_LEFT);
        hyperlink = new Hyperlink();
        vb.getChildren().add(hyperlink);
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
    public void updateItem(Predicate item, boolean empty) {
        if (empty) {
            vb.setVisible(false);
            return;
        }
        if (item != null) {
            vb.setVisible(true);
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

    /**
     * Creates a context menu with a copy-to-clipboard item, and if desired an
     * open-extern-item.
     *
     * @param withExtern
     * @return
     */
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
                    if (listener != null) {
                        LogManager.getLogger(PredicateCell.class).log(org.apache.logging.log4j.Level.DEBUG, "Fire \"open extern\" event.");
                        listener.onOpenExternRequest(currentItem);
                    }
                }
            });
            menu.getItems().add(itemOpenExtern);
        }
        return menu;
    }


}

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

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.gesis.linkinspect.model.Predicate;
import org.gesis.linkinspect.model.RDFObject;
import org.gesis.linkinspect.model.ResourceProperty;

/**
 * @author Felix Bensmann
 * FXML Controller class for the central tableviews
 */
public class ResourceDisplayController implements Initializable {

    @FXML
    private TitledPane tpTitle;

    @FXML
    private TableView tvTable;

    //list to be mirrored by tvTable
    private ObservableList<Object> data;

    private OnPredicateClickListener onPredicateClickListener = null;
    private OnObjectClickListener onObjectClickListener = null;
    private UIFactory4Predicates predicateFactory = null;
    private UIFactory4Objects objectFactory = null;

    /**
     * Initializes the controller class.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tvTable.setEditable(false);
        tvTable.getColumns().clear();

        //create left column
        LogManager.getLogger(ResourceDisplayController.class).log(org.apache.logging.log4j.Level.DEBUG, "Configuring left hand column.");
        TableColumn<ResourceProperty, Predicate> predicateCol = new TableColumn<ResourceProperty, Predicate>("Predicate");
        predicateCol.setMinWidth(100);
        predicateCol.setCellValueFactory(new PropertyValueFactory<ResourceProperty, Predicate>("predicate"));
        predicateFactory = new UIFactory4Predicates(onPredicateClickListener);
        predicateCol.setCellFactory(predicateFactory);

        //create right column
        LogManager.getLogger(ResourceDisplayController.class).log(org.apache.logging.log4j.Level.DEBUG, "Configuring right hand column.");
        TableColumn<ResourceProperty, RDFObject> valueCol = new TableColumn<ResourceProperty, RDFObject>("Value");
        valueCol.setMinWidth(100);
        valueCol.setCellValueFactory(new PropertyValueFactory<ResourceProperty, RDFObject>("refValue"));
        objectFactory = new UIFactory4Objects(onObjectClickListener);
        valueCol.setCellFactory(objectFactory);

        //create list to be mirrored
        LogManager.getLogger(ResourceDisplayController.class).log(org.apache.logging.log4j.Level.DEBUG, "Adding data.");
        data = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

        //set list
        tvTable.setItems(data);
        //add columns
        tvTable.getColumns().addAll(predicateCol, valueCol);
    }

    public ObservableList<Object> getObservableList() {
        return data;
    }

    public void setTitle(String title) {
        tpTitle.setText(title);
    }

    void reset() {
        //TODO support
    }

    public void setOnPredicateClickListener(OnPredicateClickListener onPredicateClickListener) {
        this.onPredicateClickListener = onPredicateClickListener;
        predicateFactory.setListener(onPredicateClickListener);

    }

    public void setOnObjectClickListener(OnObjectClickListener onObjectClickListener) {
        this.onObjectClickListener = onObjectClickListener;
        objectFactory.setListener(onObjectClickListener);
    }

    /**
     * Invokes a delayed refresh on the table view
     *
     * @param millis Milliseconds to wait
     */
    @SuppressWarnings("unchecked")
    public void invokeDelayedRefresh(final int millis) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                TableColumn<Object, Object> col = (TableColumn<Object, Object>) tvTable.getColumns().get(0);
                                col.setVisible(false);
                                col.setVisible(true);
                            }
                        });
                    }
                },
                millis
        );

    }

}

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

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.gesis.linkinspect.dal.SparqlSource;
import org.gesis.linkinspect.model.Predicate;
import org.gesis.linkinspect.model.RDFObject;
import org.gesis.linkinspect.model.ResourceProperty;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

/**
 * @author Felix Bensmann
 * FXML Controller class
 */
public class ResourceDisplayDialog extends Stage implements Initializable, OnPredicateClickListener, OnObjectClickListener {

    @FXML
    private ResourceDisplayController rdCentralController;

    private String resource = null;
    private SparqlSource source = null;

    public ResourceDisplayDialog(String resource, String sparqlEp) throws MalformedURLException, RepositoryException, MalformedQueryException, QueryEvaluationException {
        LogManager.getLogger(ResourceDisplayDialog.class).log(org.apache.logging.log4j.Level.DEBUG, "Opening browser for " + resource + " in " + sparqlEp);
        this.resource = resource;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ResourceDisplayDialog.fxml"));
        fxmlLoader.setController(this);

        try {
            Scene scene = new Scene((Parent) fxmlLoader.load());
            setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTitle("Browser");

        rdCentralController.reset();
        rdCentralController.setOnPredicateClickListener(this);
        rdCentralController.setOnObjectClickListener(this);
        ObservableList<Object> ol = rdCentralController.getObservableList();
        source = new SparqlSource(new URL(sparqlEp), ol);
        rdCentralController.setTitle(resource);
        source.requestResource(resource);
        //schedule delayed refresh for tableView
        rdCentralController.invokeDelayedRefresh(3000);
        rdCentralController.invokeDelayedRefresh(6000);

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    /**
     * Displays the given resource
     *
     * @param resource the resource
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    public void display(String resource) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        LogManager.getLogger(ResourceDisplayDialog.class).log(org.apache.logging.log4j.Level.DEBUG, "Displaying resource " + resource + ".");
        this.resource = resource;
        rdCentralController.setTitle(this.resource);
        source.requestResource(this.resource);
        rdCentralController.invokeDelayedRefresh(3000);
        rdCentralController.invokeDelayedRefresh(6000);
    }

    /**
     * Reacts to a click in a predicate
     *
     * @param predicate
     */
    @Override
    public void onPredicateClick(Predicate predicate) {
        LogManager.getLogger(ResourceDisplayDialog.class).log(org.apache.logging.log4j.Level.DEBUG, "click on predicate " + predicate.getValue());
        try {
            Desktop desktop = Desktop.getDesktop();
            java.net.URI uri = java.net.URI.create(predicate.getValue());
            try {
                desktop.browse(uri);
            } catch (IOException ex) {
                LogManager.getLogger(ResourceDisplayDialog.class).log(org.apache.logging.log4j.Level.DEBUG, ex);
            }
        } catch (Exception ex) {
            LogManager.getLogger(ResourceDisplayDialog.class).log(org.apache.logging.log4j.Level.WARN, "Request for predicate " + predicate.getValue() + " failed. " + ex);
            showError(ex.getMessage() + "\nPlease try again.");
        }
    }

    /**
     * Reacts to a click on an rdf object
     *
     * @param object
     */
    @Override
    public void onObjectClick(RDFObject object) {
        LogManager.getLogger(ResourceDisplayDialog.class).log(org.apache.logging.log4j.Level.DEBUG, "click on object " + object.getValue() + " in " + object.getOrigin() + ".");
        try {

            if (SparqlSource.isPresent(object.getOrigin(), object.getValue())) {
                display(object.getValue());
            } else {
                Desktop desktop = Desktop.getDesktop();
                java.net.URI uri = java.net.URI.create(object.getValue());
                try {
                    desktop.browse(uri);
                } catch (IOException ex) {
                    LogManager.getLogger(ResourceDisplayDialog.class).log(org.apache.logging.log4j.Level.ERROR, "Openening resource in desktop web browser failed. " + ex);
                }
            }
        } catch (Exception ex) {
            LogManager.getLogger(ResourceDisplayDialog.class).log(org.apache.logging.log4j.Level.WARN, "Request for object " + object.getValue() + " in " + object.getOrigin() + " failed." + ex);
            showError(ex.getMessage() + "\nPlease try again.");
        }
    }

    /**
     * Reacts to an "Open-extern"-request by opening the resource in the system
     * web browser.
     *
     * @param object
     */
    @Override
    public void onOpenExternRequest(RDFObject object) {
        LogManager.getLogger(ResourceDisplayDialog.class).log(org.apache.logging.log4j.Level.DEBUG, "Open extern request for object " + object.getValue());
        Desktop desktop = Desktop.getDesktop();
        java.net.URI uri = java.net.URI.create(object.getValue());
        try {
            desktop.browse(uri);
        } catch (IOException ex) {
            LogManager.getLogger(ResourceDisplayDialog.class).log(org.apache.logging.log4j.Level.ERROR, ex);
            showError(ex.getMessage());
        }
    }

    /**
     * Reacts to an "Open-extern"-request by opening the resource in the system
     * web browser.
     *
     * @param predicate
     */
    @Override
    public void onOpenExternRequest(Predicate predicate) {
        LogManager.getLogger(ResourceDisplayDialog.class).log(org.apache.logging.log4j.Level.DEBUG, "Open extern request for predicate " + predicate.getValue());
        Desktop desktop = Desktop.getDesktop();
        java.net.URI uri = java.net.URI.create(predicate.getValue());
        try {
            desktop.browse(uri);
        } catch (IOException ex) {
            LogManager.getLogger(ResourceDisplayDialog.class).log(org.apache.logging.log4j.Level.ERROR, ex);
            showError(ex.getMessage());
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

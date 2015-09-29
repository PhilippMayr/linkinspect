/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import org.gesis.linkinspect.dal.SparqlSource;
import org.gesis.linkinspect.model.Predicate;
import org.gesis.linkinspect.model.RDFObject;
import org.gesis.linkinspect.model.ResourceProperty;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

/**
 * FXML Controller class
 */
public class ResourceDisplayDialog extends Stage implements Initializable, OnPredicateClickListener, OnObjectClickListener {

    @FXML
    private ResourceDisplayController rdCentralController;

    private String resource = null;
    private SparqlSource source = null;

    public ResourceDisplayDialog(String resource, String sparqlEp) throws MalformedURLException, RepositoryException, MalformedQueryException, QueryEvaluationException {
        this.resource = resource;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ResourceDisplayDialog.fxml"));
        fxmlLoader.setController(this);

        // Nice to have this in a load() method instead of constructor, but this seems to be the convention.
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
        ObservableList<ResourceProperty> ol = rdCentralController.getObservableList();
        source = new SparqlSource(new URL(sparqlEp), ol);
        rdCentralController.setTitle(resource);
        source.requestResource(resource);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    public void display(String resource) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
        this.resource = resource;
        rdCentralController.setTitle(this.resource);
        source.requestResource(this.resource);
    }

    @Override
    public void onPredicateClick(Predicate predicate) {
        System.out.println("click on predicate "+predicate.getValue());
        try {
            Desktop desktop = Desktop.getDesktop();
            java.net.URI uri = java.net.URI.create(predicate.getValue());
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

    
    @Override
    public void onObjectClick(RDFObject object) {
        System.out.println("click on object "+object.getValue());
        try {

            if (SparqlSource.isPresent(object.getOrigin(), object.getValue())) {
                display(object.getValue());
            } else {
                Desktop desktop = Desktop.getDesktop();
                java.net.URI uri = java.net.URI.create(object.getValue());
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
    
    @Override
    public void onOpenExternRequest(RDFObject object) {
        Desktop desktop = Desktop.getDesktop();
        java.net.URI uri = java.net.URI.create(object.getValue());
        try {
            desktop.browse(uri);
        } catch (IOException ex) {
            showError(ex.getMessage());
        }
    }
    
    @Override
    public void onOpenExternRequest(Predicate predicate) {
        Desktop desktop = Desktop.getDesktop();
        java.net.URI uri = java.net.URI.create(predicate.getValue());
        try {
            desktop.browse(uri);
        } catch (IOException ex) {
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

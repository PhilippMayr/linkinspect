/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.gesis.linkinspect.dal.SparqlSource;
import org.gesis.linkinspect.model.ResourceProperty;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

/**
 * FXML Controller class
 */
public class ResourceDisplayDialog extends Stage implements Initializable {

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

}

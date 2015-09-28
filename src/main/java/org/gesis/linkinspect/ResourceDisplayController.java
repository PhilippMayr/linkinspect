/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gesis.linkinspect.model.PotentialURI;
import org.gesis.linkinspect.model.Predicate;
import org.gesis.linkinspect.model.RDFObject;
import org.gesis.linkinspect.model.ResourceProperty;

/**
 * FXML Controller class for the central tableviews
 */
public class ResourceDisplayController implements Initializable {

    @FXML
    private TitledPane tpTitle;
    
    @FXML
    private TableView tvTable;
    
    //list to be mirrored by tvTable
    private ObservableList<ResourceProperty> data;
  
    
    /**
     * Initializes the controller class.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tvTable.setEditable(false);
        tvTable.getColumns().clear();
        
        //create left column
        TableColumn<ResourceProperty,Predicate> predicateCol = new TableColumn<ResourceProperty,Predicate>("Predicate");
        predicateCol.setMinWidth(100);
        predicateCol.setCellValueFactory( new PropertyValueFactory<ResourceProperty, Predicate>("predicate"));
        predicateCol.setCellFactory(new UIFactory4Predicates());
    
        //create right column
        TableColumn<ResourceProperty,RDFObject> valueCol = new TableColumn<ResourceProperty,RDFObject>("Value");
        valueCol.setMinWidth(100);
        valueCol.setCellValueFactory( new PropertyValueFactory<ResourceProperty, RDFObject>("refValue"));
        valueCol.setCellFactory(new UIFactory4Objects());
             
        
        //create list to be mirrored
        data = FXCollections.observableArrayList();
        
        //set list
        tvTable.setItems(data);
        //add columns
        tvTable.getColumns().addAll(predicateCol, valueCol);
    }
    
    
    public ObservableList<ResourceProperty> getObservableList(){
        return data;
    }
    
    public void setTitle(String title){
        tpTitle.setText(title);
    }

    void reset() {
        //TODO support
    }

    
}

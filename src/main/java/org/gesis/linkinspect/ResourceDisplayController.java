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
import javafx.scene.control.Labeled;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gesis.linkinspect.model.ResourceProperty;

/**
 * FXML Controller class
 *
 * @author bensmafx
 */
public class ResourceDisplayController implements Initializable {

    @FXML
    private TitledPane tpTitle;
    
    @FXML
    private TableView tvTable;
    
    private ObservableList<ResourceProperty> data;
  
    
    /**
     * Initializes the controller class.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tvTable.setEditable(false);
        tvTable.getColumns().clear();
        
        TableColumn<ResourceProperty,String> predicateCol = new TableColumn<ResourceProperty,String>("Predicate");
        predicateCol.setMinWidth(100);    
        predicateCol.setCellValueFactory( new PropertyValueFactory<ResourceProperty, String>("predicate"));
        
        TableColumn<ResourceProperty,String> valueCol = new TableColumn<ResourceProperty,String>("Value");
        valueCol.setMinWidth(100);
        valueCol.setCellValueFactory( new PropertyValueFactory<ResourceProperty, String>("refValue"));
        
        
        data = FXCollections.observableArrayList();
        
        tvTable.setItems(data);
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

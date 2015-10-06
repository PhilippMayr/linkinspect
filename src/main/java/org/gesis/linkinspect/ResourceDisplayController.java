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
        TableColumn<ResourceProperty,Predicate> predicateCol = new TableColumn<ResourceProperty,Predicate>("Predicate");
        predicateCol.setMinWidth(100);
        predicateCol.setCellValueFactory( new PropertyValueFactory<ResourceProperty, Predicate>("predicate"));
        predicateFactory = new UIFactory4Predicates(onPredicateClickListener);
        predicateCol.setCellFactory(predicateFactory);
    
        //create right column
        TableColumn<ResourceProperty,RDFObject> valueCol = new TableColumn<ResourceProperty,RDFObject>("Value");
        valueCol.setMinWidth(100);
        valueCol.setCellValueFactory( new PropertyValueFactory<ResourceProperty, RDFObject>("refValue"));
        objectFactory = new UIFactory4Objects(onObjectClickListener);
        valueCol.setCellFactory(objectFactory);
             
        
        //create list to be mirrored
        data = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        
        //set list
        tvTable.setItems(data);
        //add columns
        tvTable.getColumns().addAll(predicateCol, valueCol);
    }
    
    
    public ObservableList<Object> getObservableList(){
        return data;
    }
    
    public void setTitle(String title){
        tpTitle.setText(title);
    }

    void reset() {
        //TODO support
    }

    public void setOnPredicateClickListener(OnPredicateClickListener onPredicateClickListener) {
        this.onPredicateClickListener = onPredicateClickListener;
        predicateFactory.setListener(onPredicateClickListener);
        
    }
    
    public void setOnObjectClickListener(OnObjectClickListener onObjectClickListener){
        this.onObjectClickListener = onObjectClickListener;
        objectFactory.setListener(onObjectClickListener);
    }

    

    
    
    
}

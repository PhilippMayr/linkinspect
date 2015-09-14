/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author bensmafx
 */
public class NewDialog extends Stage implements Initializable {

    private boolean successful = false;
    
    @FXML
    private TextField tfPath;

    @FXML
    private Button btBrowse;
    
    @FXML
    private ChoiceBox<String> cbSelectionMethods;
    
    @FXML
    private Spinner spSamples;
    
    @FXML
    private TextField tfSource;
    
    @FXML
    private TextField tfTarget;
    
    @FXML
    private Button btFinish;
    
    @FXML
    private Button btCancel;

    public NewDialog(Parent parent, String[] selectionMethods) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/NewDialog.fxml"));
        fxmlLoader.setController(this);
        
        // Nice to have this in a load() method instead of constructor, but this seems to be the convention.
        try {
            Scene scene = new Scene((Parent) fxmlLoader.load());
            setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTitle("Create a new session...");
        initModality(Modality.APPLICATION_MODAL);
        
        successful = false;
        
        cbSelectionMethods.getItems().clear();
        cbSelectionMethods.getItems().addAll(Arrays.asList(selectionMethods));

        btCancel.setCancelButton(true);

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        successful=false;
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource() == btBrowse) {
            FileChooser fileChooser = new FileChooser();
            File dir = new File(System.getProperty("user.home"));
            fileChooser.setInitialDirectory(dir);
            fileChooser.setTitle("Open link file");
            fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("NTriple files (*.nt)", "*.nt"));
            File file = fileChooser.showOpenDialog(null);
            if(file != null){
                tfPath.setText(file.getAbsolutePath());
            }
        }
        else if(event.getSource() == btCancel){
            successful = false;
            this.close();
        }
        else if(event.getSource() == btFinish){
            successful = true;
            this.close();
        }
        

    }

    public boolean isSuccessful() {
        return successful;
    }
    
    public String getFilePath(){
        return tfPath.getText();
    }
    
    public String getSelectionMethod(){
        return cbSelectionMethods.getSelectionModel().getSelectedItem();
    }
    
    public int getSampleCount(){
        Object o = spSamples.getValue();
        return (int)o;
    }

    public String getSource(){
        return tfSource.getText();
    }
    
    public String getTarget(){
        return tfTarget.getText();
    }
    
}

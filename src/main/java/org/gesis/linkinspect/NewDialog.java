package org.gesis.linkinspect;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.gesis.linkinspect.bl.LinkFileChecker;
import org.gesis.linkinspect.dal.PreferenceStorage;
import org.gesis.linkinspect.model.LinkFile;

/**
 * FXML Controller class for the NewDialog
 */
public class NewDialog extends Stage implements Initializable {

    //click on ok or on cancel
    private boolean successful = false;

    //UI elements
    @FXML
    private VBox vbFrame;

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

    private LinkFile linkFile = null;

    /**
     * Loads the dialog and initializes it.
     *
     * @param parent
     * @param selectionMethods
     */
    @SuppressWarnings("unchecked")
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

        //preset a value for the selection method
        cbSelectionMethods.getItems().clear();
        cbSelectionMethods.getItems().addAll(Arrays.asList(selectionMethods));
        cbSelectionMethods.setValue(cbSelectionMethods.getItems().get(0));
     
        //work around broken spinner
        spSamples.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    return;
                }
                commitEditorText(spSamples);
            }
        });
        
        //preset a value for SPARQL source and target
        tfSource.setText(PreferenceStorage.getInstance().getSource());
        tfTarget.setText(PreferenceStorage.getInstance().getTarget());

        btCancel.setCancelButton(true);
    }

    private <T> void commitEditorText(Spinner<T> spinner) {
        if (!spinner.isEditable()) {
            return;
        }
        String text = spinner.getEditor().getText();
        SpinnerValueFactory<T> valueFactory = spinner.getValueFactory();
        if (valueFactory != null) {
            StringConverter<T> converter = valueFactory.getConverter();
            if (converter != null) {
                T value = converter.fromString(text);
                valueFactory.setValue(value);
            }
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        successful = false;
    }

    /**
     * Handles clicks to buttons in this dialog
     *
     * @param event
     */
    @FXML
    @SuppressWarnings("unchecked")
    private void handleButtonAction(ActionEvent event) {
        //if browse button, select a file
        if (event.getSource() == btBrowse) {
            FileChooser fileChooser = new FileChooser();
            File dir = new File(System.getProperty("user.home"));
            fileChooser.setInitialDirectory(dir);
            fileChooser.setTitle("Open link file");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("NTriple files (*.nt)", "*.nt"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                tfPath.setText(file.getAbsolutePath());
            }
            spSamples.setDisable(true);
            vbFrame.getScene().setCursor(Cursor.WAIT);
            if (!file.exists() || !file.isFile()) {
                showError("File does not exist or is not a file.");
                vbFrame.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            //check link file content
            LinkFileChecker lfChecker = new LinkFileChecker();
            boolean fileOk = lfChecker.checkLinkFile(file);
            if (!fileOk) {
                showError("Error when reading file.");
                vbFrame.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            long linkCount = lfChecker.getLinkCount();
            if (linkCount < 1) {
                showError("Error too less entries found in file. Found " + linkCount);
                vbFrame.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            String linkType = lfChecker.getLinkType();
            linkFile = new LinkFile(file, linkType, linkCount);
            spSamples.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, (int) linkCount, 20, 5));
            spSamples.setDisable(false);
            vbFrame.getScene().setCursor(Cursor.DEFAULT);
        } //if cancel button, cancel
        else if (event.getSource() == btCancel) {
            successful = false;
            this.close();
        } //if finish button, finish
        else if (event.getSource() == btFinish) {
            successful = true;
            this.close();
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

    public boolean isSuccessful() {
        return successful;
    }

    public String getSelectionMethod() {
        return cbSelectionMethods.getSelectionModel().getSelectedItem();
    }

    public int getSampleCount() {
        Object o = spSamples.getValue();
        if (o instanceof Integer) { //for windows
            return (int) o;
        } else if (o instanceof Double) { //for ubuntu
            return (int) (double) o;
        }
        return -1;
    }

    public String getSource() {
        return tfSource.getText();
    }

    public String getTarget() {
        return tfTarget.getText();
    }

    public LinkFile getLinkFile() {
        return linkFile;
    }

}

package org.gesis.linkinspect;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import org.gesis.linkinspect.bl.LinkFileChecker;
import org.gesis.linkinspect.bl.Selector;
import org.gesis.linkinspect.dal.SparqlSource;
import org.gesis.linkinspect.model.ResourceProperty;
import org.gesis.linkinspect.model.Sample;
import org.gesis.linkinspect.model.Sample.State;
import org.gesis.linkinspect.model.SessionSettings;
import org.gesis.linkinspect.model.Testset;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

/**
 * Controller class for the main screen - Scene.fxml
 */
public class FXMLController implements Initializable {

    //GUI controls in Scene.fxml
    //central pane
    @FXML
    private BorderPane bpAll;

    //labels
    @FXML
    private Label lbFileValue;

    @FXML
    private Label lbSourceValue;

    @FXML
    private Label lbTargetValue;

    @FXML
    private Label lbLinkTypeValue;

    //central table left
    @FXML
    private ResourceDisplayController rdSourceController;

    //central table right
    @FXML
    private ResourceDisplayController rdTargetController;

    //navigation buttons
    @FXML
    private Button btNext;

    @FXML
    private Button btPrev;

    //toggle buttons
    @FXML
    private ToggleButton tbIncorrect;

    @FXML
    private ToggleButton tbUndecidable;

    @FXML
    private ToggleButton tbCorrect;

    //generate report button
    @FXML
    private Button btReport;

    @FXML
    private MenuItem miReport;

    //references to lower layer functions
    private Testset testSet = null;
    private SessionSettings settings = null;
    private SparqlSource src = null;
    private SparqlSource tgt = null;

    /**
     * Handles the new-Button. Shows an input dialog, reads out the data, checks
     * the data and sets up the lower layer objects.
     *
     * @param event
     */
    @FXML
    private void handleButtonAction(ActionEvent event) {
        //selector required for provision of selection methods
        Selector selector = new Selector();

        //shows a "New session" dialog
        NewDialog newDialog = new NewDialog(null, selector.getSelectionMethods());
        newDialog.showAndWait();
        boolean result = newDialog.isSuccessful();
        //check data
        if (result) {
            //check link file
            bpAll.getScene().setCursor(Cursor.WAIT);
            File file = new File(newDialog.getFilePath());
            if (!file.exists() || !file.isFile()) {
                showError("File does not exist or is not a file.");
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            //check link file content
            LinkFileChecker lfChecker = new LinkFileChecker();
            boolean fileOk = lfChecker.checkLinkFile(file);
            if (!fileOk) {
                showError("Error when reading file.");
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            long linkCount = lfChecker.getLinkCount();
            String linkType = lfChecker.getLinkType();
            //check selection method
            String selectionMethod = newDialog.getSelectionMethod();
            if (selectionMethod == null || selectionMethod.equals("")) {
                showError("Invalid selection method.");
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            //check sample count
            int sampleCount = newDialog.getSampleCount();
            if (sampleCount <= 0 || sampleCount > linkCount) {
                showError("Invalid sample count.");
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            //check if source is set
            String source = newDialog.getSource();
            if (source == null || source.equals("")) {
                showError("Invalid endpoint for source.");
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            //check if source is connectable
            if (!SparqlSource.checkConnectivity(source)) {
                showError("Unable to connect to source.");
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            //check if target is set
            String target = newDialog.getTarget();
            if (target == null || target.equals("")) {
                showError("Invalid endpoint for target.");
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            //check if target is connectable
            if (!SparqlSource.checkConnectivity(target)) {
                showError("Unable to connect to target.");
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            //setup session
            //  select a set of samples
            try {
                selector.selectFrom(selectionMethod, file, sampleCount);
                testSet = selector.generateTestSet();
            } catch (Exception ex) {
                showExceptionDialog(ex);
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            //save the settings
            settings = new SessionSettings();
            settings.setLinkFile(file);
            settings.setNrOfsamples(sampleCount);
            settings.setSelectMethod(selectionMethod);
            settings.setSrcSparqlEp(source);
            settings.setTrtSparqlEp(target);
            //create access objects to SPARQL endpoints and populate gui
            try {
                Sample sample = testSet.getSample();
                //for source
                rdSourceController.reset();
                ObservableList<ResourceProperty> ol = rdSourceController.getObservableList();
                src = new SparqlSource(new URL(source), ol);
                rdSourceController.setTitle(sample.getLeftResource());
                src.requestResource(sample.getLeftResource());
                //for target
                rdTargetController.reset();
                ObservableList<ResourceProperty> ol2 = rdTargetController.getObservableList();
                tgt = new SparqlSource(new URL(target), ol2);
                rdTargetController.setTitle(sample.getRightResource());
                tgt.requestResource(sample.getRightResource());

            } catch (MalformedURLException ex) {
                showExceptionDialog(ex);
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            } catch (RepositoryException ex) {
                showExceptionDialog(ex);
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            } catch (MalformedQueryException ex) {
                showExceptionDialog(ex);
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            } catch (QueryEvaluationException ex) {
                showExceptionDialog(ex);
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }

            //fill out labels
            lbFileValue.setText(file.getName());
            if (linkType != null) {
                lbLinkTypeValue.setText(linkType);
            } else {
                lbLinkTypeValue.setText("not set");
            }
            lbSourceValue.setText(source);
            lbTargetValue.setText(target);

            //determine button states
            determineNavigationButtonStates();
            determineToggleButtonStates();

            bpAll.getScene().setCursor(Cursor.DEFAULT);
            System.out.println("Everything went well.");
        }
    }

    /**
     * Initialized the UI-Controls
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lbFileValue.setText("Not set");
        lbSourceValue.setText("Not set");
        lbTargetValue.setText("Not set");
        lbLinkTypeValue.setText("Not set");
    }

    /**
     * Shows an error dialog
     *
     * @param msg
     */
    private void showError(String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Ooops, there was an error!");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Shows an exception dialog
     *
     * @param ex
     */
    private void showExceptionDialog(Exception ex) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText(ex.getMessage());
        alert.setContentText(ex.toString());

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    /**
     * Handles the navigation actions
     *
     * @param event
     */
    @FXML
    private void handleNavigationButtonAction(ActionEvent event) {
        //if next, set position to next
        if (event.getSource() == btNext) {
            testSet.goToNext();
        } //.. else set position to prev
        else {
            testSet.goToPrevious();
        }
        try {
            //get sample at new position and display
            Sample s = testSet.getSample();
            src.requestResource(s.getLeftResource());
            tgt.requestResource(s.getRightResource());
            rdSourceController.setTitle(s.getLeftResource());
            rdTargetController.setTitle(s.getRightResource());
        } catch (Exception ex) {
            showExceptionDialog(ex);
            System.exit(-1);
        }

        //adapt button states e.g. grey out next-button at end of set
        determineNavigationButtonStates();
        //set states of toggle buttons according to sample state
        determineToggleButtonStates();
    }

    /**
     * Sets the states of the navigation buttons according to the sample
     * displayed
     */
    private void determineNavigationButtonStates() {
        if (testSet.getSample().getState().equals(State.OPEN) || !testSet.hasNext()) {
            btNext.setDisable(true);
        } else {
            btNext.setDisable(false);
        }
        if (testSet.getSample().getState().equals(State.OPEN) || !testSet.hasPrevious()) {
            btPrev.setDisable(true);
        } else {
            btPrev.setDisable(false);
        }
    }

    /**
     * Sets the toggle buttons' states, according to the samples state
     */
    private void determineToggleButtonStates() {
        State state = testSet.getSample().getState();
        tbCorrect.setDisable(false);
        tbUndecidable.setDisable(false);
        tbIncorrect.setDisable(false);
        switch (state) {
            case OPEN:
                tbCorrect.setSelected(false);
                tbUndecidable.setSelected(false);
                tbIncorrect.setSelected(false);
                break;
            case CORRECT:
                tbCorrect.setSelected(true);
                tbUndecidable.setSelected(false);
                tbIncorrect.setSelected(false);
                break;
            case INCORRECT:
                tbCorrect.setSelected(false);
                tbUndecidable.setSelected(false);
                tbIncorrect.setSelected(true);
                break;
            case UNDECIDABLE:
                tbCorrect.setSelected(false);
                tbUndecidable.setSelected(true);
                tbIncorrect.setSelected(false);
                break;
        }
    }

    /**
     * Handles toggle button events
     *
     * @param event
     */
    @FXML
    private void handleRadioButton(ActionEvent event) {
        Sample sample = testSet.getSample();

        //handle click to correct-button
        if (event.getSource() == tbCorrect) {
            if (event.getEventType().equals(ActionEvent.ACTION)) {
                //revoke state if button was selected before
                if (sample.getState().equals(Sample.State.CORRECT)) {
                    sample.setState(Sample.State.OPEN);

                } //set new state
                else {
                    sample.setState(Sample.State.CORRECT);
                }
            }
            //s.o.
        } else if (event.getSource() == tbUndecidable) {
            if (event.getEventType().equals(ActionEvent.ACTION)) {
                if (sample.getState().equals(Sample.State.UNDECIDABLE)) {
                    sample.setState(Sample.State.OPEN);
                } else {
                    sample.setState(Sample.State.UNDECIDABLE);
                }
            }
            //s.o.
        } else if (event.getSource() == tbIncorrect) {
            if (event.getEventType().equals(ActionEvent.ACTION)) {
                if (sample.getState().equals(Sample.State.INCORRECT)) {
                    sample.setState(Sample.State.OPEN);
                } else {
                    sample.setState(Sample.State.INCORRECT);
                }
            }
        }
        //set buttons
        determineNavigationButtonStates();
        determineReportButtonStatus();
    }

    /**
     * Ungreys the report-button and report-menuitem as soon as evaluations are
     * available
     */
    private void determineReportButtonStatus() {
        if (testSet != null && testSet.getEvaluated() > 0) {
            btReport.setDisable(false);
            miReport.setDisable(false);
            return;
        }
        btReport.setDisable(true);
        miReport.setDisable(true);
    }

    /**
     * Generates TXT-document with the outcome of the inspection
     */
    @FXML
    private void generateReport() {
        //show file save dialog
        FileChooser fileChooser = new FileChooser();
        File dir = new File(System.getProperty("user.home"));
        fileChooser.setInitialDirectory(dir);
        fileChooser.setTitle("Save report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return;
        }
        try {
            PrintStream ps = new PrintStream(file);
            //calculate
            int g = testSet.getEvaluated();
            int pCorrect = testSet.getCorrect();
            int pIncorrect = testSet.getIncorrect();
            int pUndecidable = testSet.getUndecidable();
            float correctPercent = (100f / g) * pCorrect;
            float incorrectPercent = (100f / g) * pIncorrect;
            float undecidablePercent = (100f / g) * pUndecidable;
            //print to file
            ps.println("linkinspect report");
            ps.println();
            ps.println("Date: " + new Date().toString());
            ps.println("Source: " + settings.getSrcSparqlEp());
            ps.println("Target: " + settings.getTrtSparqlEp());
            ps.println("Link file: " + settings.getLinkFile().getAbsolutePath());
            ps.println("Select method: " + settings.getSelectMethod());
            ps.println("Total samples: " + g);
            ps.println("Correct: " + pCorrect);
            ps.println("Incorrect: " + pIncorrect);
            ps.println("Undecidable: " + pUndecidable);
            ps.println("Correct %: " + correctPercent);
            ps.println("Incorrect %: " + incorrectPercent);
            ps.println("Undecidable %: " + undecidablePercent);
            ps.close();
            //open file in system editor...
            if (Desktop.isDesktopSupported()
                    && !System.getProperty("os.name", "any other name").equals("Linux")) { //this does not work in ubuntu 14.04.1
                Desktop dt = Desktop.getDesktop();
                dt.open(file);
            } else {//...show just an info dialog
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Success!");
                alert.setHeaderText(null);
                alert.setContentText("A report file was created.\n" + file.getAbsolutePath());
                alert.showAndWait();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void aboutMenuHandler(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Linkinspect v0.1");
        alert.setContentText("Linkinspect was created in the context of the linked.swissbib project.\n\nVisit us on http://linked.swissbib.ch");
        alert.showAndWait();
    }

    @FXML
    public void exitHandler(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Please confirm");
        alert.setHeaderText("Please make sure you generated a report.");
        alert.setContentText("You are about to close this session. Proceed?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            System.exit(0);
        } else {
            return;
        }
    }

}

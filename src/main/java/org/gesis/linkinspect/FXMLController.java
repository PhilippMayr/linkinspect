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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.apache.logging.log4j.LogManager;
import org.gesis.linkinspect.bl.Selector;
import org.gesis.linkinspect.dal.PreferenceStorage;
import org.gesis.linkinspect.dal.ReportWriter;
import org.gesis.linkinspect.dal.SparqlSource;
import org.gesis.linkinspect.model.LinkFile;
import org.gesis.linkinspect.model.Predicate;
import org.gesis.linkinspect.model.RDFObject;
import org.gesis.linkinspect.model.ResourceProperty;
import org.gesis.linkinspect.model.Sample;
import org.gesis.linkinspect.model.Sample.State;
import org.gesis.linkinspect.model.SessionSettings;
import org.gesis.linkinspect.model.Testset;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

/**
 * @author Felix Bensmann
 * Controller class for the main screen - Scene.fxml
 */
public class FXMLController implements Initializable, OnPredicateClickListener, OnObjectClickListener {

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

    @FXML
    private Label lbProgressValue;

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

    @FXML
    private Button btLog;

    @FXML
    private MenuItem miLog;

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
        LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.DEBUG, "\"New\" button clicked.");

        //selector required for provision of selection methods
        Selector selector = new Selector();

        //shows a "New session" dialog
        LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.DEBUG, "Showing \"New\"-dialog.");
        NewDialog newDialog = new NewDialog(null, selector.getSelectionMethods());
        newDialog.showAndWait();
        boolean result = newDialog.isSuccessful();
        //check data
        if (result) {
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.INFO, "In \"New\"-dialog \"Finish\"-button clicked.");

            //check link file
            bpAll.getScene().setCursor(Cursor.WAIT);
            LinkFile linkFile = newDialog.getLinkFile();
            //check selection method
            String selectionMethod = newDialog.getSelectionMethod();
            if (selectionMethod == null || selectionMethod.equals("")) {
                LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, "Invalid selection method, aborting procedure.");
                showError("Invalid selection method.");
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            //check sample count
            int sampleCount = newDialog.getSampleCount();
            if (sampleCount <= 0 || sampleCount > linkFile.getLinkCount()) {
                LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, "Invalid sample count: " + sampleCount + " , aborting procedure.");
                showError("Invalid sample count: " + sampleCount);
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            //check if source is set
            String source = newDialog.getSource();
            if (source == null || source.equals("")) {
                LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, "Invalid endpoint for source: " + source + ", aborting procedure.");
                showError("Invalid endpoint for source: " + source);
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            //check if source is connectable
            if (!SparqlSource.checkConnectivity(source)) {
                LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, "Unable to connect to source: " + source + ", aborting procedure.");
                showError("Unable to connect to source: " + source);
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            //check if target is set
            String target = newDialog.getTarget();
            if (target == null || target.equals("")) {
                LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, "Invalid endpoint for target: " + target + ", aborting procedure.");
                showError("Invalid endpoint for target: " + target);
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            //check if target is connectable
            if (!SparqlSource.checkConnectivity(target)) {
                LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, "Unable to connect to target: " + target + ", aborting procedure.");
                showError("Unable to connect to target: " + target);
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            //setup session
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.DEBUG, "Entering session setup.");
            //  select a set of samples
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.DEBUG, "Selecting samples.");
            try {
                selector.selectFrom(selectionMethod, linkFile, sampleCount);
                testSet = selector.generateTestSet();
            } catch (Exception ex) {
                LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, ex + " abroting setup.");
                showExceptionDialog(ex);
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            //save the settings to RAM
            settings = new SessionSettings();
            settings.setLinkFile(linkFile);
            settings.setNrOfsamples(sampleCount);
            settings.setSelectMethod(selectionMethod);
            settings.setSrcSparqlEp(source);
            settings.setTrtSparqlEp(target);

            //save the settings to PreferenceStorage
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.DEBUG, "Storing preferences.");
            PreferenceStorage store = PreferenceStorage.getInstance();
            store.setSource(source);
            store.setTarget(target);

            //create access objects to SPARQL endpoints and populate gui
            try {
                //for source
                LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.DEBUG, "Setup ResourceDisplay and SPARQLSource for source.");
                rdSourceController.reset();
                rdSourceController.setOnPredicateClickListener(this);
                rdSourceController.setOnObjectClickListener(this);
                ObservableList<Object> ol = rdSourceController.getObservableList();
                src = new SparqlSource(new URL(source), ol);
                //for target
                LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.DEBUG, "Setup ResourceDisplay and SPARQLSource for target.");
                rdTargetController.reset();
                rdTargetController.setOnPredicateClickListener(this);
                rdTargetController.setOnObjectClickListener(this);
                ObservableList<Object> ol2 = rdTargetController.getObservableList();
                tgt = new SparqlSource(new URL(target), ol2);
                //load and display data
                {
                    boolean success = false;
                    while (!success) {
                        try {
                            updateTableData();
                            success = true;
                        } catch (QueryEvaluationException ex) {
                            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, "The dataset for " + testSet.getSample() + " is not valid.\nThe sample is removed from the testset.", ex);
                            showError("The dataset for " + testSet.getSample() + " is not valid.\nThe sample is removed from the testset.");
                            if (testSet.excludeSample(testSet.getSample())) {
                                success = false;
                            } else {
                                showError("Unable to switch to next sample.\nPlease save your data.");
                                break;
                            }
                        }
                    }
                    updateViews();
                }
            } catch (MalformedURLException ex) {
                LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, ex + "Aborting setup");
                showExceptionDialog(ex);
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            } catch (RepositoryException ex) {
                LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, ex + "Aborting setup");
                showExceptionDialog(ex);
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            } catch (MalformedQueryException ex) {
                LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, ex + "Aborting setup");
                showExceptionDialog(ex);
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }

            bpAll.getScene().setCursor(Cursor.DEFAULT);
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.INFO, "Setting up session done.");
        }
    }

    /**
     * Initialized the UI-Controls
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb
    ) {
        LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.DEBUG, "Initializing labels with \"not set\" text.");
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

    public void updateTableData() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        //get sample at new position and display
        Sample s = testSet.getSample();
        LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.INFO, "Load left hand resource.");
        src.requestResource(s.getLeftResource());
        LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.INFO, "Load right hand resource.");
        tgt.requestResource(s.getRightResource());
        rdSourceController.setTitle(s.getLeftResource());
        rdTargetController.setTitle(s.getRightResource());
        //refresh table view        
        rdSourceController.invokeDelayedRefresh(3000);
        rdSourceController.invokeDelayedRefresh(6000);
        rdTargetController.invokeDelayedRefresh(3000);
        rdTargetController.invokeDelayedRefresh(6000);
    }

    public void updateViews() {

        //adapt button states e.g. grey out next-button at end of set
        determineNavigationButtonStates();
        //set states of toggle buttons according to sample state
        determineToggleButtonStates();
        //determin ReportButton states
        determineReportButtonStatus();
        //update labels
        updateLabels();
        //update progress
        updateProgress();

    }

    /**
     * Updates the labels
     */
    private void updateLabels() {
        //fill out information labels
        LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.DEBUG, "Setting label texts.");
        if (settings.getLinkFile() != null && settings.getLinkFile().getFile() != null) {
            lbFileValue.setText(settings.getLinkFile().getFile().getName());
        } else {
            lbFileValue.setText("not set");
        }
        if (settings.getLinkFile() != null && settings.getLinkFile().getLinkType() != null) {
            lbLinkTypeValue.setText(settings.getLinkFile().getLinkType());
        } else {
            lbLinkTypeValue.setText("not set");
        }
        if (settings.getSrcSparqlEp() != null) {
            lbSourceValue.setText(settings.getSrcSparqlEp());
        } else {
            lbSourceValue.setText("not set");
        }
        if (settings.getTrtSparqlEp() != null) {
            lbTargetValue.setText(settings.getTrtSparqlEp());
        } else {
            lbTargetValue.setText("not set");
        }
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
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.DEBUG, "\"Next\" button clicked.");
            testSet.goToNext();
        } //.. else set position to prev
        else {
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.DEBUG, "\"Preview\" button clicked.");
            testSet.goToPrevious();
        }
        try {
            //load and display data
            {
                boolean success = false;
                while (!success) {
                    try {
                        updateTableData();
                        success = true;
                    } catch (QueryEvaluationException ex) {
                        LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, "The dataset for " + testSet.getSample() + " is not valid.\nThe sample is removed from the testset.", ex);
                        showError("The dataset for " + testSet.getSample() + " is not valid.\nThe sample is removed from the testset.");
                        if (testSet.excludeSample(testSet.getSample())) {
                            success = false;
                        } else {
                            showError("Unable to switch to next sample.\nPlease save your data.");
                            break;
                        }
                    }
                }
                updateViews();
            }
        } catch (Exception ex) {
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, "Exceptiond during resource loading: " + ex);
            showExceptionDialog(ex);
            System.exit(-1);
        }

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
        updateProgress();
    }

    /**
     * Ungreys the report-button and report-menuitem as soon as evaluations are
     * available
     */
    private void determineReportButtonStatus() {
        if (testSet != null && testSet.getEvaluated() > 0) {
            btReport.setDisable(false);
            miReport.setDisable(false);
            btLog.setDisable(false);
            miLog.setDisable(false);
            return;
        }
        btReport.setDisable(true);
        miReport.setDisable(true);
        btLog.setDisable(true);
        miLog.setDisable(true);
    }

    /**
     * Generates a TXT-document with the outcome of the inspection
     */
    @FXML
    private void generateReport() {
        LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.DEBUG, "\"Generate report\"-button clicked.");

        //show file save dialog
        FileChooser fileChooser = new FileChooser();
        File dir = new File(System.getProperty("user.home"));
        fileChooser.setInitialDirectory(dir);
        fileChooser.setTitle("Save report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.DEBUG, "No file chosen. Aborting procedure.");
            return;
        }
        try {
            ReportWriter reportWriter = new ReportWriter(testSet, settings);
            reportWriter.writeReport(file);

            //open file in system editor...
            if (Desktop.isDesktopSupported()
                    && !System.getProperty("os.name", "any other name").equals("Linux")) { //this does not work in ubuntu 14.04.1
                Desktop dt = Desktop.getDesktop();
                dt.open(file);
            } else {//...show just an info dialog
                LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.WARN, "Using desktop API is not feasable on this plattform.");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success!");
                alert.setHeaderText(null);
                alert.setContentText("A report file was created.\n" + file.getAbsolutePath());
                alert.showAndWait();
            }
        } catch (FileNotFoundException ex) {
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, ex);
        } catch (IOException ex) {
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, ex);
        }

    }

    /**
     * Writes a CSV file with every sample and its evaluation
     */
    @FXML
    private void writeLog() {
        LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.DEBUG, "\"Generate log\"-button clicked.");

        //show file save dialog
        FileChooser fileChooser = new FileChooser();
        File dir = new File(System.getProperty("user.home"));
        fileChooser.setInitialDirectory(dir);
        fileChooser.setTitle("Save log");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.DEBUG, "No file chosen. Aborting procedure.");
            return;
        }
        try {
            ReportWriter reportWriter = new ReportWriter(testSet, settings);
            reportWriter.writeCSV(file);
            //open file in system editor...
            if (Desktop.isDesktopSupported()
                    && !System.getProperty("os.name", "any other name").equals("Linux")) { //this does not work in ubuntu 14.04.1
                Desktop dt = Desktop.getDesktop();
                dt.open(file);
            } else {//...show just an info dialog
                LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.WARN, "Using desktop API is not feasable on this plattform.");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success!");
                alert.setHeaderText(null);
                alert.setContentText("A report file was created.\n" + file.getAbsolutePath());
                alert.showAndWait();
            }
        } catch (FileNotFoundException ex) {
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, ex);
        } catch (IOException ex) {
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, ex);
        }

    }

    /**
     * Reacts on the About-menu item
     *
     * @param event
     */
    @FXML
    private void aboutMenuHandler(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Linkinspect v0.1");
        alert.setContentText("Linkinspect was created in the context of the linked.swissbib project.\n\nVisit us on http://linked.swissbib.ch");
        alert.showAndWait();
    }

    /**
     * Reacts on close events and shows an "Are you sure"-dialog.
     *
     * @param event
     */
    @FXML
    public void exitHandler(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Please confirm");
        alert.setHeaderText("Please make sure you generated a report.");
        alert.setContentText("You are about to close this session. Proceed?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, "Shutting down with value 0.");
            System.exit(0);
        } else {
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, "Shuttung down aborted.");
            return;
        }
    }

    /**
     * Updates the progress label
     */
    private void updateProgress() {
        if (testSet != null) {
            float g = testSet.size();
            float p = testSet.getEvaluated();
            double p_percent = p / g * 100f;
            p_percent = Math.round(p_percent * 100.0) / 100.0;
            lbProgressValue.setText(p_percent + "%");
        } else {
            lbProgressValue.setText("0.0%");
        }
    }

    /**
     * Reacts to click on a predicate
     *
     * @param predicate
     */
    @Override
    public void onPredicateClick(Predicate predicate) {
        try {
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.INFO, "Predicate clicked. Open link in plattform web browser: " + predicate.getValue());
            Desktop desktop = Desktop.getDesktop();
            java.net.URI uri = java.net.URI.create(predicate.getValue());
            try {
                desktop.browse(uri);
            } catch (IOException ex) {
                LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, ex);
            }
        } catch (Exception ex) {
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, ex);
            showError(ex.getMessage() + "\nPlease try again.");
        }
    }

    /**
     * Reacts to click on an object
     *
     * @param object
     */
    @Override
    public void onObjectClick(RDFObject object) {
        try {
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.INFO, "Object clicked. Open link in linkinspect browser. Link: " + object.getValue() + ", Origin: " + object.getOrigin());
            if (SparqlSource.isPresent(object.getOrigin(), object.getValue())) {
                ResourceDisplayDialog browser = new ResourceDisplayDialog(object.getValue(), object.getOrigin());
                browser.showAndWait();
            } else {
                LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, "Resource: " + object.getValue() + " was not available in triplestore: " + object.getOrigin());
                showError("This resource is not available in the triplestore.");
            }
        } catch (Exception ex) {
            showError(ex.getMessage() + "\nPlease try again.");
            System.err.println(ex);
        }
    }

    /**
     * Reacts to open-extern-requests by trying to open the system web browser
     * with the given object.
     *
     * @param object
     */
    @Override
    public void onOpenExternRequest(RDFObject object) {
        LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.INFO, "Opening object extern.");
        Desktop desktop = Desktop.getDesktop();
        java.net.URI uri = java.net.URI.create(object.getValue());
        try {
            desktop.browse(uri);
        } catch (IOException ex) {
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, ex);
            showExceptionDialog(ex);
        }
    }

    /**
     * Reacts to open-extern-requests by trying to open the system web browser
     * with the given object.
     *
     * @param predicate
     */
    @Override
    public void onOpenExternRequest(Predicate predicate) {
        LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.INFO, "Opening predicate extern.");
        Desktop desktop = Desktop.getDesktop();
        java.net.URI uri = java.net.URI.create(predicate.getValue());
        try {
            desktop.browse(uri);
        } catch (IOException ex) {
            LogManager.getLogger(FXMLController.class).log(org.apache.logging.log4j.Level.ERROR, ex);
            showExceptionDialog(ex);
        }
    }

}

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
import javafx.scene.control.Label;
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

public class FXMLController implements Initializable {

    @FXML
    private BorderPane bpAll;

    @FXML
    private Label lbFileValue;

    @FXML
    private Label lbSourceValue;

    @FXML
    private Label lbTargetValue;

    @FXML
    private Label lbLinkTypeValue;

    @FXML
    private ResourceDisplayController rdSourceController;

    @FXML
    private ResourceDisplayController rdTargetController;

    @FXML
    private Button btNext;

    @FXML
    private Button btPrev;

    @FXML
    private ToggleButton tbIncorrect;

    @FXML
    private ToggleButton tbUndecidable;

    @FXML
    private ToggleButton tbCorrect;

    @FXML
    private Button btReport;

    private Testset testSet = null;
    private SessionSettings settings = null;
    private SparqlSource src = null;
    private SparqlSource tgt = null;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        Selector selector = new Selector();

        NewDialog newDialog = new NewDialog(null, selector.getSelectionMethods());
        newDialog.showAndWait();
        boolean result = newDialog.isSuccessful();
        if (result) {
            bpAll.getScene().setCursor(Cursor.WAIT);
            File file = new File(newDialog.getFilePath());
            if (!file.exists() || !file.isFile()) {
                showError("File does not exist or is not a file.");
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            LinkFileChecker lfChecker = new LinkFileChecker();
            boolean fileOk = lfChecker.checkLinkFile(file);
            if (!fileOk) {
                showError("Error when reading file.");
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            long linkCount = lfChecker.getLinkCount();
            String linkType = lfChecker.getLinkType();
            String selectionMethod = newDialog.getSelectionMethod();
            if (selectionMethod == null || selectionMethod.equals("")) {
                showError("Invalid selection method.");
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            int sampleCount = newDialog.getSampleCount();
            if (sampleCount <= 0 || sampleCount > linkCount) {
                showError("Invalid sample count.");
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            String source = newDialog.getSource();
            if (source == null || source.equals("")) {
                showError("Invalid endpoint for source.");
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            if (!SparqlSource.checkConnectivity(source)) {
                showError("Unable to connect to source.");
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            String target = newDialog.getTarget();
            if (target == null || target.equals("")) {
                showError("Invalid endpoint for target.");
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }
            if (!SparqlSource.checkConnectivity(target)) {
                showError("Unable to connect to target.");
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }

            try {
                selector.selectFrom(selectionMethod, file, sampleCount);
                testSet = selector.generateTestSet();
            } catch (Exception ex) {
                showExceptionDialog(ex);
                bpAll.getScene().setCursor(Cursor.DEFAULT);
                return;
            }

            settings = new SessionSettings();
            settings.setLinkFile(file);
            settings.setNrOfsamples(sampleCount);
            settings.setSelectMethod(selectionMethod);
            settings.setSrcSparqlEp(source);
            settings.setTrtSparqlEp(target);

            try {
                Sample sample = testSet.getSample();

                rdSourceController.reset();
                ObservableList<ResourceProperty> ol = rdSourceController.getObservableList();
                src = new SparqlSource(new URL(source), ol);
                rdSourceController.setTitle(sample.getLeftResource());
                src.requestResource(sample.getLeftResource());

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
            System.out.println("Everything went fine yet.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lbFileValue.setText("Not set");
        lbSourceValue.setText("Not set");
        lbTargetValue.setText("Not set");
        lbLinkTypeValue.setText("Not set");
    }

    private void showError(String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Ooops, there was an error!");
        alert.setContentText(msg);
        alert.showAndWait();
    }

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

    @FXML
    private void handleNavigationButtonAction(ActionEvent event) {
        if (event.getSource() == btNext) {
            testSet.goToNext();
        } else {
            testSet.goToPrevious();
        }
        try {
            Sample s = testSet.getSample();
            src.requestResource(s.getLeftResource());
            tgt.requestResource(s.getRightResource());
        } catch (Exception ex) {
            showExceptionDialog(ex);
            System.exit(-1);
        }
        determineNavigationButtonStates();
        determineToggleButtonStates();
    }

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

    @FXML
    private void handleRadioButton(ActionEvent event) {
        Sample sample = testSet.getSample();

        if (event.getSource() == tbCorrect) {
            if (event.getEventType().equals(ActionEvent.ACTION)) {
                if (sample.getState().equals(Sample.State.CORRECT)) {
                    sample.setState(Sample.State.OPEN);
                } else {
                    sample.setState(Sample.State.CORRECT);
                }
            }
        } else if (event.getSource() == tbUndecidable) {
            if (event.getEventType().equals(ActionEvent.ACTION)) {
                if (sample.getState().equals(Sample.State.UNDECIDABLE)) {
                    sample.setState(Sample.State.OPEN);
                } else {
                    sample.setState(Sample.State.UNDECIDABLE);
                }
            }
        } else if (event.getSource() == tbIncorrect) {
            if (event.getEventType().equals(ActionEvent.ACTION)) {
                if (sample.getState().equals(Sample.State.INCORRECT)) {
                    sample.setState(Sample.State.OPEN);
                } else {
                    sample.setState(Sample.State.INCORRECT);
                }
            }
        }
        determineNavigationButtonStates();
        determineReportButtonStatus();
    }

    private void determineReportButtonStatus() {
        if (testSet == null) {
            btReport.setDisable(true);
            return;
        }
        if (testSet.isComplete()) {
            btReport.setDisable(false);
        }
    }

    @FXML
    private void generateReport() {
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
            int g = testSet.size();
            int pCorrect = testSet.getCorrect();
            int pIncorrect = testSet.getIncorrect();
            int pUndecidable = testSet.getUndecidable();
            float correctPercent = (100f/g)*pCorrect;
            float incorrectPercent = (100f/g)*pIncorrect;
            float undecidablePercent = (100f/g)*pUndecidable;
            
            ps.println("linkinspect report");
            ps.println();
            ps.println("Date: "+new Date().toString());
            ps.println("Source: "+settings.getSrcSparqlEp());
            ps.println("Target: "+settings.getTrtSparqlEp());
            ps.println("Link file: "+settings.getLinkFile().getAbsolutePath());
            ps.println("Select method: "+settings.getSelectMethod());
            ps.println("Total samples: "+g);
            ps.println("Correct: "+pCorrect);
            ps.println("Incorrect: "+pIncorrect);
            ps.println("Undecidable: "+pUndecidable);
            ps.println("Correct %: "+ correctPercent);
            ps.println("Incorrect %: "+incorrectPercent);
            ps.println("Undecidable %: "+undecidablePercent);
            ps.close();
            Desktop dt = Desktop.getDesktop();
            dt.open(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }


}

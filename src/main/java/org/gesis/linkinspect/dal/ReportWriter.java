/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.dal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.gesis.linkinspect.model.Sample;
import org.gesis.linkinspect.model.SessionSettings;
import org.gesis.linkinspect.model.Testset;

/**
 *
 * @author bensmafx
 */
public class ReportWriter {

    private Testset testSet = null;
    private SessionSettings settings = null;

    public ReportWriter(Testset testSet, SessionSettings settings) {
        this.testSet = testSet;
        this.settings = settings;
    }

    public void writeReport(File file) throws FileNotFoundException, IOException {
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
        ps.println("Link file: " + settings.getLinkFile().getFile().getAbsolutePath());
        ps.println("Select method: " + settings.getSelectMethod());
        ps.println("Total samples: " + g);
        ps.println("Correct: " + pCorrect);
        ps.println("Incorrect: " + pIncorrect);
        ps.println("Undecidable: " + pUndecidable);
        ps.println("Correct %: " + correctPercent);
        ps.println("Incorrect %: " + incorrectPercent);
        ps.println("Undecidable %: " + undecidablePercent);
        ps.close();

    }

    public void writeCSV(File file) throws FileNotFoundException {
        PrintStream ps = new PrintStream(file);
        String z = ";";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        for (int i = 0; i < testSet.size(); i++) {
            Sample s = testSet.get(i);
            if (s.getState() == Sample.State.CORRECT || s.getState() == Sample.State.INCORRECT || s.getState() == Sample.State.UNDECIDABLE) {
                ps.print(s.getLeftResource());
                ps.print(z);
                ps.print(s.getRightResource());
                ps.print(z);
                ps.print(settings.getLinkFile().getLinkType());
                ps.print(z);
                ps.print(s.getState().name());
                ps.print(z);
                ps.print(settings.getSrcSparqlEp());
                ps.print(z);
                ps.print(settings.getTrtSparqlEp());
                ps.print(z);
                ps.print(sdf.format(s.getDate()));
                ps.print(s.getDate());
                ps.println();
            }
        }

        ps.close();
    }

}

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
 * http://www.gnu.org/licenses/ .
 */
package org.gesis.linkinspect.dal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.gesis.linkinspect.bl.Selector;
import org.gesis.linkinspect.model.Sample;
import org.gesis.linkinspect.model.SessionSettings;
import org.gesis.linkinspect.model.Testset;

/**
 * @author Felix Bensmann
 * Writes reports about the current state of the testset.
 */
public class ReportWriter {

    private Testset testSet = null;
    private SessionSettings settings = null;

    /**
     * ctor
     *
     * @param testSet
     * @param settings
     */
    public ReportWriter(Testset testSet, SessionSettings settings) {
        this.testSet = testSet;
        this.settings = settings;
    }

    /**
     * Writes a statistics report to the given file.
     *
     * @param file The given file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void writeReport(File file) throws FileNotFoundException, IOException {
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
        } catch (IOException ex) {
            LogManager.getLogger(ReportWriter.class).log(org.apache.logging.log4j.Level.ERROR, ex);
            throw ex;
        }
    }

    /**
     * Writes a detailed CSV report about every sample to the given file.
     *
     * @param file The given file
     * @throws FileNotFoundException
     */
    public void writeCSV(File file) throws FileNotFoundException {
        try {
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
                    ps.println();
                }
            }
            ps.close();
        } catch (IOException ex) {
            LogManager.getLogger(ReportWriter.class).log(org.apache.logging.log4j.Level.ERROR, ex);
            throw ex;
        }
    }

}

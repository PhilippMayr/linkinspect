/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.bl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import org.gesis.linkinspect.dal.LinkFileReader;
import org.gesis.linkinspect.model.LinkFile;
import org.gesis.linkinspect.model.Sample;
import org.gesis.linkinspect.model.Testset;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

/**
 * Provides methods to select a sample set from a link file.
 */
public class Selector {

    private static final String FIRST_N = "First n";
    private static final String ALGORITHM_R = "Algorithm R";

    private String[] selectionMethods = null;
    private ArrayList<Statement> selection = null;

    public Selector() {
        selectionMethods = new String[]{FIRST_N, ALGORITHM_R};
        selection = new ArrayList<Statement>();
    }

    public String[] getSelectionMethods() {
        return selectionMethods;
    }

    /**
     * Selects n triples from a link file according to the given method.
     *
     * @param method Name of method to use
     * @param file Link file
     * @param sampleCount Number of samples to select
     * @throws IOException
     * @throws RDFParseException
     * @throws RDFHandlerException
     * @throws Exception
     */
    public void selectFrom(String method, LinkFile linkFile, int sampleCount) throws IOException, RDFParseException, RDFHandlerException, Exception {
        if (sampleCount > linkFile.getLinkCount()) {
            throw new Exception("Less triples available than requested.");
        }

        if (method.equals(FIRST_N)) {
            LinkFileReader reader = new LinkFileReader(linkFile.getFile());
            reader.startReading();
            selection.clear();
            int cnt = 0;
            while (reader.hasNext()) {
                Statement st = reader.readNext();
                selection.add(st);
                cnt++;
                if (cnt == sampleCount) {
                    break;
                }
            }
            reader.close();
        } else if (method.equals(ALGORITHM_R)) {
            int[] r = algorithmR(sampleCount, (int) linkFile.getLinkCount());
            LinkFileReader reader = new LinkFileReader(linkFile.getFile());
            reader.startReading();
            selection.clear();
            int idx = 0;
            int cnt = 0;

            while (reader.hasNext()) {
                Statement st = reader.readNext();
                if (cnt == r[idx]) {
                    selection.add(st);
                    idx++;
                    if (idx == r.length) {
                        break;
                    }
                }
                cnt++;
            }

        }

    }

    private static Random ran = new Random(System.currentTimeMillis());

    private static int[] algorithmR(int k, int n) {
        
        int r[] = new int[k];
        for (int i = 0; i < k; i++) {
            r[i] = i;
        }
        int j = -1;

        for (int i = k; i < n; i++) {
            j = ran.nextInt(i);
            
            if (j < k) {
                r[j] = i;
            }
        }
        Arrays.sort(r);
        return r;
    }

    /**
     * Generates a testset object and populates it with the selected items.
     *
     * @return The testset
     * @throws IllegalStateException
     */
    public Testset generateTestSet() throws IllegalStateException {
        if (selection.isEmpty()) {
            throw new IllegalStateException("A selection has not been done.");
        }
        Testset set = new Testset();
        for (int i = 0; i < selection.size(); i++) {
            Sample s = new Sample(selection.get(i)).init(Sample.State.OPEN);
            set.add(s);
        }
        return set;
    }

}

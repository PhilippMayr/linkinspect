/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.bl;

import java.io.File;
import java.io.IOException;
import org.gesis.linkinspect.dal.LinkFileReader;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

/**
 * Class to read in a LinkFile, check if wellformed and to collect basic data.
 */
public class LinkFileChecker {

    //number of links in this file
    private long linkCount = -1;
    //type of links
    private String linkType = null;

    public LinkFileChecker() {

    }

    public String getLinkType() {
        return linkType;
    }

    /**
     * Reads in the link file, checks if it is wellformed, counts the number of
     * links and extracts the link type.
     *
     * @param file
     * @return
     */
    public boolean checkLinkFile(File file) {
        linkCount = 0;
        LinkFileReader reader = new LinkFileReader(file);
        try {
            reader.startReading();
        } catch (IOException ex) {
            System.err.println(ex);
            return false;
        } catch (RDFParseException ex) {
            System.err.println(ex);
            return false;
        } catch (RDFHandlerException ex) {
            System.err.println(ex);
            return false;
        }

        //all the link types are the same
        //no links to one self
        //all objects are URIs
        Statement first = null;
        while (reader.hasNext()) {
            linkCount++;
            Statement st = reader.readNext();
            if (first == null) {
                first = st;
                linkType = first.getPredicate().stringValue();
            }
            if (!st.getPredicate().equals(first.getPredicate())) {
                System.err.println("Link types are not homogen.");
                reader.close();
                return false;
            }
            if (!(st.getObject() instanceof URI)) {
                System.err.println("Object is not a URI.");
                reader.close();
                return false;
            }
            if (st.getSubject().stringValue().equals(st.getObject().stringValue())) {
                System.err.println("Self-link found.");
                reader.close();
                return false;
            }
        }
        reader.close();
        return true;
    }

    public long getLinkCount() {
        return linkCount;
    }

}

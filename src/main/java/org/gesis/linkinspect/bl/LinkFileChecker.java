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
package org.gesis.linkinspect.bl;

import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.gesis.linkinspect.ResourceDisplayDialog;
import org.gesis.linkinspect.dal.LinkFileReader;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

/**
 * @author Felix Bensmann
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
        LogManager.getLogger(LinkFileChecker.class).log(org.apache.logging.log4j.Level.DEBUG, "Checking file "+file.getAbsolutePath());
        linkCount = 0;
        LinkFileReader reader = new LinkFileReader(file);
        try {
            reader.startReading();
        } catch (IOException ex) {
            LogManager.getLogger(LinkFileChecker.class).log(org.apache.logging.log4j.Level.ERROR, ex);
            return false;
        } catch (RDFParseException ex) {
            LogManager.getLogger(LinkFileChecker.class).log(org.apache.logging.log4j.Level.ERROR, ex);
            return false;
        } catch (RDFHandlerException ex) {
            LogManager.getLogger(LinkFileChecker.class).log(org.apache.logging.log4j.Level.ERROR, ex);
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
                LogManager.getLogger(LinkFileChecker.class).log(org.apache.logging.log4j.Level.ERROR, "Link types are not homogen.");
                reader.close();
                return false;
            }
            if (!(st.getObject() instanceof URI)) {
                LogManager.getLogger(LinkFileChecker.class).log(org.apache.logging.log4j.Level.ERROR, "Object is not a URI.");   
                reader.close();
                return false;
            }
            if (st.getSubject().stringValue().equals(st.getObject().stringValue())) {
                LogManager.getLogger(LinkFileChecker.class).log(org.apache.logging.log4j.Level.ERROR, "Self-link found.");   
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

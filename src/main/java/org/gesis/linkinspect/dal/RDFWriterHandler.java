/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.dal;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Queue;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.rio.helpers.RDFHandlerBase;

/**
 *
 * @author bensmafx
 */
public class RDFWriterHandler extends RDFHandlerBase {

    private Queue<Statement> queue = null;

    public RDFWriterHandler(Queue<Statement> queue) {
        this.queue = queue;
    }

    @Override
    public void handleStatement(Statement st) {
        queue.add(st);
    }

}

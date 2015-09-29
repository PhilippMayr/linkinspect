package org.gesis.linkinspect.dal;

import java.util.Queue;
import org.openrdf.model.Statement;
import org.openrdf.rio.helpers.RDFHandlerBase;

/**
 * Handles a read statement by putting it to a queue container
 */
public class RDFWriterHandler extends RDFHandlerBase {

    private Queue<Statement> queue = null;

    /**
     * ctor
     * @param queue Queue to append statements to.
     */
    public RDFWriterHandler(Queue<Statement> queue) {
        this.queue = queue;
    }

    /**
     * Appends the statements to a queue.
     * @param st 
     */
    @Override
    public void handleStatement(Statement st) {
        queue.add(st);
    }

}

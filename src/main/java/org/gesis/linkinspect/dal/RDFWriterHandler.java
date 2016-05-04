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

import java.util.Queue;
import org.openrdf.model.Statement;
import org.openrdf.rio.helpers.RDFHandlerBase;

/**
 * @author Felix Bensmann
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

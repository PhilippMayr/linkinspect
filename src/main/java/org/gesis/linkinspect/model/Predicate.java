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
package org.gesis.linkinspect.model;

import org.openrdf.model.Value;

/**
 * @author Felix Bensmann
 * Represents an RDF predicate
 */
public class Predicate extends PotentialURI {

    private boolean forward = true;

    public Predicate(Value value, boolean forward) {
        this.value = value;
        this.forward = forward;
    }

    /**
     * Returns the direction of the link.
     *
     * @return True, if the link points from the given resource to another,
     * false if the given resource is reference by an external resource.
     */
    public boolean isForward() {
        return forward;
    }

}

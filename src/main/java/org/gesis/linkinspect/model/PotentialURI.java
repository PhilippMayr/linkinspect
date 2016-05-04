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

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * @author Felix Bensmann
 * Represents a value that can possibly be interpreted as a URI.
 */
public class PotentialURI {
    
    protected Value value = null;
    
    
    public boolean isURI(){
        if(value instanceof URI)
            return true;
        return false;
    }
            
    public boolean isLiteral(){
        if(value instanceof Literal)
            return true;
        return false;
    }
            
    public String getValue(){
        return value.stringValue();
    }
    
    
    
}

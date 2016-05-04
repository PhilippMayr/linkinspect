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

import java.util.ArrayList;

/**
 * @author Felix Bensmann
 * Stores and manages a list of samples. A testset has an index that points to
 * the current elements. This index can be move in steps of one.
 */
public class Testset extends ArrayList<Sample> {

    private int position = 0;

    public Testset() {
        super();
    }

    /**
     * Move index to next position
     *
     * @return True if a next position is available
     */
    public boolean goToNext() {
        if (hasNext()) {
            position++;
            return true;
        }
        return false;
    }

    /**
     * Move index to previous position
     *
     * @return True if a previous position is available
     */
    public boolean goToPrevious() {
        if (hasPrevious()) {
            position--;
            return true;
        }
        return false;
    }

    /**
     * Returns the current sample.
     *
     * @return
     */
    public Sample getSample() {
        return this.get(position);
    }

    public boolean hasNext() {
        if (position < this.size() - 1) {
            return true;
        }
        return false;
    }

    public boolean hasPrevious() {
        if (position > 0) {
            return true;
        }
        return false;
    }

    /**
     * Iterates through all elements and determines if no sample is in state
     * OPEN or UNDEFINED
     *
     * @return True if every sample is not open or undefined
     */
    public boolean isComplete() {
        for (int i = 0; i < size(); i++) {
            if (get(i).getState().equals(Sample.State.OPEN)
                    || get(i).getState().equals(Sample.State.UNDEFINED)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Counts the correct samples.
     *
     * @return Amount of correct samples.
     */
    public int getCorrect() {
        int cnt = 0;
        for (int i = 0; i < size(); i++) {
            if (get(i).getState().equals(Sample.State.CORRECT)) {
                cnt++;
            }
        }
        return cnt;
    }

    /**
     * Counts the incorrect samples.
     *
     * @return Amount of incorrect samples.
     */
    public int getIncorrect() {
        int cnt = 0;
        for (int i = 0; i < size(); i++) {
            if (get(i).getState().equals(Sample.State.INCORRECT)) {
                cnt++;
            }
        }
        return cnt;
    }

    /**
     * Count the undecidable samples.
     *
     * @return Amount of undecidable samples.
     */
    public int getUndecidable() {
        int cnt = 0;
        for (int i = 0; i < size(); i++) {
            if (get(i).getState().equals(Sample.State.UNDECIDABLE)) {
                cnt++;
            }
        }
        return cnt;
    }

    /**
     * Returns the number of samples that are CORRECT, UNDECIDABLE or INCORRECT.
     *
     * @return
     */
    public int getEvaluated() {
        int cnt = 0;
        for (int i = 0; i < size(); i++) {
            if (get(i).getState().equals(Sample.State.CORRECT)) {
                cnt++;
            } else if (get(i).getState().equals(Sample.State.UNDECIDABLE)) {
                cnt++;
            } else if (get(i).getState().equals(Sample.State.INCORRECT)) {
                cnt++;
            }
        }
        return cnt;
    }

    /**
     * Removes a sample from the testset.
     *
     * @param sample
     */
    public boolean excludeSample(Sample sample) {
        boolean success = this.remove(sample);
        if(success){
            gotoFirstOpen();
        }
        return success;
    }

    /**
     * Sets position to the first open sample in this set.
     */
    public void gotoFirstOpen(){
        position =0;
        for (int i = 0; i < size(); i++) {
            if (get(i).getState().equals(Sample.State.OPEN)) {
                position = i;
                break;
            }
        }
    }
    
    
}

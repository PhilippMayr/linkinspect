/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.model;

import java.util.ArrayList;

/**
 *
 * @author bensmafx
 */
public class Testset extends ArrayList<Sample>{
    
    private int position = 0;
    
    public Testset(){
        super();
    }
    
    public boolean goToNext(){
        if(hasNext()){
            position++;
            return true;
        }
        return false;
    }
    
    public boolean goToPrevious(){
        if(hasPrevious()){
            position--;
            return true;
        }
        return false;
    }
    
    public Sample getSample(){
        return this.get(position);
    }
    
    public boolean hasNext(){
        if( position < this.size()-1 )
            return true;
        return false;
    }
    
    public boolean hasPrevious(){
        if( position > 0 )
            return true;
        return false;
    }

    public boolean isComplete() {
        for(int i=0; i < size(); i++){
            if(get(i).getState().equals(Sample.State.OPEN) || 
                    get(i).getState().equals(Sample.State.UNDEFINED) ){
                return false;
            }
        }
        return true;
    }

    public int getCorrect() {
        int cnt = 0;
        for(int i=0; i<size();i++){
            if(get(i).getState().equals(Sample.State.CORRECT))
                cnt++;
        }
        return cnt;
    }

    public int getIncorrect() {
        int cnt = 0;
        for(int i=0; i<size();i++){
            if(get(i).getState().equals(Sample.State.INCORRECT))
                cnt++;
        }
        return cnt;
    }

    public int getUndecidable() {
        int cnt = 0;
        for(int i=0; i<size();i++){
            if(get(i).getState().equals(Sample.State.UNDECIDABLE))
                cnt++;
        }
        return cnt;
    }
    
}

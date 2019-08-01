/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
sa	 	 D5	587.329	
re komal	 D#5	622.254
re shuddha 	 E5	659.255
ga komal 	 F5	698.456
ga shuddha	 F#5	739.988
ma shuddha 	 G5	783.990
ma teevra 	 G#5	830.610
pa 		 A4	440
dha komal 	 A#4	466.163
dha shuddha 	 B4	493.883
ni komal 	 C5	523.251	
ni shuddha 	 C#5	554.36 
*/
package javaapplication3;

/**
 *
 * @author sudhanshu
 */

public class InTuneChecker {

    private double noteUpperLimit = 0; //this is upper limit of the selected practice note
    private double noteLowerLimit = 0; //this is lower limit of the selected practice note
   
    private double noteFrequency = 0;// this is current selected note

    public double getNoteFrequency() {
        return noteFrequency;
    }

    public void setNoteFrequency(double noteFrequency) {
        this.noteFrequency = noteFrequency;
    }

    public double getNoteUpperLimit() {
        return noteUpperLimit;
    }

    public void setNoteUpperLimit(double noteUpperLimit) {
        this.noteUpperLimit = noteUpperLimit;
    }

    public double getNoteLowerLimit() {
        return noteLowerLimit;
    }

    public void setNoteLowerLimit(double noteLowerLimit) {
        this.noteLowerLimit = noteLowerLimit;
    }
    
    public void setLimit(int isChanged){
        if(isChanged == 0){
            noteLowerLimit = noteFrequency-10 ;
            noteUpperLimit = noteFrequency+10 ;
        }
    }

    public int checkNote(double fzholder ){
       if( fzholder >= (noteFrequency-25)  && fzholder <= (noteFrequency+25)){ // +-25 wrt actual frequency of current selected note
           if( fzholder >= noteLowerLimit && fzholder <= noteUpperLimit){
               return 1;                                                       //return 1 for correct playing 
           }
           else{
               return 0;                                                       //return 0 for incorrect playing
           }
       }
       else{
           return 2;                                                           //return 2 if not correct nor incorrect
       }
    }
}


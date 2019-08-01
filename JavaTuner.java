package javaapplication3;


import java.nio.ByteBuffer;
	
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.nio.ByteOrder;
import java.util.ArrayList;
import javaapplication3.GuiAft;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author sudhanshu
 */
public class JavaTuner {

   TargetDataLine  microphone;
   protected static int checkResult = 2; // used for taking status of correct or incorrect note
   final int       audioFrames= 8192;  //power ^ 2 
   final float     sampleRate= 8000.0f;
   final int       bitsPerRecord= 16;
   final int       channels= 1;
   final boolean   bigEndian = true;
   final boolean   signed= true;
   static protected int turns=1000;
   byte            byteData[];     // length=audioFrames * 2
   double          doubleData[];   // length=audioFrames only reals needed for apache lib.
   AudioFormat     format;
   FastFourierTransformer transformer;
   static ArrayList<Double> recordedFz = new ArrayList<Double>();
   static ArrayList<Boolean> recordedFzFlag = new ArrayList<Boolean>(); 
   static double[] recordedFzDouble; 
   public static int getTurns() {
        return turns;
    }

    public static void setTurns(int turns) {
        JavaTuner.turns = turns;
    }

   public JavaTuner () {
        
       byteData= new byte[audioFrames * 2];  //two bytes per audio frame, 16 bits
        
       //doubleData= new double[audioFrames * 2];  // real & imaginary
       doubleData= new double[audioFrames];  // only real for apache
        
       transformer = new FastFourierTransformer(DftNormalization.STANDARD);
        
       System.out.print("Microphone initialization\n");
       format = new AudioFormat(sampleRate, bitsPerRecord, channels, signed, bigEndian);
       DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); // format is an AudioFormat object
        
       if (!AudioSystem.isLineSupported(info)) {
           System.err.print("isLineSupported failed");
           System.exit(1);
       }
        
       try {
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            System.out.print("Microphone opened with format: "+format.toString()+"\n");
            microphone.start();
       }catch(Exception ex){
           System.out.println("Microphone failed: "+ex.getMessage());
           System.exit(1);
       }
        
   }
   
   public float calHitRatio(int correctPlayed,int incorrectPlayed){
       
       float totalInput = (float)correctPlayed + (float)incorrectPlayed;
       float hitRatio = ((float)correctPlayed/totalInput)*100;
       return hitRatio;
   }
    
   public int readPcm(){
       int numBytesRead= 
               microphone.read(byteData, 0, byteData.length);
       if(numBytesRead!=byteData.length){
           System.out.println("Warning: read less bytes than buffer size");
           System.exit(1);
       }
       return numBytesRead;
   }
    
    
   public void byteToDouble(){
       ByteBuffer buf= ByteBuffer.wrap(byteData);
       buf.order(ByteOrder.BIG_ENDIAN);
       int i=0; 
        
       while(buf.remaining()>2){
           short s = buf.getShort();
           doubleData[ i ] = (new Short(s)).doubleValue();
           ++i;
       }
       //System.out.println("Parsed "+i+" doubles from "+byteData.length+" bytes");
   }
    
    
   public double findFrequency(){
       double frequency;
       Complex[] cmplx= transformer.transform(doubleData, TransformType.FORWARD);
       double real;
       double im;
       double mag[] = new double[cmplx.length];
        
       for(int i = 0; i < cmplx.length; i++){
           real = cmplx[i].getReal();
           im = cmplx[i].getImaginary();
           mag[i] = Math.sqrt((real * real) + (im*im));
       }
        
       double peak = -1.0;
       int index=-1;
       for(int i = 0; i < cmplx.length; i++){
           if(peak < mag[i]){
               index=i;
               peak= mag[i];
           }
       }
       frequency = (sampleRate * index) / audioFrames;
//       System.out.print("Index: "+index+", Frequency: "+frequency+"\n");
       return (frequency);
  }
    
   public static void main(String[] args) {
       
       JavaTuner ai= new JavaTuner();
       double fz = 0;
       float hitPercentage;
       GuiAft guiAft = new GuiAft();
       String s; // used to show the frequency
       String hitRatio;
       int indexOfRecordeFz = 0;
       int correctPlayed = 0;
       int incorrectPlayed = 0;
//       try{
//            UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
//        }   
//        catch(Exception e){
//            System.out.println("UIManager Exception : "+e);
//        }
       guiAft.setVisible(true); 
       for(int i = 0; i<=turns;i++){
            ai.readPcm();
            ai.byteToDouble();
            fz = ai.findFrequency();
            s = String.valueOf(fz);
            guiAft.setT2Text(s);
            checkResult = guiAft.checker.checkNote(fz);
            if(checkResult == 1){
                guiAft.setPanelColor("green");
                recordedFz.add(indexOfRecordeFz, fz);
                recordedFzFlag.add(indexOfRecordeFz, Boolean.TRUE);
                indexOfRecordeFz++;
                correctPlayed++;
            }
            else if(checkResult == 0){
                guiAft.setPanelColor("red");
                recordedFz.add(indexOfRecordeFz, fz);
                recordedFzFlag.add(indexOfRecordeFz, Boolean.FALSE);
                indexOfRecordeFz++;
                incorrectPlayed++;
            }
            else if(checkResult == 2){
                guiAft.setPanelColor("pink");
                
            }
            
                       
        }
       hitPercentage = ai.calHitRatio(correctPlayed,incorrectPlayed);// 3 line code for calculating hit ratio
       hitRatio = String.valueOf(hitPercentage);
       guiAft.setjTextField3(hitRatio);
      
    }
}



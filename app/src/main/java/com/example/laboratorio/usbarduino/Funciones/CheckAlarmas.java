package com.example.laboratorio.usbarduino.Funciones;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by Diego on 09/05/2015.
 */
public class CheckAlarmas extends Thread{
    Multimedia Audio;
    String Alarma,IpPublica,msg;
    ConexionIP ClienteTCP;
    int Puerto,IdRadiobase;
    MediaPlayer mpIntrusion,mpApertura,mpSensorOn,mpSensorOff,mpEnergiaOn,mpEnergiaOff,mpPersonalNo,mpEnviandoInfo;
   // String static final TAG="USB_Arduino";
    Context contex;
     String  TAG="USB_Arduino";

    public CheckAlarmas(int IdRadiobase,String Alarma,String IpPublica,int Puerto,Context contex){

        this.Alarma=Alarma;
        this.IpPublica=IpPublica;
        this.Puerto=Puerto;
        this.contex=contex;
        this.IdRadiobase=IdRadiobase;


    }

   public void run(){

       switch (Alarma){

           case "2":
               Audio =new Multimedia(contex,2);
               Audio.AudioPlay();
        msg=Mensaje(IdRadiobase,2);
            ClienteTCP=new ConexionIP(IpPublica,Puerto,msg);
 ClienteTCP.start();
     break;
           case "3":
               Audio =new Multimedia(contex,3);
               Audio.AudioPlay();
               msg=Mensaje(IdRadiobase,3);
               ClienteTCP=new ConexionIP(IpPublica,Puerto,msg);

               ClienteTCP.start();


               break;
           case "4":
               Audio =new Multimedia(contex,4);
               Audio.AudioPlay();
              msg=Mensaje(IdRadiobase,4);
               ClienteTCP=new ConexionIP(IpPublica,Puerto,msg);
               ClienteTCP.start();
               break;
           default:

               Log.d(TAG,"Alarma Default");
                    break;

       }
  Log.d(TAG,"Alarmas: "+ Alarma);

   }


    private String Mensaje(int IdRadiobase,int IdAlarma) {
        String msg=null;

        msg=" "+IdRadiobase+" "+IdAlarma;

        return msg;
    }
}

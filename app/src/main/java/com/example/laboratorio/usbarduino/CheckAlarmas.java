package com.example.laboratorio.usbarduino;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by Diego on 09/05/2015.
 */
public class CheckAlarmas extends Thread{

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

        mpIntrusion = MediaPlayer.create(contex, R.raw.alarmadeintrusion);
        mpApertura = MediaPlayer.create(contex, R.raw.alarmadeapertura);
      mpEnergiaOn = MediaPlayer.create(contex, R.raw.alarmadeenergia);
     /*   mpEnergiaOff = MediaPlayer.create(contex, R.raw.energiarestablecida);
        mpSensorOn = MediaPlayer.create(contex, R.raw.sensoresactivados);
        mpSensorOff = MediaPlayer.create(contex, R.raw.sensoresdesactivados);
        mpPersonalNo = MediaPlayer.create(contex, R.raw.personalnoautorizado);
        mpEnviandoInfo = MediaPlayer.create(contex, R.raw.enviandoinformacion);*/

    }

   public void run(){

       switch (Alarma){

           case "2":

            //   mpIntrusion.start();
             //     mCamera.takePicture(null, null, mPicture);
               // Filmacion();
               //   sendSMS("2235776581", "Alarma de Intrusin");

               msg=Mensaje(IdRadiobase,2);
            ClienteTCP=new ConexionIP(IpPublica,Puerto,msg);


               ClienteTCP.start();
     break;
           case "3":

           //    mpApertura.start();
           //      mCamera.takePicture(null, null, mPicture);
               // Filmacion();
               //   sendSMS("2235776581", "Alarma de Intrusin");

               msg=Mensaje(IdRadiobase,3);
               ClienteTCP=new ConexionIP(IpPublica,Puerto,msg);

               ClienteTCP.start();


               break;
           case "4":

            // mpEnergiaOn.start();
           //      mCamera.takePicture(null, null, mPicture);
               // Filmacion();
               //   sendSMS("2235776581", "Alarma de Intrusin");

               msg=Mensaje(IdRadiobase,4);
               ClienteTCP=new ConexionIP(IpPublica,Puerto,msg);
               ClienteTCP.start();
               break;
           default:

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

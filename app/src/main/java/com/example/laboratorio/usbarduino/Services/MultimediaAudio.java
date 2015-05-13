package com.example.laboratorio.usbarduino.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.example.laboratorio.usbarduino.R;

/**
 * Created by Diego on 10/05/2015.
 */
public class MultimediaAudio extends Service {
    MediaPlayer mp2,mp3,mp4;
    int Alarma=0;
    boolean FlagSonido=false;
   static String TAG="USB_ARDUINO";
boolean salir=false;
    @Override
    public void onCreate() {
       mp2= MediaPlayer.create(this,R.raw.alarmadeintrusion);
       mp3= MediaPlayer.create(this,R.raw.alarmadeapertura);
       mp4= MediaPlayer.create(this,R.raw.alarmadeenergia);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

      //  if (intent.getExtras().getInt("Alarma")==null){



       // }
        Alarma=intent.getExtras().getInt("Alarma");
        Log.d(TAG,"Alarma: "+Alarma);
        FlagSonido=intent.getExtras().getBoolean("FlagSonido");
        Log.d(TAG,"FlagSonido: "+FlagSonido);
        switch (Alarma){
            case 2:
                if(FlagSonido){
                mp2.start();}
                break;
            case 3:
                if(FlagSonido){
                 mp3.start();}
                break;
            case 4:
                if(FlagSonido){ mp4.start();}
                break;
            default:break;
        }
       return START_STICKY;
    }

    @Override
    public void onDestroy() {
       switch (Alarma){
            case 2:
                mp2.stop();

                break;
            case 3:
                mp3.stop();
                break;
            case 4:
                 mp4.stop();
                break;
            default:break;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {




        return null;
    }
}

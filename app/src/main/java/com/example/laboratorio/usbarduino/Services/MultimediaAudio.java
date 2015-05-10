package com.example.laboratorio.usbarduino.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import com.example.laboratorio.usbarduino.R;

/**
 * Created by Diego on 10/05/2015.
 */
public class MultimediaAudio extends Service {
    MediaPlayer mp2,mp3,mp4;
    int Alarma;
boolean salir=false;
    @Override
    public void onCreate() {
       mp2= MediaPlayer.create(this,R.raw.alarmadeintrusion);
       mp3= MediaPlayer.create(this,R.raw.alarmadeapertura);
       mp4= MediaPlayer.create(this,R.raw.alarmadeenergia);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Alarma=intent.getExtras().getInt("Alarma");
        switch (Alarma){
            case 2:
                mp2.start();
                break;
            case 3:
                 mp3.start();
                break;
            case 4:
                 mp4.start();
                break;
            default:break;
        }
       return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this,"Servicio Detenido",Toast.LENGTH_SHORT).show();
        switch (Alarma){
            case 2:
                mp2.stop();
                salir=true;
                break;
            case 3:
                mp3.stop();
                break;
            case 4:
                 mp4.stop();
                break;
            default:break;
        }
        Toast.makeText(this,"Servicio Detenido",Toast.LENGTH_SHORT).show();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

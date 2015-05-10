package com.example.laboratorio.usbarduino.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.example.laboratorio.usbarduino.ConexionIP;

/**
 * Created by Diego on 09/05/2015.
 */
public class KeepAlive  extends Service {
    static ConexionIP ClienteTCP;
    static int IdRadiobase, TiempoSeg;
    static String IpPublica;
    String TAG = "Usb Arduino";
    static boolean Bool;
    Intent intento;
    Hilo hilito;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IpPublica = intent.getExtras().getString("Ip");
        IdRadiobase = intent.getExtras().getInt("Id");
        Bool = intent.getExtras().getBoolean("bool");
        TiempoSeg = intent.getExtras().getInt("Timer");
        hilito=new Hilo();
        hilito.start();
        Toast.makeText(getApplicationContext(), "Servicio iniciado: " + Bool, Toast.LENGTH_SHORT).show();

      return START_STICKY;
    }


    @Override
    public void onDestroy() {


        Bool = false;

        Toast.makeText(getApplicationContext(), "Servicio detenido: " + Bool, Toast.LENGTH_SHORT).show();


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public static class Hilo extends Thread {



        @Override
        public void run() {
            while(Bool){
            try {
                Thread.sleep(TiempoSeg);
                ClienteTCP=new ConexionIP(IpPublica,9002," "+IdRadiobase+" 1");
                ClienteTCP.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }}
        }
    }
}
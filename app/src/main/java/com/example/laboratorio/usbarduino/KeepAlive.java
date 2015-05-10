package com.example.laboratorio.usbarduino;

import android.util.Log;

/**
 * Created by Diego on 09/05/2015.
 */
public class KeepAlive  {
    ConexionIP ClienteTCP;
    int  IdRadiobase,Puerto,TiempoSeg;
    String msg,IpPublica;
    String TAG="Usb Arduino";

    public KeepAlive(int  IdRadiobase,int Puerto,String IpPublica){

            this.IdRadiobase=IdRadiobase;
            this.Puerto=Puerto;
            this.IpPublica=IpPublica;



    }

    public void ESTOYVIVO(){
        msg=" "+IdRadiobase+" 1";
        Log.d(TAG, "Keep Alive");
        ClienteTCP=new ConexionIP(IpPublica,Puerto,msg);
        ClienteTCP.start();

    }
}

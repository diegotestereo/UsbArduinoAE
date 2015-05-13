package com.example.laboratorio.usbarduino.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.laboratorio.usbarduino.ConexionIP;

/**
 * Created by Diego on 30/04/2015.
 */
public class SistemaEnBateria extends BroadcastReceiver {
    Context contexto;

    ConexionIP ClienteTCP;
    String IP="200.51.82.70";
    int Puerto=9001;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.contexto = context;

        Toast.makeText(context, "Sistema Sobre Baterias", Toast.LENGTH_SHORT).show();
    /*   SmsManager manager = SmsManager.getDefault();
        PendingIntent sentIntent = PendingIntent.getActivity(contexto, 0, new Intent(), 0);
        PendingIntent deliveryIntent = PendingIntent.getActivity(contexto, 0, new Intent(), 0);*/
        ClienteTCP=new ConexionIP(IP,Puerto," 1 5");
        ClienteTCP.start();
     //   manager.sendTextMessage("2235776581", null, "Sistema en Baterias", sentIntent, deliveryIntent);

    }
}

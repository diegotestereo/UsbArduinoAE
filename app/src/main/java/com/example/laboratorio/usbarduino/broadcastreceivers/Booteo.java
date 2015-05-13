package com.example.laboratorio.usbarduino.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.laboratorio.usbarduino.Actividades.MainActivity;
import com.example.laboratorio.usbarduino.ConexionIP;

/**
 * Created by Diego on 30/04/2015.
 */
public class Booteo extends BroadcastReceiver {
Context contexto;
    ConexionIP ClienteTCP;
    String IP="192.168.0.109";
    int Puerto=9001;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.contexto=context;
        Intent intento= new Intent(context,MainActivity.class);
        intento.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intento);

        ClienteTCP=new ConexionIP(IP,Puerto," 1 7");
        ClienteTCP.start();
        Toast.makeText(context,"Sistema Reinicializado",Toast.LENGTH_SHORT).show();
      /*  SmsManager manager = SmsManager.getDefault();
        PendingIntent sentIntent = PendingIntent.getActivity(contexto, 0, new Intent(), 0);
        PendingIntent deliveryIntent = PendingIntent.getActivity(contexto, 0, new Intent(), 0);
        manager.sendTextMessage("2235776581", null, "Sistema Reiniciado", sentIntent, deliveryIntent);*/

    }
}

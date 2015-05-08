package com.example.laboratorio.usbarduino;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

/**
 * Created by Diego on 30/04/2015.
 */
public class Booteo extends BroadcastReceiver {
Context contexto;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.contexto=context;
        Intent intento= new Intent(context,MainActivity.class);
        intento.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intento);
        SmsManager manager = SmsManager.getDefault();
        PendingIntent sentIntent = PendingIntent.getActivity(contexto, 0, new Intent(), 0);
        PendingIntent deliveryIntent = PendingIntent.getActivity(contexto, 0, new Intent(), 0);
     //   manager.sendTextMessage("2235776581", null, "Sistema Reiniciado", sentIntent, deliveryIntent);

    }
}

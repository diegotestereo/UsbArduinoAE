package com.example.laboratorio.usbarduino;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by Diego on 30/04/2015.
 */
public class SistemaEnBateria extends BroadcastReceiver {
    Context contexto;
    MediaPlayer mpEnergiaOff;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.contexto=context;
        //mpEnergiaOff = MediaPlayer.create(contexto, R.raw.alarmadeenergia);
       // mpEnergiaOff.start();
        Toast.makeText(context,"Sistema Sobre Baterias",Toast.LENGTH_SHORT).show();
        SmsManager manager = SmsManager.getDefault();
        PendingIntent sentIntent = PendingIntent.getActivity(contexto, 0, new Intent(), 0);
        PendingIntent deliveryIntent = PendingIntent.getActivity(contexto, 0, new Intent(), 0);
     //   manager.sendTextMessage("2235776581", null, "Sistema en Baterias", sentIntent, deliveryIntent);

    }
}

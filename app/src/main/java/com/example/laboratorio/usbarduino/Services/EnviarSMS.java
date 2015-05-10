package com.example.laboratorio.usbarduino.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;

/**
 * Created by Diego on 10/05/2015.
 */
public class EnviarSMS extends Service {




    @Override
    public IBinder onBind(Intent intent) {

        SmsManager manager = SmsManager.getDefault();
        PendingIntent sentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
        PendingIntent deliveryIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);




        return null;
    }
}

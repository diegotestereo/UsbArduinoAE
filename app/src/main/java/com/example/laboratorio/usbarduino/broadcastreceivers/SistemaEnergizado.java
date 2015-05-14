package com.example.laboratorio.usbarduino.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.laboratorio.usbarduino.ConexionIP;

/**
 * Created by Diego on 30/04/2015.
 */
public class SistemaEnergizado extends BroadcastReceiver {
    Context contexto;

    ConexionIP ClienteTCP;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.contexto = context;

        SharedPreferences mispreferencias = context.getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        String IP = mispreferencias.getString("edit_IP", "localhost");

        int Puerto = Integer.parseInt(mispreferencias.getString("edit_Port", "9001"));

        Toast.makeText(context, "Sistema Energizado", Toast.LENGTH_SHORT).show();
        ClienteTCP = new ConexionIP(IP, Puerto, " 1 6");
        ClienteTCP.start();

    }
}

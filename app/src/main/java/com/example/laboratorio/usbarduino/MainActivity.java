package com.example.laboratorio.usbarduino;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.nio.ByteBuffer;

public class MainActivity extends ActionBarActivity implements Runnable{

    private static final String TAG = "AndroidUsbHostArduino";

    private static final byte IGNORE_00 = (byte) 0x00;
    private static final byte SYNC_WORD = (byte) 0xFF;

    private static final int CMD_LED_OFF = 2;
    private static final int CMD_LED_ON = 1;
    private static final int CMD_TEXT = 3;
    private static final int MAX_TEXT_LENGTH = 16;

    ToggleButton buttonLed,toogleAlarma;
    EditText textOut;
    Button buttonSend,buttonSonido;
    TextView textIn,textAlarma1;

    String stringToRx;

    private UsbManager usbManager;
    private UsbDevice deviceFound;
    private UsbDeviceConnection usbDeviceConnection;
    private UsbInterface usbInterfaceFound = null;
    private UsbEndpoint endpointOut = null;
    private UsbEndpoint endpointIn = null;
    private Thread RX,Audio;
    private MediaPlayer mp1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mp1 = MediaPlayer.create(this, R.raw.intrusion);


        LevantarXML();
        Botones();




        RX=new Thread( new Runnable() {
            @Override
            public void run() {
                while(true){
                   try {
                      Thread.sleep(100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(textIn.getText().toString().equals("A")){
                                textAlarma1.setText("1");
                                    mp1.start();
                                /*    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }*/
                                }else{textAlarma1.setText("-");
                                    mp1.stop();
                                }
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                   }

                }
            }
        });
 //      RX.start();


        usbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
    }

    private void Botones() {


        buttonSonido.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mp1.start();

            }
        });


        buttonLed.setOnCheckedChangeListener(new OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    sendArduinoCommand(CMD_LED_ON);
                }else{
                    sendArduinoCommand(CMD_LED_OFF);
                }
            }});

        buttonSend.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                final String textToSend = textOut.getText().toString();
                if(textToSend!=""){
                    stringToRx = "";
                    textIn.setText("");
                    Thread threadsendArduinoText =
                            new Thread(new Runnable(){

                                @Override
                                public void run() {
                                    sendArduinoText(textToSend);

                                }});
                    threadsendArduinoText.start();
                }

            }});
        toogleAlarma.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    textIn.setText("A");
                }else{
                    textIn.setText("-");
                }
            }
        });

    }

    private void LevantarXML() {
        textAlarma1=(TextView)findViewById(R.id.textAlarma1);
       textOut = (EditText)findViewById(R.id.textout);
        textIn = (TextView)findViewById(R.id.textin);
        buttonSend = (Button)findViewById(R.id.send);
        buttonLed = (ToggleButton)findViewById(R.id.arduinoled);
        toogleAlarma= (ToggleButton) findViewById(R.id.toggleAlarma);
        buttonSonido= (Button)findViewById(R.id.btn_Sonido);
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String action = intent.getAction();

        UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            setDevice(device);
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            if (deviceFound != null && deviceFound.equals(device)) {
                setDevice(null);
            }
        }
    }

    private void setDevice(UsbDevice device) {
        usbInterfaceFound = null;
        endpointOut = null;
        endpointIn = null;

        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface usbif = device.getInterface(i);

            UsbEndpoint tOut = null;
            UsbEndpoint tIn = null;

            int tEndpointCnt = usbif.getEndpointCount();
            if (tEndpointCnt >= 2) {
                for (int j = 0; j < tEndpointCnt; j++) {
                    if (usbif.getEndpoint(j).getType()
                            == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        if (usbif.getEndpoint(j).getDirection()
                                == UsbConstants.USB_DIR_OUT) {
                            tOut = usbif.getEndpoint(j);
                        } else if (usbif.getEndpoint(j).getDirection()
                                == UsbConstants.USB_DIR_IN) {
                            tIn = usbif.getEndpoint(j);
                        }
                    }
                }

                if (tOut != null && tIn != null) {
                    // This interface have both USB_DIR_OUT
                    // and USB_DIR_IN of USB_ENDPOINT_XFER_BULK
                    usbInterfaceFound = usbif;
                    endpointOut = tOut;
                    endpointIn = tIn;
                }
            }
        }

        if (usbInterfaceFound == null) {
            return;
        }

        deviceFound = device;

        if (device != null) {
            UsbDeviceConnection connection =
                    usbManager.openDevice(device);
            if (connection != null &&
                    connection.claimInterface(usbInterfaceFound, true)) {

                connection.controlTransfer(0x21, 34, 0, 0, null, 0, 0);
                connection.controlTransfer(0x21, 32, 0, 0,
                        new byte[] { (byte) 0x80, 0x25, 0x00,
                                0x00, 0x00, 0x00, 0x08 },
                        7, 0);

                usbDeviceConnection = connection;
                Thread thread = new Thread(this);
                thread.start();

            } else {
                usbDeviceConnection = null;
            }
        }
    }

    private void sendArduinoCommand(int control) {
        synchronized (this) {

            if (usbDeviceConnection != null) {
                byte[] message = new byte[1];
            //    byte message = (byte)control;
                message[0] = (byte)control;

            //    message[0] = SYNC_WORD;
             //   message[1] = (byte)control;

                usbDeviceConnection.bulkTransfer(endpointOut,
                        message, message.length, 0);

                Log.d(TAG, "sendArduinoCommand: " + String.valueOf(control));
            }
        }
    }

    private void sendArduinoText(String s) {
        synchronized (this) {

            if (usbDeviceConnection != null) {

                Log.d(TAG, "sendArduinoText: " + s);

                int length = s.length();
                if(length>MAX_TEXT_LENGTH){
                    length = MAX_TEXT_LENGTH;
                }
                byte[] message = new byte[length + 3];
                message[0] = SYNC_WORD;
                message[1] = (byte)CMD_TEXT;
                message[2] = (byte)length;
                s.getBytes(0, length, message, 3);

                /*
                usbDeviceConnection.bulkTransfer(endpointOut,
                		message, message.length, 0);
                */

                byte[] b = new byte[1];
                for(int i=0; i< length+3; i++){
                    b[0] = message[i];
                    Log.d(TAG, "sendArduinoTextb[0]: " + b[0]);
                    usbDeviceConnection.bulkTransfer(endpointOut,
                            b, 1, 0);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void run() {

        ByteBuffer buffer = ByteBuffer.allocate(1);
        UsbRequest request = new UsbRequest();
        request.initialize(usbDeviceConnection, endpointIn);
        while (true) {

            request.queue(buffer, 1);
            if (usbDeviceConnection.requestWait() == request) {
                 byte dataRx = buffer.get(0);

               if(dataRx==0){
                        Log.d("dataRx==0","dataRx:"+dataRx);

               }else{
                   stringToRx="";
                   stringToRx+=(char)dataRx;

                   runOnUiThread(new Runnable(){

                       @Override
                       public void run() {
                           textIn.setText(stringToRx);
                           if(textIn.getText().toString().equals("A")){
                               textAlarma1.setText("si");
                               mp1.start();

                           }else{textAlarma1.setText("no");
                               mp1.stop();
                           }

                       }});

               }


                }

        }



    }

}

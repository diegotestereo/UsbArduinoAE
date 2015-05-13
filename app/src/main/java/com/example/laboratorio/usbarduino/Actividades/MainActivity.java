package com.example.laboratorio.usbarduino.Actividades;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.laboratorio.usbarduino.CheckAlarmas;
import com.example.laboratorio.usbarduino.R;
import com.example.laboratorio.usbarduino.Services.KeepAlive;
import com.example.laboratorio.usbarduino.Services.MultimediaAudio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends ActionBarActivity implements Runnable {


    private static final byte IGNORE_00 = (byte) 0x00;
    private static final byte SYNC_WORD = (byte) 0xFF;

    private static final int CMD_LED_OFF = 2;
    private static final int CMD_LED_ON = 1;
    private static final int CMD_TEXT = 3;
    private static final int MAX_TEXT_LENGTH = 16;

    ToggleButton buttonLed,toggleAudio, toogleAlarma,toggle_ka;
    Switch switch_button;
    EditText textOut,edit_IP,edit_Port,edit_IdRadio,textIn,edit_TimerKA,edit_PortKA;
    Button buttonSend, btn_Prueba, btn_Foto, btn_Video, btn_Intrusion;
    Button btn_Energia,btn_Apertura;
    TextView  textAlarma1;

    String stringToRx;
    File ImagenFile;
    byte dataRx;// datos entrantes al usb del telefono
    private FrameLayout preview;
    private SurfaceView mPreview;
    Boolean isRecording = false;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    Camera.Parameters parameters;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private int calidadFoto = 90;
    private  Socket socket;

    private UsbManager usbManager;
    private UsbDevice deviceFound;
    private UsbDeviceConnection usbDeviceConnection;
    private UsbInterface usbInterfaceFound = null;
    private UsbEndpoint endpointOut = null;
    private UsbEndpoint endpointIn = null;
    private Thread RX, Audio;
    public MediaPlayer mpIntrusion,mpApertura,mpSensorOn,mpSensorOff,mpEnergiaOn,mpEnergiaOff,mpPersonalNo,mpEnviandoInfo;
    static final String TAG = "USB_ARDUINO";


    int  IdRadiobase;
    Intent intentMultimedia,intentKeepAlive;

    String IpPublica;

    // String foto = Environment.getExternalStorageDirectory() + "/Radiobase.jpg";
    //   private File ImagenFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LevantarXML();
        Botones();
        CAMARA_ON();
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        IdRadiobase=Integer.parseInt(edit_IdRadio.getText().toString());
        IpPublica=edit_IP.getText().toString();
        BotonesEnabled(false);
        Log.d(TAG, "OnCreate");
    }
    @Override
    public void onResume() {
        super.onResume();


       Intent intent = getIntent();
        String action = intent.getAction();

        UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            setDevice(device);
            Toast.makeText(getApplicationContext(), "USB Conectado", Toast.LENGTH_SHORT).show();
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            Toast.makeText(getApplicationContext(), "USB Desconectado", Toast.LENGTH_SHORT).show();

            if (deviceFound != null && deviceFound.equals(device)) {
                setDevice(null);
            }
        }
        Log.d(TAG, "OnResume");

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OnDestroy");
       mCamera.release();

    }

    private void CAMARA_ON() {
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(getApplicationContext(), mCamera);
        preview.addView(mPreview);
    }

    ////////////////////////// SMS ++++++++////////////////////

    private void sendSMS(String phoneNumber, String message) {

        SmsManager manager = SmsManager.getDefault();
        PendingIntent sentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
        PendingIntent deliveryIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
        manager.sendTextMessage(phoneNumber, null, message, sentIntent, deliveryIntent);

    }

    ///////////////7//////// SMS ----////////////////////
    private void Botones() {


        switch_button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                       Log.d(TAG, "Alarma Activada");
        //     mpSensorOn.start();


                } else {
                    Log.d(TAG, "Alarma Desac" +
                            "tivada");
        //    mpSensorOff.start();
                }
            }
        });

        btn_Foto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
                Log.d(TAG, "Boton de Foto");

            }


        });

        btn_Video.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
// initialize video camera
                Filmacion();
                Log.d(TAG, "Boton de Video");
            }
        });

        btn_Prueba.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
               //sendSMS(TelDiego, Alarma_1);
                Log.d(TAG, "Boton de foto alarma");


            edit_IP.setText("200.51.82.70");
                Toast.makeText(getApplicationContext(),"IP Publica: 200.51.82.70",Toast.LENGTH_SHORT).show();


            }
        });

        btn_Intrusion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "Alarma Simulada");
                mCamera.takePicture(null, null, mPicture);
                textIn.setText("2");
                intentMultimedia=new Intent(getApplicationContext(), MultimediaAudio.class);
                intentMultimedia.putExtra("Alarma", 2);
                toggleAudio.isChecked();
                if( toggleAudio.isChecked()){
                    intentMultimedia.putExtra("FlagSonido",true);}else{
                    intentMultimedia.putExtra("FlagSonido",false);}
                startService(intentMultimedia);
                startService(intentMultimedia);
            }
        });
        btn_Apertura.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

               //sendSMS(TelDiego, Alarma_1);
                Log.d(TAG, "Boton de Apertura");

                textIn.setText("3");
                intentMultimedia=new Intent(getApplicationContext(), MultimediaAudio.class);
                intentMultimedia.putExtra("Alarma", 3);
                //intentMultimedia.putExtra("FlagSonido",true);
                toggleAudio.isChecked();
                if (toggleAudio.isChecked()){
                    intentMultimedia.putExtra("FlagSonido",true);}else{
                    intentMultimedia.putExtra("FlagSonido",false);}
                startService(intentMultimedia);
                startService(intentMultimedia);

            }
        });
        btn_Energia.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
           //     mpEnergiaOn.start();
                //sendSMS(TelDiego, Alarma_1);
                mCamera.takePicture(null, null, mPicture);
                Log.d(TAG, "Boton de Energia");
                textIn.setText("4");
                intentMultimedia=new Intent(getApplicationContext(), MultimediaAudio.class);
                intentMultimedia.putExtra("Alarma", 4);
                toggleAudio.isChecked();
                if( toggleAudio.isChecked()){
                intentMultimedia.putExtra("FlagSonido",true);}else{
                intentMultimedia.putExtra("FlagSonido",false);}
                startService(intentMultimedia);

            }
        });

        buttonSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final String textToSend = textOut.getText().toString();
                if (textToSend != "") {
                    stringToRx = "";
                    textIn.setText("");
                    Thread threadsendArduinoText =
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    sendArduinoText(textToSend);
                                    Log.d(TAG, "Texto:'" + textToSend + "' enviado al Arduino");
                                    Log.d(TAG, "");

                                }
                            });
                    threadsendArduinoText.start();
                }

            }
        });
        toggleAudio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.d(TAG,"Audio ON");
                    Toast.makeText(getApplicationContext(),"Audio ON",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Audio ON",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"Audio Off");
                }
            }
        });
        toggle_ka.setOnCheckedChangeListener(new OnCheckedChangeListener() {



            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if(isChecked){
                    IdRadiobase=Integer.parseInt(edit_IdRadio.getText().toString());
                    IpPublica=edit_IP.getText().toString();

                    int Timer=Integer.parseInt(edit_TimerKA.getText().toString());
                    intentKeepAlive=new Intent(getApplicationContext(), KeepAlive.class);
                    intentKeepAlive.putExtra("Id",IdRadiobase );
                    intentKeepAlive.putExtra("Ip", IpPublica);
                    intentKeepAlive.putExtra("PuertoKA",Integer.parseInt(edit_PortKA.getText().toString()));
                    intentKeepAlive.putExtra("bool",true);
                    intentKeepAlive.putExtra("Timer", Timer);
                    startService(intentKeepAlive);
                    BotonesEnabled(isChecked);
                }else{
                 stopService(intentKeepAlive);
                    edit_TimerKA.setEnabled(true);
                    intentKeepAlive.putExtra("bool",false);
                    BotonesEnabled(isChecked);

                }


            }
        });

        textIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG,"Cambio el texto: "+s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                Log.d(TAG, "Alarmeta");
                String IP=edit_IP.getText().toString();
                int Port=Integer.parseInt(edit_Port.getText().toString());
                int IdRadiobase=Integer.parseInt(edit_IdRadio.getText().toString());
                Log.d(TAG,"IdRadiobase:"+IdRadiobase);
                String Alarma=textIn.getText().toString();
                CheckAlarmas CheckAlarmita=new CheckAlarmas(IdRadiobase,Alarma,IP,Port,getApplicationContext());
                CheckAlarmita.start();



            }
        });


    }

    public void Filmacion() {
        if (isRecording) {
            // stop recording and release camera
            mMediaRecorder.stop();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            // inform the user that recording has stopped
            btn_Video.setText("Rec");
            isRecording = false;
            Log.d(TAG, "Filmacion Detenida");

        } else {
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording

                mMediaRecorder.start();
                Log.d(TAG, "Filmacion Comenzada");

                // inform the user that recording has started
                btn_Video.setText("Stop");
                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                Log.d(TAG, "Se libero el MadiaRecorder");

                // inform user
            }
        }


    }

    private void LevantarXML() {
        textAlarma1 = (TextView) findViewById(R.id.textAlarma1);
        textOut = (EditText) findViewById(R.id.textout);
        textIn = (EditText) findViewById(R.id.textin);

        edit_IP =(EditText)findViewById(R.id.edit_IP);
        edit_Port=(EditText)findViewById(R.id.edit_Port);
        edit_IdRadio =(EditText)findViewById(R.id.edit_IdRadio);
        edit_TimerKA= (EditText) findViewById(R.id.edit_TimerKA);
        edit_PortKA=(EditText)findViewById(R.id.edit_PortKA);

        toggle_ka= (ToggleButton) findViewById(R.id.toggle_ka);
        toggleAudio= (ToggleButton) findViewById(R.id.toggleAudio);

        buttonSend = (Button) findViewById(R.id.send);
        btn_Foto = (Button) findViewById(R.id.btn_Captura);
        btn_Video = (Button) findViewById(R.id.btn_Video);
        btn_Intrusion = (Button) findViewById(R.id.btn_Intrusion);
        btn_Energia = (Button) findViewById(R.id.btn_Energia);
        btn_Prueba = (Button) findViewById(R.id.btn_Prueba);
        btn_Apertura = (Button) findViewById(R.id.btn_Apertura);

        switch_button = (Switch) findViewById(R.id.switch_Alarma);

        preview = (FrameLayout) findViewById(R.id.camera_preview);

        mPreview = (SurfaceView) findViewById(R.id.surfaceView);

        mpIntrusion = MediaPlayer.create(this, R.raw.alarmadeintrusion);
        mpApertura = MediaPlayer.create(this, R.raw.alarmadeapertura);
        mpEnergiaOn = MediaPlayer.create(this, R.raw.alarmadeenergia);
        mpEnergiaOff = MediaPlayer.create(this, R.raw.energiarestablecida);
        mpSensorOn = MediaPlayer.create(this, R.raw.sensoresactivados);
        mpSensorOff = MediaPlayer.create(this, R.raw.sensoresdesactivados);
        mpPersonalNo = MediaPlayer.create(this, R.raw.personalnoautorizado);
        mpEnviandoInfo = MediaPlayer.create(this, R.raw.enviandoinformacion);



        Log.d(TAG, "XML LEvantado");

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
                        new byte[]{(byte) 0x80, 0x25, 0x00,
                                0x00, 0x00, 0x00, 0x08},
                        7, 0);

                usbDeviceConnection = connection;
                Thread thread = new Thread(this);

                thread.start();
                Log.d(TAG, "USB Detectado");

            } else {
                usbDeviceConnection = null;
                Log.d(TAG, "USB Desconectado");

            }
        }
    }

    private void sendArduinoCommand(int control) {
        synchronized (this) {

            if (usbDeviceConnection != null) {
                byte[] message = new byte[1];
                //    byte message = (byte)control;
                message[0] = (byte) control;

                //    message[0] = SYNC_WORD;
                //   message[1] = (byte)control;

                usbDeviceConnection.bulkTransfer(endpointOut,
                        message, message.length, 0);


            }
        }
    }

    private void sendArduinoText(String s) {
        synchronized (this) {

            if (usbDeviceConnection != null) {

                Log.d(TAG, "Texto: '" + s + "' enviado");


                int length = s.length();
                if (length > MAX_TEXT_LENGTH) {
                    length = MAX_TEXT_LENGTH;
                }
                byte[] message = new byte[length + 3];
                message[0] = SYNC_WORD;
                message[1] = (byte) CMD_TEXT;
                message[2] = (byte) length;
                s.getBytes(0, length, message, 3);

                /*
                usbDeviceConnection.bulkTransfer(endpointOut,
                		message, message.length, 0);
                */

                byte[] b = new byte[1];
                for (int i = 0; i < length + 3; i++) {
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
                dataRx = buffer.get(0);

                if (dataRx == 0) {
                    Log.d("dataRx==0", "dataRx:" + dataRx);

                } else {
                    stringToRx = "";
                    stringToRx += (char) dataRx;

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            textIn.setText(stringToRx);
                            Log.d(TAG, "Mensaje: '" + stringToRx + "' recibido del USB");
                            Log.d(TAG, "");
                        }
                    });
                }
            }
        }
    }
    ///////////////  CheckAlarmas///////////////

    ////////////////////////// ++++   FOTO y VIDEO +++++ /////////////////

    public void PARAMETROS() {

        parameters =mCamera.getParameters();
        if(parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
        {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

        }
        if(parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
        {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        parameters.setJpegQuality(calidadFoto);
        parameters.setVideoStabilization(true);

        mCamera.setParameters(parameters);
        Log.d(TAG,"Parametros de la Camara Cargados");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //  releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        //     releaseCamera();              // release the camera immediately on pause event
        Log.d(TAG,"On Pause");
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            Log.d(TAG,"Camara Abierta");
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.d(TAG,"Camara Cerrada");
        }
        return c; // returns null if camera is unavailable
    }

    /** A basic Camera preview class */
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here
            mCamera.setDisplayOrientation(90);
            PARAMETROS();

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: ");// +  e.getMessage());
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
            Log.d(TAG,"Camara Liberada");
        }
    }

    private boolean prepareVideoRecorder(){


        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
            Log.d("prepareVideoRecorder", " mMediaRecorder.prepare()");
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use

        }
    }
    /////////////////////////////////////////////////////////////

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Radiobases");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


    //////////////////////////--- FOTO Y VIDEO ----//////////////////////


    private void BotonesEnabled (boolean ena){

        edit_IdRadio.setEnabled(!ena);
        edit_IP.setEnabled(!ena);
        edit_Port.setEnabled(!ena);
        edit_PortKA.setEnabled(!ena);
        edit_TimerKA.setEnabled(!ena);

    }



}

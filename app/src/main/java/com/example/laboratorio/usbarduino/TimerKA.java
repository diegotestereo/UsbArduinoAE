package com.example.laboratorio.usbarduino;

import android.os.AsyncTask;
import android.util.Log;

import com.example.laboratorio.usbarduino.Services.KeepAlive;

/**
 * Created by Diego on 09/05/2015.
 */
public class TimerKA extends AsyncTask {



    int Puerto=9001;
    int IdRadiobase;
  String IpPublica;


    static final String TAG = "TimerKA";

   public TimerKA(  int  IdRadiobase,String IpPublica){

       this.IpPublica=IpPublica;
       this.IdRadiobase=IdRadiobase;


   }





    @Override



    protected Object doInBackground(Object[] params) {

        while(!isCancelled()){
         //   if(isCancelled()){
           //     Log.d(TAG,"isCancelled()="+isCancelled());
            //    break;}else{
            Log.d(TAG,"isCancelled 1 ()="+isCancelled());
            try {
                 Thread.sleep(5000);
                KeepAlive KA =new KeepAlive(IdRadiobase,Puerto,IpPublica);
                Log.d(TAG,"isCancelled 2 ()="+isCancelled());
                KA.ESTOYVIVO();
                Log.d(TAG, "isCancelled 3 ()=" + isCancelled());



            } catch (InterruptedException e) {
                Log.d(TAG,""+e);
                e.printStackTrace();
                Log.d(TAG, "" + e);

            }

            }

        return null;
        }





    @Override
    protected void onCancelled() {


      Log.d("TIMERKA","cancelado");
    }
}

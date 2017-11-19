package com.example.tongyu.mysqldemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * Created by tongyu on 11/18/17.
 */

public class ShakeGame extends AppCompatActivity implements SensorEventListener {
    TextView score, countTime;
    CountDownTimer mCountDownTimer;
    private Sensor mLinearAccelerometer;
    private SensorManager mSensorManager;
    int maximum = 0;
    TextToSpeech speech;
    String username;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2);
        score = (TextView) findViewById(R.id.tv_shake_score);
        countTime = (TextView) findViewById(R.id.tv_shake_counter);

        Intent intent = this.getIntent();
        username = intent.getExtras().getString("username");

        speech=new TextToSpeech(ShakeGame.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result=speech.setLanguage(Locale.US);
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "This Language is not supported");
                    }
                }
                else
                    Log.e("error", "Initilization Failed!");
            }
        });

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mLinearAccelerometer = mSensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION).get(0);

        startTimer();

    }

    public void startTimer()
    {
        mCountDownTimer = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                if(seconds >7 && seconds < 8.3)
                    speech.speak("Shake as hard as you can", TextToSpeech.QUEUE_FLUSH, null);
                if(seconds< 6)
                    countTime.setText("" + seconds);
            }

            public void onFinish() {
                countTime.setText("Time up");
                unRegisterListeners();

                InsertShakeScore insertShakeScore = new InsertShakeScore(ShakeGame.this);
                insertShakeScore.execute("insert", username, String.valueOf(maximum));

                //TODO: insert to database, go back


            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(speech != null){

            speech.stop();
            speech.shutdown();
        }
        super.onPause();
        unRegisterListeners();

    }

    public void unRegisterListeners()
    {
        mSensorManager.unregisterListener(this, mLinearAccelerometer);
    }

    public void registerListeners()
    {
        mSensorManager.registerListener(this, mLinearAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  // Respond to the action bar's Up/Home button
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        synchronized (this) {
            int xx = Math.round(Math.abs(event.values[0]));
            int yy = Math.round(Math.abs(event.values[1]));
            int zz = Math.round(Math.abs(event.values[2]));

            if (xx > yy && xx > zz && xx > maximum) {
                maximum = xx;
                score.setText(String.valueOf(maximum));
            } else if (yy > xx && yy > zz && yy > maximum) {
                maximum = yy;
                score.setText(String.valueOf(maximum));
            } else if (zz > xx && zz > yy && zz > maximum) {
                maximum = zz;
                score.setText(String.valueOf(maximum));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class InsertShakeScore extends AsyncTask<String,Void,String> {
        Context context;
        AlertDialog alertDialog;

        InsertShakeScore(Context ctx) {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            String type = params[0];
            String login_url = "https://uarkacm.000webhostapp.com/insertshakescore.php";
            if (type.equals("insert")) {
                try {
                    String user_name = params[1];
                    String score = params[2];

                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8") + "&"
                            + URLEncoder.encode("score", "UTF-8") + "=" + URLEncoder.encode(score, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result = "";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                        Log.d("Test", result);
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Loading score");
        }

        @Override
        protected void onPostExecute(String result) {
            alertDialog.setMessage(result);
            alertDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}

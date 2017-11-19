package com.example.tongyu.mysqldemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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

/**
 * Created by tongyu on 11/18/17.
 */

public class SpecificPostActivity extends AppCompatActivity {

    EditText posts;
    String username;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_post);
        posts = (EditText) findViewById(R.id.ed_specific_text);
        Intent intent = getIntent();
        username = intent.getExtras().getString("username");
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

    public void onClickSpecificPost(View v)
    {
        String postcontent = posts.getText().toString();
        InsertPost insertPost = new InsertPost(this);
        insertPost.execute("insert", username, postcontent );

    }

    class InsertPost extends AsyncTask<String,Void,String> {
        Context context;
        AlertDialog alertDialog;

        InsertPost(Context ctx) {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            String type = params[0];
            String login_url = "https://uarkacm.000webhostapp.com/insertpost.php";
            if (type.equals("insert")) {
                try {
                    String user_name = params[1];
                    String postcontent = params[2];


                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8") + "&"
                            + URLEncoder.encode("postcontent", "UTF-8") + "=" + URLEncoder.encode(postcontent, "UTF-8");
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
            alertDialog.setTitle("Inserting");
        }

        @Override
        protected void onPostExecute(String result) {
            alertDialog.setMessage(result);
            alertDialog.show();
            //Intent intent = new Intent(NewUserActivity.this, MainActivity.class);
            //startActivity(intent);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}

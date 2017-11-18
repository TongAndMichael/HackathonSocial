package com.example.tongyu.mysqldemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class NewUserActivity extends AppCompatActivity {

    EditText edUsername, edPassword, edAge, edSurename, edName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        edUsername = (EditText)findViewById(R.id.ed_username);
        edPassword = (EditText)findViewById(R.id.ed_password);
        edAge = (EditText) findViewById(R.id.ed_age);
        edSurename = (EditText) findViewById(R.id.ed_surename);
        edName = (EditText) findViewById(R.id.ed_name);
    }

    public void onClickCreate(View v)
    {
        String username = edUsername.getText().toString();
        String password = edPassword.getText().toString();
        String age = edAge.getText().toString();
        String surename = edSurename.getText().toString();
        String name = edName.getText().toString();

        InsertUser insertUser = new InsertUser(this);
        insertUser.execute("insert", username, password, age, surename, name);
    }

    class InsertUser extends AsyncTask<String,Void,String> {
        Context context;
        AlertDialog alertDialog;

        InsertUser(Context ctx) {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            String type = params[0];
            String login_url = "https://uarkacm.000webhostapp.com/register.php";
            if (type.equals("insert")) {
                try {
                    String user_name = params[1];
                    String password = params[2];
                    String age = params[3];
                    String surename = params[4];
                    String name = params[5];

                    Log.d("Test", user_name);
                    Log.d("Test", password);

                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&"
                            + URLEncoder.encode("surename", "UTF-8") + "=" + URLEncoder.encode(surename, "UTF-8") + "&"
                            + URLEncoder.encode("age", "UTF-8") + "=" + URLEncoder.encode(age, "UTF-8") + "&"
                            + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8") + "&"
                            + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
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
            alertDialog.setTitle("Login Status");
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

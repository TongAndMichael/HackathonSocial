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
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tongyu on 11/18/17.
 */

public class PostActivity extends AppCompatActivity {
    ListView listview;
    BufferedInputStream is = null;
    String line = null;
    String result = null;
    String data[];
    String username;

    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    Map<String, Object> map = new HashMap<String, Object>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();
        username = intent.getExtras().getString("username");

        listview = (ListView) findViewById(R.id.listview_post);

    }

    @Override
    public void onResume()
    {
        list.clear();
        QueryPost query = new QueryPost(this);
        query.execute();
        super.onResume();
        Log.d("Databse", "REsume");
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

    public void onClickPostTopic(View v)
    {
        Intent intent = new Intent(this, SpecificPostActivity.class);
        startActivity(intent);
    }

    class QueryPost extends AsyncTask<String,Void,String> {
        Context context;
        AlertDialog alertDialog;
        QueryPost (Context ctx) {
            context = ctx;
        }
        @Override
        protected String doInBackground(String... params) {
            try
            {
                URL url = new URL("https://uarkacm.000webhostapp.com/querypost.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("GET");
                is = new BufferedInputStream(con.getInputStream());
            }
            catch (Exception e)
            {

            }

            try
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(is) );
                StringBuilder sb = new StringBuilder();

                while((line =br.readLine()) != null)
                {
                    sb.append(line +"\n");
                }
                is.close();
                result=sb.toString();
            }
            catch (Exception e)
            {

            }

            try
            {
                JSONArray ja = new JSONArray(result);
                JSONObject jo  =null;

                data= new String[ja.length()];

                for(int i = ja.length()-1; i>-1;i--)
                {
                    jo = ja.getJSONObject(i);
                    map.put("post", jo.getString("postcontent"));
                    map.put("username", jo.getString("username"));
                    list.add(map);
                    map = new HashMap<String, Object>();
                    Log.d("DatabaseJson", jo.getString("postcontent"));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Loading Post");
        }

        @Override
        protected void onPostExecute(String result) {

            SimpleAdapter adapter = new SimpleAdapter(PostActivity.this,list,
                    R.layout.content_list_view_post,
                    new String [] {"post", "username" },
                    new int[]{R.id.tv_content_post,R.id.tv_post_username});

            listview.setAdapter(adapter);

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
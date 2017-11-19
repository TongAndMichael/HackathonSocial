package com.example.tongyu.mysqldemo;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tongyu on 11/18/17.
 */

public class LeaderBoard extends AppCompatActivity {

    BufferedInputStream is = null;
    String line = null;
    String result = null;
    String data[];
    ListView listView;
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    Map<String, Object> map = new HashMap<String, Object>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        listView = (ListView) findViewById(R.id.listview_leaderborad);
        QueryShake shake = new QueryShake(this);
        shake.execute();


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

    class QueryShake extends AsyncTask<String,Void,String> {
        Context context;
        AlertDialog alertDialog;
        QueryShake (Context ctx) {
            context = ctx;
        }
        @Override
        protected String doInBackground(String... params) {
            try
            {
                URL url = new URL("https://uarkacm.000webhostapp.com/queryshake.php");
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
                JSONObject jo  = null;

                data= new String[ja.length()];

                int rank = 1;

                List<String> usernames = new ArrayList<>();
                List<Integer> scores = new ArrayList<>();

                for(int i = ja.length()-1; i>-1;i--)
                {
                    jo = ja.getJSONObject(i);

                    map.put("username", jo.getString("username"));
                    map.put("score", jo.getString("score"));
                    map.put("rank", rank + "");
                    rank++;
                    list.add(map);
                    map = new HashMap<String, Object>();
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

            SimpleAdapter adapter = new SimpleAdapter(LeaderBoard.this,list,
                    R.layout.content_listview_leaderboard,
                    new String [] {"rank","username", "score" },
                    new int[]{R.id.tv_leaderboard_rank, R.id.tv_leaderborad_name,R.id.tv_leaderborad_score});

            listView.setAdapter(adapter);

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}

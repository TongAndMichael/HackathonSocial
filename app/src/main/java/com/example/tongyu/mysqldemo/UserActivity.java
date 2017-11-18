package com.example.tongyu.mysqldemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by tongyu on 11/18/17.
 */

public class UserActivity extends AppCompatActivity {

    TextView welcome;
    String username;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Intent intent = getIntent();
        username = intent.getExtras().getString("username");

        welcome = (TextView) findViewById(R.id.tv_welcome);
        welcome.setText("welcome :" + username);



    }

    public void onClickGame1(View v)
    {
        Intent intent = new Intent (this, ClickGame.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void onClickGame2(View v)
    {
        Intent intent = new Intent (this, ShakeGame.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void onClickPost(View v)
    {
        Intent intent = new Intent (this, PostActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}

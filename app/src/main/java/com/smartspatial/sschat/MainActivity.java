package com.smartspatial.sschat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.login.widget.LoginButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
    }
}

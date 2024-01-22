package com.example.uiapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class statusActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        ImageButton ibt_back = findViewById(R.id.ibt_back);
        ibt_back.setOnClickListener(this);
        Button btn_sync = findViewById(R.id.btn_sync);
        btn_sync.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.ibt_back)
            finish();
        // if(v.getId() == R.id.btn_sync)

    }
}
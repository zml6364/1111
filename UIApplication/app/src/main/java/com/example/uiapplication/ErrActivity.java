package com.example.uiapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ErrActivity extends AppCompatActivity {

    private TextView error;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_err);
        error = findViewById(R.id.Error);

        Bundle bundle = getIntent().getExtras();
        String str = bundle.getString("Error");//获取上一个页面传递来的错误信息
        if(str!=null){
            error.setText(str);
        }
    }
}
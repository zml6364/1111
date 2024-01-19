package com.example.uiapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.uiapplication.util.CommonUtils;

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
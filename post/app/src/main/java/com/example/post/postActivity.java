package com.example.post;



import static com.example.post.queryUtil.readJsonFile;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class postActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_test;
    private Button btn_post;
    public List<Userinfo> userinfoList;   // 用户数据集合
    private Handler mainHandler;   // 主线程
    static String response;
    static String str;




    //调用自定义方法读取json字符串



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        et_test = findViewById(R.id.et_test);
        btn_post = findViewById(R.id.btn_post);
        btn_post.setOnClickListener(this);

        //et_test.setText(response);

    }

    @Override
    public void onClick(View v) {
        new Thread(() -> {
            try {
                queryUtil.queryRequest("焊接材料是纯铝，焊接厚度是1，焊接方法是MIG，焊接参数",
                        "http://202.38.247.12:8001/api/chat");

                str = readJsonFile("/storage/emulated/0/Download/response.json");
                parseJson(str);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();


    }

    /*读取json字符串中的嵌套数据*/
    public void parseJson(String jsonstr) throws JSONException {

        userinfoList = new ArrayList<>();
        Userinfo item = null;

        //new Thread(() -> {

            try {
                JSONObject jsonObject = new JSONObject(jsonstr);
                JSONObject jsonObject2 = jsonObject.optJSONObject("response");
                response = jsonObject2.optString("response");

                JSONObject jsonObject3 = jsonObject2.optJSONObject("data");
                JSONArray jsonArray = jsonObject3.optJSONArray("WireDiameter:0.8");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject0 = jsonArray.optJSONObject(i);
                    //通过迭代器获取这段json当中所有的key值
                    Iterator<String> keys = jsonObject0.keys();
                    //然后通过一个循环取出所有的key值
                    while (keys.hasNext()) {
                        item = new Userinfo();
                        String key = String.valueOf(keys.next());
                        item.setParamName(key);
                        //最后就可以通过刚刚得到的key值去解析后面的json了
                        String value = jsonObject0.optString(key);
                        item.setParamValue(value);
                        userinfoList.add(item);
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        //}).start();
    }
}
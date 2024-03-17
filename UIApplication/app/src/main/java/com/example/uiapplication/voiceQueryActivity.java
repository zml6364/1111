package com.example.uiapplication;

import static com.example.uiapplication.util.queryUtil.queryRequest;
import static com.example.uiapplication.util.queryUtil.readJsonFile;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.uiapplication.record.IdealRecorder;
import com.example.uiapplication.record.Log;
import com.example.uiapplication.record.StatusListener;
import com.example.uiapplication.util.CommonUtils;
import com.example.uiapplication.util.FileUtil;
import com.example.uiapplication.util.HttpRequest;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class voiceQueryActivity extends AppCompatActivity implements View.OnClickListener {

    private static String machine_id;
    private ConstraintLayout main_layout;
    private EditText tips_info;
    private String et_text=null;
    private boolean parse_successful=false;
    private boolean network_status=false;
    private ImageView btn_vback;
    private ImageView media_play;
    private ImageView clear_text;
    private Handler mainHandler;   // 主线程
    private String response;
    private String response_str;
    private EditText et_vquery;
    private ImageButton record_bnt;
    private String id=null;
    private String filename; //保存文件时的文件名
    final private int AUDIO_CODE=101;
    final private int VIBRATE_CODE=102;
    final static int REQUEST_CODE_READ=104;
    final static int REQUEST_CODE_WRITE=105;

    private Button btn_vquery_multiply;
    private ImageView btn_reset;
    private String query_file_path;
    private String response_file_path;
    private String id_file_path;

    private IdealRecorder idealRecorder;
    private IdealRecorder.RecordConfig recordConfig;

    /*音频播放测试*/
    private MediaPlayer mediaPlayer;
    private ProgressBar progressBar;
    private ProgressBar progressBar_voice;
    private int progressStatus = 0;
    private String err_info=null;
    private Thread networkThread;
    private boolean progressBar_pause=false;


    private final StatusListener statusListener = new StatusListener() {
        @Override
        public void onStartRecording() {
            //CommonUtils.showShortMsg(voiceQueryActivity.this,"start record!");
        }

        @Override
        public void onRecordData(short[] data, int length) {

        }

        @Override
        public void onVoiceVolume(int volume) {
            double myVolume = (volume - 40) * 4;
            Log.d("MainActivity", "current volume is " + volume);
        }

        @Override
        public void onRecordError(int code, String errorMsg) {
            CommonUtils.showShortMsg(voiceQueryActivity.this,"record! Error"+errorMsg);
        }

        @Override
        public void onFileSaveFailed(String error) {
            CommonUtils.showShortMsg(voiceQueryActivity.this,"file save fail! error is:"+error);
        }

        @Override
        public void onFileSaveSuccess(String fileUri) {
            //CommonUtils.showShortMsg(voiceQueryActivity.this,"file save successfully! path is:"+fileUri);
        }

        @Override
        public void onStopRecording() {
            //CommonUtils.showShortMsg(voiceQueryActivity.this," record finish!");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_query);
        initView();
    }

    @Override
    protected void onDestroy() {

        /*页面关闭前停止播放*/
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            // 检查设备是否正在播放音频
            if (audioManager.isMusicActive()) {
                // 请求音频焦点
                int result = audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // 如果获得了音频焦点，可以停止音频播放
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    // 释放音频焦点
                    audioManager.abandonAudioFocus(null);
                }
            }
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        /*页面关闭前停止播放*/
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            // 检查设备是否正在播放音频
            if (audioManager.isMusicActive()) {
                // 请求音频焦点
                int result = audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // 如果获得了音频焦点，可以停止音频播放
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    // 释放音频焦点
                    audioManager.abandonAudioFocus(null);
                }
            }
        }
    }

    private void initView() {
        /*textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    switch (textToSpeech.setLanguage(Locale.US)) {
                    } // 设置语音合成的语言
                }
            }
        });
        String text = "Hello, this is a test.";
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);*/
        /*隐藏系统顶部UI*/
        Hide_SystemUI();
        /*设置状态栏颜色*/
        int[] colors = {Color.parseColor("#ffffff"), Color.parseColor("#C5C5C5")};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        getWindow().getDecorView().setBackground(gradientDrawable);
        /*机器型号*/
        machine_id = Build.MODEL;
        /*主线程句柄*/
        mainHandler = new Handler(getMainLooper());

        /*清空旧文件*/
        try {
            FileWriter fw=new FileWriter(getSaveFilePath());
            fw.write("");//清空
            fw.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
        /*文件路径配置*/
        query_file_path = query_file_init(" ");//初始查询语句为空
        response_file_path = response_file_init();//响应文件路径初始化
        id_file_path = id_file_init();//id存储文件路径初始化

        /*主布局，设置监听*/
        main_layout = findViewById(R.id.voice_query_activity);
        main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*收起键盘*/
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                /*隐藏系统UI*/
                Hide_SystemUI();
            }
        });

        /*文本输入框及其焦点变更监听*/
        et_vquery = findViewById(R.id.et_vquery);
        et_vquery.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏键盘
                }
            }
        });

        /*提示文本框*/
        tips_info = findViewById(R.id.editext_tips_info);
        tips_info.setVisibility(View.VISIBLE);//一开始隐藏提示框
        tips_info.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0); // 隐藏键盘
                }
            }
        });

        /*设置四个按键的监听*/
        btn_vquery_multiply = findViewById(R.id.btn_vquery_multiply);
        btn_vquery_multiply.setOnClickListener(this);
        btn_vback = findViewById(R.id.btn_vback);
        btn_vback.setOnClickListener(this);
        btn_reset = findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(this);
        /*新播放按钮*/
        media_play = findViewById(R.id.media_play);
        media_play.setOnClickListener(this);
        /*文本擦除*/
        clear_text = findViewById(R.id.clear_text);
        clear_text.setOnClickListener(this);

        /*进度条*/
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE); // 隐藏进度条

        progressBar_voice = findViewById(R.id.progressBar_voice);
        progressBar_voice.setVisibility(View.GONE);// 隐藏进度条

        /*初始化录音对象，获取一个实例*/
        idealRecorder = IdealRecorder.getInstance();
        /*录音预配置*/
        recordConfig = new IdealRecorder.RecordConfig(MediaRecorder.AudioSource.MIC, 16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        /*录音按键配置*/
        record_bnt = findViewById(R.id.record_bnt);


        /*-------------------------------------------------------------------------------------click监听-------------------------------------------------------------------------------------*/
        record_bnt.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                et_vquery.setText("");

                /*振动反馈*/
                if(ContextCompat.checkSelfPermission(voiceQueryActivity.this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED){
                    // 如果权限未被授予，向用户请求权限
                    ActivityCompat.requestPermissions(voiceQueryActivity.this, new String[]{Manifest.permission.VIBRATE}, VIBRATE_CODE);
                }else {
                    // 获取系统的振动服务
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);// 振动 100 毫秒
                }

                /*录音权限检测*/
                if (ContextCompat.checkSelfPermission(voiceQueryActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    // 如果权限未被授予，向用户请求权限
                    ActivityCompat.requestPermissions(voiceQueryActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_CODE);//无权限申请权限
                }
                else {
                    //有权限，开始录音
                    progressStatus=0;
                    progressBar_pause=false;

                    /*动态进度条线程*/
                    new Thread(new Runnable() {
                        public void run()
                        {
                            while (progressStatus<100 && !progressBar_pause)
                            {
                                progressStatus += 1;
                                // 通过Handler更新进度条
                                mainHandler.post(new Runnable() {
                                    public void run() {
                                        progressBar_voice.setProgress(progressStatus);
                                    }
                                });
                                /*录音超时，30s*/
                                if(progressStatus==100)
                                {
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar_voice.setVisibility(View.GONE);
                                            //idealRecorder.stop();
                                            progressBar.setVisibility(View.GONE);
                                            record_bnt.setImageResource(R.drawable.microphone);/*切换按键形态*/
                                            CommonUtils.showShortMsg(voiceQueryActivity.this,"录音超时！");
                                        }
                                    });
                                }

                                try {
                                    // 线程休眠一段时间
                                    Thread.sleep(600);//决定录音时长
                                } catch (InterruptedException e) {
                                    err_info+=e.toString();
                                }
                            }

                        }
                    }).start();

                    /*录音开启*/
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            /*切换按键形态*/
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar_voice.setVisibility(View.VISIBLE);
                                    record_bnt.setImageResource(R.drawable.record_press);//切换录音按钮形态
                                }
                            });
                            /*录音准备*/
                            idealRecorder.setRecordFilePath(getSaveFilePath());
                            //如果需要保存录音文件  设置好保存路径就会自动保存  也可以通过onRecordData 回调自己保存  不设置 不会保存录音
                            idealRecorder.setRecordConfig(recordConfig).setMaxRecordTime(20000).setVolumeInterval(200);
                            //设置录音配置 最长录音时长 以及音量回调的时间间隔
                            idealRecorder.setStatusListener(statusListener);
                            /*开始录音*/
                            idealRecorder.start();
                        }
                    }).start();

                }
                return false;
            }
        });

        /*-------------------------------------------------------------------------------------Touch监听-------------------------------------------------------------------------------------*/
        record_bnt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();
                if(MotionEvent.ACTION_UP== event.getAction())
                {

                    /*第一次，要录音权限检测*/
                    if (ContextCompat.checkSelfPermission(voiceQueryActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        /*抬手去按确定授权*/
                        return false;
                    }

                    /*振动反馈*/
                    if(ContextCompat.checkSelfPermission(voiceQueryActivity.this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED){
                        // 如果权限未被授予，向用户请求权限
                        ActivityCompat.requestPermissions(voiceQueryActivity.this, new String[]{Manifest.permission.VIBRATE}, VIBRATE_CODE);
                    }else {
                        // 获取系统的振动服务
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(100);// 振动 100 毫秒
                    }

                    /*界面处理*/
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar_pause=true;//超时标志
                            progressBar_voice.setVisibility(View.GONE);/*隐藏进度条*/
                            record_bnt.setImageResource(R.drawable.microphone);/*切换按键形态*/
                        }
                    });

                    /*停止录音*/
                    idealRecorder.stop();


                    /******************************************************修改语音查询接口******************************************************************************/
                    /*开启语音查询线程*/
                    new Thread(new Runnable() {
                        @Override
                        public void run()
                        {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.VISIBLE); // 显示进度条
                                }
                            });

                            WebAPI webAPI = new WebAPI();
                            webAPI.setFilePath(getSaveFilePath());

                            //超时处理
                            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                                try {
                                    return webAPI.connectWebSocket();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            });

                            try {
                                // 等待5秒，超时则设置network_status为false
                                et_text = future.get(5, TimeUnit.SECONDS);
                                network_status = true;
                            } catch (TimeoutException e) {
                                network_status = false;
                            } catch (Exception e) {
                                throw new RuntimeException(e); //抛出 RuntimeException 并传递给调用此代码的上层代码
                            }

//                            try {
//                                et_text = webAPI.connectWebSocket();
//                                network_status=true;
//                            } catch (Exception e) {
//                                network_status=false;
//                                //throw new RuntimeException(e); //抛出 RuntimeException 并传递给调用此代码的上层代码
//                            }

                            if(network_status){
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE); // 隐藏进度条
                                        et_vquery.setText(et_text);//测试语音转文字准确性
                                    }
                                });
                            } else {
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        CommonUtils.showShortMsg(voiceQueryActivity.this,"网络故障");
                                        progressBar.setVisibility(View.GONE); // 隐藏进度条
                                        //et_vquery.setText("解析失败");
                                        //tips_info.setTextColor(Color.RED);  //bug: 网络故障设置字体红色后, 正常提示文字都是红色,
                                                                            // 查询id失败也设置红色了, 现在测试不了, 要不直接注释掉
                                        tips_info.setText("请检查网络！");
                                    }
                                });
                            }

//                            File post_output = new File(response_file_path);//输出流，解析得到的查询命令
//                            File record = new File(getSaveFilePath());
//                            try {
//                                HttpRequest request = HttpRequest.post("http://61.142.130.81:8001/api/speech");
//                                request.connectTimeout(10000);
//                                request.readTimeout(10000);
//                                request.part("file","file",record);
//                                request.receive(post_output);
//
//                                network_status=true;
//                            }catch (Exception e){
//                                network_status=false;
//                            }
//
//                            if(network_status)
//                            {
//                                /*解析响应文件*/
//                                response_str=null;
//                                response_str = readJsonFile(response_file_path);//读取post得到的json文件转为字符串
//                                try {
//                                    JSONObject jsonObject=new JSONObject(response_str);//创建响应文件json对象
//                                    Iterator<String> response_keys = jsonObject.keys();//取response节点的键值对
//                                    String keyname=String.valueOf(response_keys.next());//取键名
//                                    et_text=jsonObject.optString(keyname);//取值
//                                    parse_successful=true;
//                                }catch (Exception e){
//                                    parse_successful=false;
//                                    et_text=e.toString()+"语音解析失败";
//                                }
//
//                                if(parse_successful)
//                                {
//                                    mainHandler.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            progressBar.setVisibility(View.GONE); // 隐藏进度条
//                                            et_vquery.setText(et_text);//测试语音转文字准确性
//                                            query_post();//开始语音查询
//                                        }
//                                    });
//                                }
//                                /*网络故障*/
//                                else
//                                {
//                                    mainHandler.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            progressBar.setVisibility(View.GONE); // 隐藏进度条
//                                            et_vquery.setText("解析失败");
//                                        }
//                                    });
//                                }
//                            }
//                            else
//                            {
//                                mainHandler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        CommonUtils.showShortMsg(voiceQueryActivity.this,"网络故障");
//                                        progressBar.setVisibility(View.GONE); // 隐藏进度条
//                                        tips_info.setTextColor(Color.RED);
//                                        tips_info.setText("请检查网络！");
//                                    }
//                                });
//                            }

                        }
                    }).start();

                }
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {

        /********************************************************轮询按钮********************************************************/
       if (v.getId()==R.id.btn_vquery_multiply)
       {
           query_post();
       }
       /*******************************************************返回按钮*******************************************************/
        else if (v.getId() == R.id.btn_vback)
        {
            id=null;
            finish();//关闭页面
        }
        /*******************************************************清空按钮*******************************************************/
        else if (v.getId()==R.id.btn_reset)
        {
            if(id!=null)
            {
                id=null;//结束多轮对话
                if (response_str!=null) response_str=null;
            }

            et_vquery.setText("");//清楚文本框
            tips_info.setText("");
            try {
               FileWriter fw=new FileWriter(getSaveFilePath());
               fw.write("");//清空
               fw.flush();
            }catch (Exception e){
               e.printStackTrace();
            }
        }
       /*******************************************************擦除文本按键*******************************************************/
        else if (R.id.clear_text==v.getId())
        {
            et_vquery.setText("");
        }
       /*******************************************************录音播放按钮*******************************************************/
        else if (R.id.media_play==v.getId())
        {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                // 检查设备是否正在播放音频
                if (audioManager.isMusicActive()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    // 设备正在播放音频
                    CommonUtils.showShortMsg(voiceQueryActivity.this,"音频中断");
                    //中断播放
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            media_play.setImageResource(R.drawable.media_play);
                        }
                    });
                    return;
                    // 在这里执行相应的操作
                }
                else
                {// 设备没有在播放音频

                    String err_info="";

                    /*播放录音*/
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            // 音频播放完成后图标切换
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    media_play.setImageResource(R.drawable.media_play);
                                }
                            });
                        }
                    });

                    try {mediaPlayer.setDataSource(voiceQueryActivity.this, Uri.parse(getSaveFilePath()));}
                    catch (IOException e) {err_info+=e.toString();}

                    File file = new File(getSaveFilePath());
                    if (file.exists() && file.length() == 0)
                    {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.showShortMsg(voiceQueryActivity.this,"尚未录音");
                            }
                        });
                    }
                    else
                    {/*文件非空，可以播放音频*/

                        /*更换播放图标*/
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                media_play.setImageResource(R.drawable.media_stop);
                            }
                        });
                        try {
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        }
                        catch (Exception e)
                        {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    CommonUtils.showShortMsg(voiceQueryActivity.this,"播放失败");
                                }
                            });
                        }
                    }

                }
            }
        }

    }

    private String getFilename(String response) {
        // 使用正则表达式提取关键信息
        String pattern = "焊接方法是(.*?)，.*?焊接材料是(.*?)，.*?焊接厚度是(.*?)。";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(response);
        String material = "";
        String thickness = "";
        String method = "";
        if (matcher.find()) {
            method = matcher.group(1);
            material = matcher.group(2);
            thickness = matcher.group(3);
        }
        return material + "_" + thickness + "_" + method + ".db";
    }

    private void alter_info(String str){
        /*子线程不能执行UI操作，要交给主线程 将子线程(工作线程)中需要更新UI的相关信息传递到主线程，从而实现工作线程对UI的更新*/
        new Thread(String.valueOf(mainHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    /*弹出对话框*/
                    //构建对话框构建器
                    AlertDialog.Builder builder = new AlertDialog.Builder(voiceQueryActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage(str);//json文件的response部分
                    builder.setNegativeButton("取消", null);
                    /*设置对话框按键的监听和文本*/
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            /*页面转跳*/
                            Intent intent = new Intent(voiceQueryActivity.this, query1Activity.class);
                            Bundle bundle = new Bundle(); // 创建一个新包裹
                            bundle.putString("response_json", response_str);//将解析的string传递给下一个页面
                            bundle.putString("response_filepath",response_file_path);
                            bundle.putString("response_filename",filename);
                            intent.putExtras(bundle); // 把快递包裹塞给意图
                            startActivity(intent); // 跳转到意图指定的活动页面
                        }

                    });
                    // 根据建造器构建提醒对话框对象
                    AlertDialog alert = builder.create();
                    alert.show();
                }catch (Exception e){
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.showShortMsg(voiceQueryActivity.this,e.toString());
                        }
                    });
                }
            }
        }))).start();

    }

    /*显示回应对话框*/
    private void dialog_show_response(String str){
        /*子线程不能执行UI操作，要交给主线程 将子线程(工作线程)中需要更新UI的相关信息传递到主线程，从而实现工作线程对UI的更新*/
        new Thread(String.valueOf(mainHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    /*显示提示信息*/
                    // 创建Alpha动画，从0到1的渐变效果，持续时间为1000毫秒
                    Animation fadeIn = new AlphaAnimation(0, 1);
                    fadeIn.setDuration(3000);
                    /*特定部分字体高亮*/
                    SpannableString spannableString = new SpannableString(response);
                    Pattern pattern = Pattern.compile("'(.*?)'");
                    Matcher matcher = pattern.matcher(response);
                    //int mycolor = Color.argb(255, 255, 128, 0);//自定义橙色
                    while (matcher.find()) {
                        int startIndex = matcher.start();
                        int endIndex = matcher.end();
                        spannableString.setSpan(new ForegroundColorSpan(Color.RED), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    tips_info.setText(spannableString);
                    tips_info.setVisibility(View.VISIBLE);
                    tips_info.startAnimation(fadeIn);//渐变动画

                }catch (Exception e){
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtils.showShortMsg(voiceQueryActivity.this,e.toString());
                        }
                    });
                }
            }
        }))).start();

    }

    /*读写权限检查，暂时弃用*/
    private int checkPermission() {

        /*读写权限检查*/
        if(ContextCompat.checkSelfPermission(voiceQueryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // 如果权限未被授予，向用户请求权限
            ActivityCompat.requestPermissions(voiceQueryActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE);
        }
        if(ContextCompat.checkSelfPermission(voiceQueryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // 如果权限未被授予，向用户请求权限
            ActivityCompat.requestPermissions(voiceQueryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ);
        }

        return 0;
    }

    /*权限检测回调*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_CODE_WRITE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，可以进行存储读写操作
                CommonUtils.showDlgMsg(voiceQueryActivity.this, "写申请成功！");
            } else {
                // 权限被拒绝，需要向用户解释为什么需要这个权限，并提示用户手动授予权限
                CommonUtils.showDlgMsg(voiceQueryActivity.this, "写申请失败！");
            }
        }
        if (requestCode==REQUEST_CODE_READ)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，可以进行存储读写操作
                //CommonUtils.showDlgMsg(voiceQueryActivity.this, "读申请成功！");
            } else {
                // 权限被拒绝，需要向用户解释为什么需要这个权限，并提示用户手动授予权限
                CommonUtils.showDlgMsg(voiceQueryActivity.this, "读申请失败！");
            }
        }
        if (requestCode==AUDIO_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，可以进行存储读写操作
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        //CommonUtils.showDlgMsg(voiceQueryActivity.this, "录音申请成功！");
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } else {
                // 权限被拒绝，需要向用户解释为什么需要这个权限，并提示用户手动授予权限
                CommonUtils.showDlgMsg(voiceQueryActivity.this, "录音申请失败！");
            }
        }
        if(requestCode==VIBRATE_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，可以进行存储读写操作
                //CommonUtils.showDlgMsg(voiceQueryActivity.this, "振动授权成功！");

            }else {
                CommonUtils.showDlgMsg(voiceQueryActivity.this, "没有振动权限");
            }
        }

    }

    /*返回查询文件的路径*/
    private String query_file_init(String query) {
        String filename="query.json";
        String filepath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"/"+filename;

        File queryFile=new File(filepath);//创建查询js文件
        if(!queryFile.exists())
        {
            try {
                queryFile.createNewFile();
                JSONObject json = new JSONObject();
                json.put("query", query );
                FileUtil.saveText(filepath,json.toString());
            }
            catch (Exception e){
                Error_handle(e.toString());
            }

        }//若文件已存在则清空
        else {
            FileWriter fw=null;
            /*存在就清空文件*/
            try {
                fw=new FileWriter(queryFile);
                fw.write("");//文件存在则清空文件
                fw.flush();
            }catch (Exception e){
                Error_handle(e.toString());
            }
            /*写入新的查询指令*/
            try {
                JSONObject json = new JSONObject();
                json.put("query", query );
                fw.write(json.toString());
            }catch (Exception e) {
                Error_handle(e.toString());
            }finally {
                if(fw!=null) {
                    try {
                        fw.close();//关闭输入流
                    } catch (IOException e) {
                        Error_handle(e.toString());
                    }
                }
            }

        }
        return filepath;
    }

    /*响应文件初始化*/
    private String response_file_init(){
        String filename="response.json";
        String filepath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"/"+filename;

        File queryFile=new File(filepath);//创建查询js文件
        if(!queryFile.exists())
        {
            try {
                queryFile.createNewFile();
                JSONObject json = new JSONObject();
                json.put("response", "" );
                FileUtil.saveText(filepath,json.toString());
            }
            catch (Exception e){
                Error_handle(e.toString());
            }

        }//若文件已存在则清空
        else {
            FileWriter fw=null;
            /*存在就清空文件*/
            try {
                fw=new FileWriter(queryFile);
                fw.write("");//文件存在则清空文件
                fw.flush();
            }catch (Exception e){
                Error_handle(e.toString());
            }
        }
        return filepath;
    }
    /*id文件初始化*/
    private String id_file_init(){

        String filename="session_id.json";
        String filepath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"/"+filename;

        File queryFile=new File(filepath);//创建查询js文件
        if(!queryFile.exists())
        {
            try {
                queryFile.createNewFile();
            }
            catch (Exception e){
                Error_handle(e.toString());
            }

        }//若文件已存在则清空
        else {
            try {
                FileWriter fw=new FileWriter(queryFile);
                fw.write("");//文件存在则清空文件
                fw.flush();
            }catch (Exception e){
                Error_handle(e.toString());;
            }
        }
        return filepath;
    }

    /*
     * 参数：无
     * 返回值：录音文件路径
     * */
    private String getSaveFilePath() {

        File file = new File( getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Audio");
        if (!file.exists()) {
            file.mkdirs();
        }
        File wavFile = new File(file, machine_id+"ideal.wav");
        return wavFile.getAbsolutePath();

    }

    private void Error_handle(String errorInfo){

        Intent intent = new Intent(voiceQueryActivity.this, ErrActivity.class);
        Bundle bundle = new Bundle(); // 创建一个新包裹
        bundle.putString("Error", errorInfo);//将解析的string传递给下一个页面
        intent.putExtras(bundle); // 把快递包裹塞给意图
        startActivity(intent); // 跳转到意图指定的活动页面
    }

    /*post查询处理*/
    private void query_post(){

        final String url = "http://61.142.130.81:8001/api/session/";
        err_info = null;

        networkThread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    /*******************************************************id为空（初次查询/新一轮查询），先获取id*******************************************************/
                    if(id==null)
                    {
                        /*--------------------------------------------------------准备工作，查询id的查询文件--------------------------------------------------------*/
                        try {
                            FileWriter fw=new FileWriter(id_file_path);
                            fw.write("");
                            fw.flush();
                            fw.close();
                        }catch (Exception e)
                        {
                            /*IO流错误*/
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE); // 隐藏进度条
                                    CommonUtils.showShortMsg(voiceQueryActivity.this,"IO 错误 文件读写失败！");
                                }
                            });
                        }

                        /*--------------------------发起空的post请求，获取id--------------------------*/
                        File post_output=null;
                        post_output = new File(id_file_path);//输出流，解析得到的查询命令
                        /*获取请求*/
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.VISIBLE); //切到主线程显示进度条
                            }
                        });

                        try {
                            HttpRequest request=new HttpRequest("http://61.142.130.81:8001/api/session/start","POST");
                            request.connectTimeout(10000);
                            request.readTimeout(10000);
                            request.acceptJson();//设置header
                            request.contentType("application/json");//设置contentType
                            request.receive(post_output);//上传查询请求，获取响应并拷贝到本地文件
                        }catch (Exception e)
                        {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    et_vquery.setText("");
                                    tips_info.setTextColor(Color.RED);
                                    tips_info.setText("获取id失败");
                                    CommonUtils.showShortMsg(voiceQueryActivity.this,"网络错误！");
                                }
                            });
                            return;
                        }


                        /*--------------------------解析id--------------------------*/
                        String id_file = readJsonFile(id_file_path);//读取id的json文件
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(id_file);
                            id =jsonObject.getString("session_id");
                        } catch (JSONException e) {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    CommonUtils.showShortMsg(voiceQueryActivity.this,"空响应");
                                }
                            });
                        }


                        /*---------------------------------------------获取id成功，并构建新的url------------------------------------------------*/
                        if(id!=null)
                        {
                            String query = et_vquery.getText().toString().trim();//获取查询文本
                            StringBuilder sb=new StringBuilder();
                            sb.append(url).append(id).append("/?query=").append(query);/*构建新的url*/

                            try
                            {/*修改查询文件*/
                                JSONObject query_js=new JSONObject();
                                query_js.put("query",et_vquery.getText().toString().trim());
                                FileWriter fw=new FileWriter(query_file_path);
                                fw.write("");//清空
                                fw.flush();
                                fw.write(query_js.toString());//修改
                                fw.flush();
                                fw.close();
                            }catch (Exception e)
                            {
                                /*IO错误*/
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE); // 隐藏进度条
                                        CommonUtils.showShortMsg(voiceQueryActivity.this,"IO 错误 文件读写失败！");
                                    }
                                });
                            }

                            /*--------------------------------------------------------------用新的url发起新的查询------------------------------------------------------------*/
                            try {
                                queryRequest(query_file_path,response_file_path,sb.toString());//post查询
                                /*解析查询结果*/
                                response_str=null;//清空字符串
                                response_str = readJsonFile(response_file_path);//读取post得到的json文件转为字符串
                            }catch (Exception e)
                            {
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        CommonUtils.showShortMsg(voiceQueryActivity.this,"网络错误！");
                                    }
                                });
                            }


                            /*-------------------------------------------查询结果为空-------------------------------------------*/
                            if(response_str==null)
                            {
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE); // 隐藏进度条
                                        et_vquery.setText("查询失败!");
                                    }
                                });
                            }
                            /*-------------------------------------------查询结果非空-------------------------------------------*/
                            else
                            {
                                JSONObject jsonObject2=new JSONObject(response_str);//创建响应文件json对象
                                /*--------------------------查询方式出错(如：材料方法不匹配)，此时没有data，只有response且response下无data--------------------------*/
                                if(jsonObject2.get("response") instanceof String)
                                {
                                    response=jsonObject2.getString("response");
                                    /*关闭进度条*/
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE); // 隐藏进度条
                                            dialog_show_response(response);
                                        }
                                    });
                                }
                                /*--------------------------查到了完整数据,有data且非空--------------------------*/
                                else if(jsonObject2.optJSONObject("response").has("data") && jsonObject2.optJSONObject("response").optJSONObject("data").length()!=0)
                                {

                                    response=jsonObject2.optJSONObject("response").getString("response");//取值
                                    /*创建文件名称，用于数据保存*/
                                    filename = getFilename(response);
                                    /*关闭进度条*/
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE); // 隐藏进度条
                                        }
                                    });

                                    alter_info(response);//弹出对话框，支持页面转跳
                                }
                                /*--------------------------有data，但data为空--------------------------*/
                                else if(jsonObject2.optJSONObject("response").has("data") && jsonObject2.optJSONObject("response").optJSONObject("data").length()==0)
                                {
                                    /*关闭进度条*/
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE); // 隐藏进度条
                                            CommonUtils.showShortMsg(voiceQueryActivity.this,"数据库尚更新此数据！");
                                            id=null;
                                        }
                                    });

                                }
                            }

                        }
                        /*----------------------------------------------------id获取失败----------------------------------------------------*/
                        else
                        {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE); // 隐藏进度条
                                    tips_info.setText("id获取失败");
                                }
                            });
                        }

                    }

                    /********************************************************id非空，继续轮询（多轮模式的第二轮往后）********************************************************/
                    else if(id!=null)
                    {
                        String query = et_vquery.getText().toString().trim();//获取查询文本
                        StringBuilder sb=new StringBuilder();
                        sb.append(url).append(id).append("/?query=").append(query);/*构建新的url*/

                        try
                        {
                            /*修改查询文件*/
                            JSONObject query_js=new JSONObject();
                            query_js.put("query",et_vquery.getText().toString().trim());
                            FileWriter fw=new FileWriter(query_file_path);
                            fw.write("");//清空
                            fw.flush();
                            fw.write(query_js.toString());//修改
                            fw.flush();
                            fw.close();
                        }catch (Exception e)
                        {
                            /*IO错误*/
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE); // 隐藏进度条
                                    CommonUtils.showShortMsg(voiceQueryActivity.this,"IO 错误 文件读写失败！");
                                }
                            });
                        }

                        /*--------------------------发起新的查询--------------------------*/
                        try {
                            queryRequest(query_file_path,response_file_path,sb.toString());//post查询
                            /*解析查询结果*/
                            response_str=null;//清空字符串
                            response_str = readJsonFile(response_file_path);//读取post得到的json文件转为字符串
                        }catch (Exception e)
                        {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    CommonUtils.showShortMsg(voiceQueryActivity.this,"网络错误！");
                                }
                            });
                        }


                        /*--------------------------查询结果为空--------------------------*/
                        if(response_str==null)
                        {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE); // 隐藏进度条
                                    et_vquery.setText("查询失败!");
                                }
                            });
                        }
                        /*--------------------------查询结果非空--------------------------*/
                        else
                        {
                            JSONObject jsonObject2=new JSONObject(response_str);//创建响应文件json对象

                            /*--------------------------查询方式出错(如：材料方法不匹配)，此时没有data，只有response且response下无data--------------------------*/
                            if(jsonObject2.get("response") instanceof String)
                            {
                                response=jsonObject2.getString("response");
                                /*关闭进度条*/
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE); // 隐藏进度条
                                        dialog_show_response(response);
                                    }
                                });
                            }
                            /*--------------------------查到了完整数据,有data且非空--------------------------*/
                            else if(jsonObject2.optJSONObject("response").has("data") && jsonObject2.optJSONObject("response").optJSONObject("data").length()!=0)
                            {

                                response=jsonObject2.optJSONObject("response").getString("response");//取值
                                /*创建文件名称，用于数据保存*/
                                filename = getFilename(response);
                                /*关闭进度条*/
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE); // 隐藏进度条
                                    }
                                });
                                alter_info(response);//弹出对话框，支持页面转跳
                            }
                            /*--------------------------有data，但data为空--------------------------*/
                            else if(jsonObject2.optJSONObject("response").has("data") && jsonObject2.optJSONObject("response").optJSONObject("data").length()==0)
                            {
                                /*关闭进度条*/
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE); // 隐藏进度条
                                        CommonUtils.showShortMsg(voiceQueryActivity.this,"数据库尚更新此数据！");
                                        id=null;
                                    }
                                });

                            }
                        }
                    }


                    /*--------------------------------------------------------------------网络线程完成--------------------------------------------------------------------*/

                }catch (Exception e)
                {
                    /*其他异常处理*/
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE); // 隐藏进度条
                            CommonUtils.showLonMsg(voiceQueryActivity.this,e.toString());
                        }
                    });
                }

            }
        });

        networkThread.start();//开启网络线程
    }

    private void Hide_SystemUI(){
        View view = getWindow().getDecorView();
        WindowInsetsController insetsController = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            insetsController = view.getWindowInsetsController();
        }
        if (insetsController != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        }

    }

}
package com.example.uiapplication;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebAPI {

    private static final String hostUrl = "https://iat-api.xfyun.cn/v2/iat"; //中英文，http url 不支持解析 ws/wss schema
    private static final String appid = "ff9d26aa"; //在控制台-我的应用获取
    private static final String apiSecret = "YTlhZDY0M2ZhYzk4ZjcwMGMyMDhhYThj"; //在控制台-我的应用-语音听写（流式版）获取
    private static final String apiKey = "d78a8f15d26324aacc0614a4e16a8b50"; //在控制台-我的应用-语音听写（流式版）获取
    public static final int StatusFirstFrame = 0;
    public static final int StatusContinueFrame = 1;
    public static final int StatusLastFrame = 2;
    public static final Gson json = new Gson();
    static Decoder decoder = new Decoder();
    private static String file = null;//wav格式
    private String result;

    public String connectWebSocket() throws Exception {
        // 构建鉴权url
        String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
        OkHttpClient client = new OkHttpClient.Builder().build();
        //将url中的 schema http://和https://分别替换为ws:// 和 wss://
        String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();

        //定义 CountDownLatch 实例, 等待 WebSocket 连接完成并获取 result
        //会阻塞当前线程，可考虑使用其他异步机制，如 CompletableFuture、RxJava 等
        CountDownLatch latch = new CountDownLatch(1);

        //构建 WebSocketListener, 监听和处理 WebSocket 事件
        WebSocketListener webSocketListener = new WebSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                //System.out.println("WebSocket connection established successfully!");
                //连接成功，开始发送数据
                new Thread(()->{
                    int frameSize = 6400; //每一帧音频的大小,建议每 40ms 发送 1280B; 为节省时间, 改为 5*1280B, 发送速度 156.25kb/s
                    int intervel = 40;
                    int status = 0;  // 音频的状态
                    try (FileInputStream fs = new FileInputStream(file)) {
                        byte[] buffer = new byte[frameSize];
                        end:
                        while (true) {
                            int len = fs.read(buffer);
                            if (len == -1) {
                                status = StatusLastFrame;  //文件读完，改变status 为 2
                            }
                            switch (status) {
                                case StatusFirstFrame:   // 第一帧音频status = 0
                                    JsonObject frame = new JsonObject();
                                    JsonObject business = new JsonObject();  //第一帧必须发送
                                    JsonObject common = new JsonObject();  //第一帧必须发送
                                    JsonObject data = new JsonObject();  //每一帧都要发送
                                    // 填充common
                                    common.addProperty("app_id", appid);
                                    //填充business
                                    business.addProperty("language", "zh_cn");//中文（支持简单的英文识别）
                                    business.addProperty("domain", "iat");//日常用语
                                    business.addProperty("accent", "mandarin");//中文方言请在控制台添加试用，添加后即展示相应参数值
                                    business.addProperty("vad_eos", 5000);//静默 5s 后认为音频结束, 避免录音时用户停顿时间较长, 讯飞不继续识别, 最大 10s
                                    //business.addProperty("dwa", "wpgs");//动态修正(若未授权不生效，在控制台可免费开通)
                                    //填充data
                                    data.addProperty("status", StatusFirstFrame);
                                    data.addProperty("format", "audio/L16;rate=16000");//16k音频
                                    data.addProperty("encoding", "raw");//原生音频
                                    data.addProperty("audio", Base64.getEncoder().encodeToString(Arrays.copyOf(buffer, len)));//音频内容，采用base64编码
                                    //填充frame
                                    frame.add("common", common);
                                    frame.add("business", business);
                                    frame.add("data", data);
                                    webSocket.send(frame.toString());
                                    status = StatusContinueFrame;  // 发送完第一帧改变status = 1
                                    break;
                                case StatusContinueFrame:  //中间帧status = 1
                                    JsonObject frame1 = new JsonObject();
                                    JsonObject data1 = new JsonObject();
                                    data1.addProperty("status", StatusContinueFrame);
                                    data1.addProperty("format", "audio/L16;rate=16000");
                                    data1.addProperty("encoding", "raw");
                                    data1.addProperty("audio", Base64.getEncoder().encodeToString(Arrays.copyOf(buffer, len)));
                                    frame1.add("data", data1);
                                    webSocket.send(frame1.toString());
                                    break;
                                case StatusLastFrame:    // 最后一帧音频status = 2 ，标志音频发送结束
                                    JsonObject frame2 = new JsonObject();
                                    JsonObject data2 = new JsonObject();
                                    data2.addProperty("status", StatusLastFrame);
                                    data2.addProperty("audio", "");
                                    data2.addProperty("format", "audio/L16;rate=16000");
                                    data2.addProperty("encoding", "raw");
                                    frame2.add("data", data2);
                                    webSocket.send(frame2.toString());
                                    break end;
                            }
                            Thread.sleep(intervel); //模拟音频采样延时
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                //System.out.println("WebSocket send message successfully!");
                ResponseData resp = json.fromJson(text, ResponseData.class);
                if (resp != null) {
                    if (resp.getCode() != 0) {
                        System.out.println( "code=>" + resp.getCode() + " error=>" + resp.getMessage() + " sid=" + resp.getSid());
                        return;
                    }
                    if (resp.getData() != null) {
                        if (resp.getData().getResult() != null) {
                            Text te = resp.getData().getResult().getText();
                            try {
                                decoder.decode(te);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (resp.getData().getStatus() == 2) {
                            // todo  resp.data.status ==2 说明数据全部返回完毕，可以关闭连接，释放资源
                            //System.out.println("最终识别结果 ==》" + decoder.toString());
                            result = decoder.toString();
                            decoder.discard();
                            webSocket.close(1000, "");
                            //等待数据全部返回完毕
                            latch.countDown();
                        } else {
                            // todo 根据返回的数据处理
                        }
                    }
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                try {
                    if (null != response) {
                        int code = response.code();
                        System.out.println("onFailure code:" + code);
                        System.out.println("onFailure body:" + response.body().string());
                        if (101 != code) {
                            System.out.println("connection failed");
                            System.exit(0);
                        }
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        };
        //创建 webSocket 对象, 处理回调事件
        WebSocket webSocket = client.newWebSocket(request, webSocketListener);
        //等待 webSocket 异步处理
        latch.await();
        return result;
    }

    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        StringBuilder builder = new StringBuilder("host: ").append(url.getHost()).append("\n").//
                append("date: ").append(date).append("\n").//
                append("GET ").append(url.getPath()).append(" HTTP/1.1");
        Charset charset = Charset.forName("UTF-8");
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(charset), "hmacsha256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(builder.toString().getBytes(charset));
        String sha = Base64.getEncoder().encodeToString(hexDigits);

        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        HttpUrl httpUrl = HttpUrl.parse("https://" + url.getHost() + url.getPath()).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(charset))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();
        return httpUrl.toString();
    }
    public static class ResponseData {
        private int code;
        private String message;
        private String sid;
        private Data data;
        public int getCode() {
            return code;
        }
        public String getMessage() {
            return this.message;
        }
        public String getSid() {
            return sid;
        }
        public Data getData() {
            return data;
        }
    }
    public static class Data {
        private int status;
        private Result result;
        public int getStatus() {
            return status;
        }
        public Result getResult() {
            return result;
        }
    }
    public static class Result {
        int bg;
        int ed;
        String pgs;
        int[] rg;
        int sn;
        Ws[] ws;
        boolean ls;
        JsonObject vad;
        public Text getText() {
            Text text = new Text();
            StringBuilder sb = new StringBuilder();
            for (Ws ws : this.ws) {
                sb.append(ws.cw[0].w);
            }
            text.sn = this.sn;
            text.text = sb.toString();
            text.sn = this.sn;
            text.rg = this.rg;
            text.pgs = this.pgs;
            text.bg = this.bg;
            text.ed = this.ed;
            text.ls = this.ls;
            text.vad = this.vad==null ? null : this.vad;
            return text;
        }
    }
    public static class Ws {
        Cw[] cw;
        int bg;
        int ed;
    }
    public static class Cw {
        int sc;
        String w;
    }
    public static class Text {
        int sn;
        int bg;
        int ed;
        String text;
        String pgs;
        int[] rg;
        boolean deleted;
        boolean ls;
        JsonObject vad;
        @Override
        public String toString() {
            return "Text{" +
                    "bg=" + bg +
                    ", ed=" + ed +
                    ", ls=" + ls +
                    ", sn=" + sn +
                    ", text='" + text + '\'' +
                    ", pgs=" + pgs +
                    ", rg=" + Arrays.toString(rg) +
                    ", deleted=" + deleted +
                    ", vad=" + (vad==null ? "null" : vad.getAsJsonArray("ws").toString()) +
                    '}';
        }
    }
    //解析返回数据，仅供参考
    public static class Decoder {
        private Text[] texts;
        private int defc = 10;
        public Decoder() {
            this.texts = new Text[this.defc];
        }
        public synchronized void decode(Text text) {
            if (text.sn >= this.defc) {
                this.resize();
            }
            if ("rpl".equals(text.pgs)) {
                for (int i = text.rg[0]; i <= text.rg[1]; i++) {
                    this.texts[i].deleted = true;
                }
            }
            this.texts[text.sn] = text;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Text t : this.texts) {
                if (t != null && !t.deleted) {
                    sb.append(t.text);
                }
            }
            return sb.toString();
        }
        public void resize() {
            int oc = this.defc;
            this.defc <<= 1;
            Text[] old = this.texts;
            this.texts = new Text[this.defc];
            for (int i = 0; i < oc; i++) {
                this.texts[i] = old[i];
            }
        }
        public void discard(){
            for(int i=0;i<this.texts.length;i++){
                this.texts[i]= null;
            }
        }
    }
    //设置录音文件路径
    public void setFilePath(String filePath) {
        this.file = filePath;
    }
}
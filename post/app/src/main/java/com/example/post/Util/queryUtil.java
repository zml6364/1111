package com.example.post.Util;

import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class queryUtil {
    //public static String session_id;

    /*单次查询*/
    public static void queryRequest(String query,String uri) throws JSONException, IOException {

        File input=null;
        File output=null;

        /*创建上传请求的文件*/
        String fileName="query.json";
        String filePath= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+ File.separatorChar+fileName;
        File queryFile=new File(filePath);
        //创建输入流，将要上传的查询命令
        if(!queryFile.exists())
        {
            queryFile.createNewFile();
            JSONObject json = new JSONObject();
            json.put("query", query );
            FileUtil.saveText(filePath,json.toString());
        }
        input = new File(filePath);//输入流，将要上传的查询命令

        /*创建存储响应数据的文件*/
        String fileName2="response.json";
        String filePath2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+ File.separatorChar+fileName2;
        File responseFile=new File(filePath2);
        //输出流，解析得到的查询命令
        if (!responseFile.exists())
        {
            responseFile.createNewFile();
            FileUtil.saveText(filePath2,"");
        }else {

            FileWriter fileWriter =new FileWriter(responseFile);
            fileWriter.write("");//文件存在则清空文件
            fileWriter.flush();
            fileWriter.close();

        }
        output = new File(filePath2);//输出流，解析得到的查询命令

        /*获取请求*/
        HttpRequest request=new HttpRequest(uri,"POST");//创建一个请求
        request.acceptJson();//设置header
        request.contentType("application/json");//设置contentType
        request.send(input).receive(output);//上传查询请求，获取响应并拷贝到本地文件
        Log.d("status","okay!!!");
    }

    /*将json文件转为字符串*/
    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取json字符串的response
    public static String getResponse(String jsonstr) {
        String response;
        //new Thread(() -> {
        try {
            JSONObject jsonObject = new JSONObject(jsonstr);
            JSONObject jsonObject2 = jsonObject.optJSONObject("response");
            response = jsonObject2.optString("response");
            //et_test.setText(response);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //}).start();
        return response;
    }

    //读取json字符串中的嵌套数据, 并返回list
    public static List<WeldingListinfo> parseJson(String jsonstr) {

        List<WeldingListinfo> wlist = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonstr);
            JSONObject jsonObject2 = jsonObject.optJSONObject("response");
            JSONObject jsonObject3 = jsonObject2.optJSONObject("data");

            Iterator<String> keys2 = jsonObject3.keys();

            while (keys2.hasNext()) {
                String key2 = String.valueOf(keys2.next());
                List<WeldingDatainfo> list = new ArrayList<>();
                WeldingDatainfo item = null;
                WeldingListinfo witem = null;
                witem = new WeldingListinfo();

                witem.setWireDiameter(key2);
                JSONArray jsonArray = jsonObject3.optJSONArray(key2);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject0 = jsonArray.optJSONObject(i);
                    //通过迭代器获取这段json当中所有的key值
                    Iterator<String> keys = jsonObject0.keys();
                    //然后通过一个循环取出所有的key值
                    while (keys.hasNext()) {
                        item = new WeldingDatainfo();
                        String key = String.valueOf(keys.next());
                        item.setParamName(key);
                        //最后就可以通过刚刚得到的key值去解析后面的json了
                        String value = jsonObject0.optString(key);
                        item.setParamValue(value);
                        list.add(item);
                    }
                }
                witem.setWeldingList(list);
                wlist.add(witem);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return wlist;
    }

/*--------------------------------分界线---------------------------------*/

    /*多次查询————待完善*/
    //获取session_id
    public static void getsession(String uri) throws IOException {
        File output=null;
        /*创建存储响应数据的文件*/
        String fileName2="session_id.json";
        String filePath2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+ File.separatorChar+fileName2;
        File responseFile=new File(filePath2);
        //输出流，解析得到的查询命令
        if (!responseFile.exists())
        {
            responseFile.createNewFile();
            FileUtil.saveText(filePath2,"");
        }else {
            FileWriter fileWriter =new FileWriter(responseFile);
            fileWriter.write("");//文件存在则清空文件
            fileWriter.flush();
            fileWriter.close();
        }
        output = new File(filePath2);//输出流，解析得到的查询命令
        /*获取请求*/
        HttpRequest request=new HttpRequest(uri,"POST");//创建一个请求
        request.acceptJson();//设置header
        request.contentType("application/json");//设置contentType
        request.receive(output);//上传查询请求，获取响应并拷贝到本地文件
        Log.d("status","okay!!!");
    }

    public static String getid() throws IOException, JSONException {
        getsession("http://202.38.247.12:8001/api/session/start");
        String id = readJsonFile("/storage/emulated/0/Download/session_id.json");
        JSONObject idobject = new JSONObject(id);
        return idobject.optString("session_id");
    }

}
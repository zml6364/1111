package com.example.uiapplication.util;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class queryUtil {

    /*ost一个请求*/
    public static HttpRequest queryRequest(String filePath_query,String filePath_response,String uri)  {

        /*--------------------------------------输入流部分-----------------------------------------*/
        File post_input = new File(filePath_query);//输入流，将要上传的查询命令

        /*-------------------------------------输出流部分-----------------------------------------*/
        File post_output = new File(filePath_response);//输出流，解析得到的查询命令
        /*获取请求*/
        HttpRequest request=new HttpRequest(uri,"POST");//创建一个请求
        request.acceptJson();//设置header
        request.contentType("application/json");//设置contentType
        return request.send(post_input).receive(post_output);//上传查询请求，获取响应并拷贝到本地文件

    }

    /*-----------------------读取json文件，转为字符串-------------------------*/
    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);//新建js文件
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");//建立js文件的输入流

            //FileReader fileReader = new FileReader(jsonFile);//创建文件输入流
            int ch = 0;
            StringBuffer sb = new StringBuffer();//字符串builder
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            reader.close();
            //fileReader.close();

            jsonStr = sb.toString();//获取js转换的字符串
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //读取json字符串中的嵌套数据, 并返回list
    public static List<Weldinginfo> parseJson(String jsonstr) {

        List<Weldinginfo> wlist = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonstr);
            JSONObject jsonObject2 = jsonObject.optJSONObject("response");//获取json的response部分创建新的json
            JSONObject jsonObject3 = jsonObject2.optJSONObject("data");//获取response的data部分创建新的json
            Iterator<String> WireDiameters = jsonObject3.keys();//获取data json的全部键（有几个焊丝直径就有几个键）

            //需要显示的参数
            ArrayList<String> keys = new ArrayList<>();
            keys.add("ArcingCurrent");
            keys.add("PulseBaseCurrent");
            keys.add("PulsingCurrent");
            keys.add("PulseFreqency");
            keys.add("WireFeedSpeed");
            keys.add("Voltage command value");
            keys.add("Guideline value for material");
            keys.add("Amperage guideline value");
            keys.add("Voltage guideline value");

            //遍历所有焊丝直径
            while (WireDiameters.hasNext()) {

                String WireDiameter = String.valueOf(WireDiameters.next());//键对应的字符串（焊丝直径）
                List<oneGroupInfo> list = new ArrayList<>();//建立list存放单组数据

                Weldinginfo witem = new Weldinginfo();
                /*解析一个焊丝直径下的数据列表*/
                JSONArray params= jsonObject3.optJSONArray(WireDiameter);//获取data下一个焊丝直径的全部数据（一组）
                //遍历取出这组数据下的所有元素
                for (int i = 0; i < params.length(); i++) {

                    JSONObject param = params.optJSONObject(i);//取出单个参数
                    //通过迭代器获取这段json当中所有的key值
                    Iterator<String> param_keys = param.keys();//获取单个参数的所有键
                    //然后通过一个循环取出Keys包含的参数
                    while (param_keys.hasNext()) {
                        oneGroupInfo item = new oneGroupInfo();//创建单个参数对象
                        String param_name = param_keys.next().toString();
                        if (keys.contains(param_name)) {
                            String value = param.optString(param_name);//获取参数名称对应的数值
                            item.setParamValue(value);//设置参数的数值
                            item.setParamName(param_name);//设置参数名称
                            list.add(item);//将设置好的参数加入列表当中
                        }
                    }
                }
                witem.setWireDiameter(WireDiameter);//存储焊丝直径
                witem.setWeldingList(list);//存储一组参数

                wlist.add(witem);//存储一组焊丝直径下的参数
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return wlist;
    }

    /*--------------------------------分界线---------------------------------*/
    /*待补充*/
    public static String getResponse(String jsonStr) throws JSONException {

        String response=null;
        JSONObject jsonObject_response_data=null;
        JSONObject jsonObject_response_response=null;
        JSONObject jsonObject=new JSONObject(jsonStr);//获取response的data部分创建新的json

        try {
            jsonObject_response_data = jsonObject.optJSONObject("response").optJSONObject("data");//读取response下的data对象
            /*data非空时读取response*/
            if(jsonObject_response_data!=null)
            {
                jsonObject_response_response = jsonObject.optJSONObject("response");//有data的话就读取response对应的值

                Iterator<String> keys = jsonObject_response_response.keys();
                String keyname=String.valueOf(keys.next());
                response=jsonObject_response_response.optString(keyname);
            }

        }catch (Exception e){
            /*data 为空时直接读取response*/
            try {
                Iterator<String> keys = jsonObject.keys();
                String keyname=String.valueOf(keys.next());
                response=jsonObject.optString(keyname);
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }

        }
        return response;
    }

    public static String getId(String filepath) throws IOException, JSONException {

        /*发起空的post请求*/
        File post_output=null;
        post_output = new File(filepath);//输出流，解析得到的查询命令
        /*获取请求*/
        //HttpRequest request=new HttpRequest("http://202.38.247.12:8001/api/session/start","POST");//创建一个请求
        //61.142.130.81:8001
        HttpRequest request=new HttpRequest("http://61.142.130.81:8001/api/session/start","POST");
        request.acceptJson();//设置header
        request.contentType("application/json");//设置contentType
        request.receive(post_output);//上传查询请求，获取响应并拷贝到本地文件
        /*解析id*/
        String session_id=null;
        String id = readJsonFile(filepath);//读取id的json文件
        JSONObject jsonObject = new JSONObject(id);
        Iterator<String> keys = jsonObject.keys();
        String keyname=String.valueOf(keys.next());
        session_id=jsonObject.optString(keyname);
        return session_id;
    }



}
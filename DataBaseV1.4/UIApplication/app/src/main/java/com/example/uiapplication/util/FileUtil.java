package com.example.uiapplication.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    public static  void saveText(String path,String txt){

        BufferedWriter os=null;

        try {
            os=new BufferedWriter(new FileWriter(path));
            os.write(txt);
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (os!=null)
            {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public static String openText(String path)
    {
        BufferedReader is=null;
        StringBuilder sb=new StringBuilder();
        try {
            is=new BufferedReader(new FileReader(path));
            String line=null;
            while ((line=is.readLine())!=null)
            {
                sb.append(line);
            }
            line= is.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if(is!=null)
                try {
                    is.close();
                } catch (IOException ex) {
                    e.printStackTrace();
                }
        }

        return sb.toString();
    }

    public static void saveImage(String path, Bitmap bitmap)
    {
        FileOutputStream fos=null;
        try {
            fos=new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);

        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if (fos!=null)
                try {
                    fos.close();
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
        }

    }

    public static Bitmap openImage(String path)
    {
        FileInputStream fis=null;
        Bitmap bitmap=null;
        try {
            fis=new FileInputStream(path);
            bitmap= BitmapFactory.decodeStream(fis);
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if (fis!=null)
            {
                try {
                    fis.close();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }
        return bitmap;
    }


}

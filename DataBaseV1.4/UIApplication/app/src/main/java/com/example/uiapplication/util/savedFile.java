package com.example.uiapplication.util;

public class savedFile {

    private String filename;
    private String filepath;

    public savedFile(String filename, String filepath) {
        this.filename = filename;
        this.filepath = filepath;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFilename() {
        return filename;
    }

    public String getFilepath() {
        return filepath;
    }
}

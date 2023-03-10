package com.jilani.ifta.ui;

public class Button {

    private String name;
    private String url;
    private String classs;
    private boolean isCloseBtn = false;

    public Button(){}

    public Button(String name, String url, String classs) {
        this.name = name;
        this.url = url;
        this.classs = classs;
    }

    public boolean isCloseBtn() {
        return isCloseBtn;
    }

    public void setCloseBtn(boolean closeBtn) {
        isCloseBtn = closeBtn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClasss() {
        return classs;
    }

    public void setClasss(String classs) {
        this.classs = classs;
    }
}

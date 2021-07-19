package com.testpro.model;

public class DressSlideItem {

    private int id;
    private String dressTyp;
    private String imgNm;

    public DressSlideItem() {
    }

    public DressSlideItem(String dressTyp, String imgNm) {
        this.dressTyp = dressTyp;
        this.imgNm = imgNm;
    }

    public DressSlideItem(int id, String dressTyp, String imgNm) {
        this.id = id;
        this.dressTyp = dressTyp;
        this.imgNm = imgNm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDressTyp() {
        return dressTyp;
    }

    public void setDressTyp(String dressTyp) {
        this.dressTyp = dressTyp;
    }

    public String getImgNm() {
        return imgNm;
    }

    public void setImgNm(String imgNm) {
        this.imgNm = imgNm;
    }
}
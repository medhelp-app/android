package com.medhelp.medhelp.model;

public class FeedItem {

    private String _id;
    private String idUser;
    private String name;
    private String date;
    private String text;
    private String type;

    public FeedItem() {
    }

    public FeedItem(String _id, String name, String idUser, String date, String text, String type) {
        this._id = _id;
        this.name = name;
        this.idUser = idUser;
        this.date = date;
        this.text = text;
        this.type = type;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

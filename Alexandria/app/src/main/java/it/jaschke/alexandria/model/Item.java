package it.jaschke.alexandria.model;

import com.google.gson.annotations.Expose;

/**
 * Created by sknutti on 9/10/15.
 */
public class Item {
    @Expose
    private String kind;
    @Expose
    private String id;
    @Expose
    private String etag;
    @Expose
    private String selfLink;
    @Expose
    private Book volumeInfo;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    public Book getBookInfo() {
        return volumeInfo;
    }

    public void setBookInfo(Book volumeInfo) {
        this.volumeInfo = volumeInfo;
    }
}

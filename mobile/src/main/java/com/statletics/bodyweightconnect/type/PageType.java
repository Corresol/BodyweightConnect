package com.statletics.bodyweightconnect.type;

/**
 * Created by Tonni on 13.11.2016.
 */

public class PageType {

    private String id;
    private String name;
    private String url;

    public String getId() {
        return id;
    }

    public PageType setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PageType setName(String name) {
        this.name = name;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public PageType setUrl(String url) {
        this.url = url;
        return this;
    }
}

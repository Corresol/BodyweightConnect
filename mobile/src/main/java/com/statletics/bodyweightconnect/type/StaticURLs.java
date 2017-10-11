package com.statletics.bodyweightconnect.type;

/**
 * Created by Tonni on 28.11.2016.
 */

public enum StaticURLs {
    IMPRINT("https://bodyweight-connect.com/imprint.html"),
    TERMS("https://bodyweight-connect.com/terms.html"),
    PRIVACY("https://bodyweight-connect.com/privacy.html");

    private String url;

    StaticURLs(String url) {
        this.url = url;
    }

    public String getUrl(){
        return url;
    }
}

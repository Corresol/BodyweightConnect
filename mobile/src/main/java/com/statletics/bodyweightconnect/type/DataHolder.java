package com.statletics.bodyweightconnect.type;

import java.io.Serializable;

/**
 * Created by Tonni on 07.06.2016.
 */
public class DataHolder implements Serializable {


    private String url;
    private DeviceType type;

    public DataHolder() {
    }

    public DataHolder(String url, DeviceType type) {
        this.url = url;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }
}

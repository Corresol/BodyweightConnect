package com.statletics.bodyweightconnect.util;

import android.content.Context;

import com.statletics.bodyweightconnect.type.PageType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by Tonni on 13.11.2016.
 */

public class PageModel {

    private final static String FILENAME = "page.properties";
    private final static String PREFIX = "PageType[?].";

    public List<PageType> readPageTypes(){
        List<PageType> pageTypes = new ArrayList<>();
        try {
            FileInputStream in =  ActivityManager.getInstance().getCurrentActivity().openFileInput(FILENAME);
            Properties p = new Properties();
            p.load(in);
            in.close();
            List<String> uuids = new ArrayList<>();
            for(Object k:p.keySet()){
                String key = (String)k;
                if(key.endsWith(".id")){
                    uuids.add(p.getProperty(key,"-1"));
                }
            }
            for(String uuid:uuids){
                PageType pt = new PageType().setId(uuid).setName(p.getProperty(PREFIX.replace("?",uuid)+"name")).setUrl(p.getProperty(PREFIX.replace("?",uuid)+"url"));
                pageTypes.add(pt);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pageTypes;
    }

    public boolean storePageTypes(List<PageType> data){
        try {
            FileOutputStream out =  ActivityManager.getInstance().getCurrentActivity().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            Properties p = new Properties();
            for(PageType pt:data){
                p.put(PREFIX.replace("?",pt.getId())+"id",pt.getId());
                p.put(PREFIX.replace("?",pt.getId())+"name",pt.getName());
                p.put(PREFIX.replace("?",pt.getId())+"url",pt.getUrl());
            }
            p.store(out,"nothing to comment");
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<PageType> addPageType(PageType page){
        List<PageType> pts = readPageTypes();
        pts.add(page);
        storePageTypes(pts);
        return pts;
    }

    public List<PageType> removePageType(PageType page){
        List<PageType> pts = readPageTypes();
        for (PageType pt: pts){
            if(Objects.equals(pt.getId(),page.getId())){
                pts.remove(pt);
                break;
            }
        }
        storePageTypes(pts);
        return pts;
    }

}

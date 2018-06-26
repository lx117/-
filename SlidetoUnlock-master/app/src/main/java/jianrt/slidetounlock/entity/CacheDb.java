package jianrt.slidetounlock.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Author:11719<p>
 * CreateDate:2016/12/29<p>
 * Fuction:<p>
 */
@Entity
public class CacheDb {
    @Id(autoincrement = true)
    private Long id;
    private String url;
    private String jsonObject;
    @Generated(hash = 723493550)
    public CacheDb(Long id, String url, String jsonObject) {
        this.id = id;
        this.url = url;
        this.jsonObject = jsonObject;
    }
    @Generated(hash = 2089471188)
    public CacheDb() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getJsonObject() {
        return this.jsonObject;
    }
    public void setJsonObject(String jsonObject) {
        this.jsonObject = jsonObject;
    }
}

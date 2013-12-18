package be.rvponp.build.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: canas
 * Date: 7/11/13
 * Time: 10:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class Release {
    private Map<String, String> informations = new HashMap<String, String>();
    private String url;
    private String name;

    public String getInformation(String key) {
        return informations.get(key);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getStartBuild() {
        String startBuild = informations.get("startbuild");
        if (startBuild != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd.HHmm");
            try {
                return simpleDateFormat.parse(startBuild);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addInformation(Object key, Object value) {
        if (key != null) {
            if (value != null) {
                informations.put(key.toString(), value.toString());
            } else {
                informations.put(key.toString(), null);
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }
}

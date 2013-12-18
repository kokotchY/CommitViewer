package be.rvponp.build.util;

import be.rvponp.build.model.Release;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: canas
 * Date: 7/11/13
 * Time: 10:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReleaseUtil {
    public static List<Release> getValidRelease() {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet("http://thedev01:8080/hudson/job/Local%20-%20Production/ws/");
        BufferedReader reader = null;
        List<Release> releases = new ArrayList<Release>();
        try {
            HttpResponse response = httpClient.execute(get);
            if (response.getEntity() != null) {
                HttpEntity entity = response.getEntity();
                reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("table class=\"fileList\"")) {
                        Pattern pattern = Pattern.compile("<a href=\"(theseos-[0-9]{2}.[0-9]{2}.[0-9]{2})\">(theseos-[0-9]{2}.[0-9]{2}.[0-9]{2})</a>");
                        Matcher matcher = pattern.matcher(line);
                        while (matcher.find()) {
                            Release release = createRelease(matcher.group(1));
                            if (isValidStartDate(release)) {
                                releases.add(release);
                            }
                        }
                    }
                }
                EntityUtils.consume(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return releases;
    }

    private static Release createRelease(String name) {
        Release release = new Release();
        release.setUrl("http://thedev01:8080/hudson/job/Local%20-%20Production/ws/"+name);
        release.setName(name);
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(release.getUrl() + "/informations");
        try {
            HttpResponse response = httpClient.execute(get);
            if (response != null) {
                HttpEntity entity = response.getEntity();
                Properties properties = new Properties();
                properties.load(entity.getContent());
                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    release.addInformation(entry.getKey(), entry.getValue());
                }
                EntityUtils.consume(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return release;
    }

    private static boolean isValidStartDate(Release name) {
        return name.getStartBuild() != null;
    }
}

package be.rvponp.build.util;

/**
 * Created with IntelliJ IDEA.
 * User: vermb
 * Date: 7/30/13
 * Time: 2:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class JiraEntry {
    private String id="";
    private String status="";
    private String fixVersion="";

    public JiraEntry(String id, String status, String fixVersion){
        this.id = id;
        this.status = status;
        this.fixVersion = fixVersion;
    }

    public JiraEntry(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFixVersion() {
        return fixVersion;
    }

    public void setFixVersion(String fixVersion) {
        this.fixVersion = fixVersion;
    }

    public String toString(){
        return id;
    }
}

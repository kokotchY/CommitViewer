package be.rvponp.build.util;

import com.atlassian.jira.rpc.soap.beans.RemoteComponent;

/**
 * User: vermb
 * Date: 7/30/13
 * Time: 2:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class JiraEntry {
    public static final Object JIRA_LINK = "http://jira:8080/browse/";

    private String id;
    private JiraStatus status;
    private String fixVersion;
    private String assignee;
    private boolean valid;
    private String externalLink;
    private String resolvedOn;
    private RemoteComponent[] component;

    public JiraEntry(String id, JiraStatus status, String fixVersion) {
        this.id = id;
        this.status = status;
        this.fixVersion = fixVersion;
        this.valid = true;
        this.resolvedOn = "";
    }

    public JiraEntry() {
        id = "";
        status = JiraStatus.Undefined_A;
        fixVersion = "";
        assignee = "";
        valid = true;
        this.resolvedOn = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JiraStatus getStatus() {
        return status;
    }

    public void setStatus(JiraStatus status) {
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = JiraStatus.valueOf(status);
    }

    public String getFixVersion() {
        return fixVersion;
    }

    public void setFixVersion(String fixVersion) {
        this.fixVersion = fixVersion;
    }

    public String toString() {
        return id;
    }


    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getExternalLink() {
        return JIRA_LINK + getId();
    }

    public void setExternalLink(String externalLink) {
        this.externalLink = externalLink;
    }

    public void setResolvedOn(String resolvedOn) {
        this.resolvedOn = resolvedOn;
    }

    public String getResolvedOn() {
        return resolvedOn;
    }

    public void setComponent(RemoteComponent[] component) {
        this.component = component;
    }

    public RemoteComponent[] getComponent() {
        return component;
    }
}

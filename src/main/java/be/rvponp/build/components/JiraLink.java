package be.rvponp.build.components;

import be.rvponp.build.util.JiraEntry;
import be.rvponp.build.util.JiraStatus;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Link;

/**
 * Created with IntelliJ IDEA.
 * User: vermb
 * Date: 8/8/13
 * Time: 10:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class JiraLink extends Link {
    public JiraEntry jiraEntry;

    public JiraLink(JiraEntry jira){
        super(jira.getId(), new ExternalResource(jira.getExternalLink()));
        jiraEntry = jira;
        generateLink();
    }

    private void generateLink() {
        setTargetName("_BLANK");
        setDescription(jiraEntry.getStatus().toString());
        setIcon(createJiraStatusImage(jiraEntry.getStatus()));
    }

    private ThemeResource createJiraStatusImage(JiraStatus status) {
        String imageName = "";
        if(status == JiraStatus.Open)
            imageName = "status_open";
        else if (status == JiraStatus.InProgress)
            imageName = "status_inprogress";
        else if (status == JiraStatus.Closed)
            imageName = "status_closed";
        else if (status == JiraStatus.Reopened)
            imageName = "status_reopened";
        else if (status == JiraStatus.Resolved)
            imageName = "status_resolved";

        return (new ThemeResource("img/"+imageName+".gif"));
    }

    public String toString(){
        return jiraEntry.JIRA_LINK+jiraEntry.getId()+" ("+jiraEntry.getStatus()+")";
    }
}

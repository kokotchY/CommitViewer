package be.rvponp.build.components;

import be.rvponp.build.util.JiraEntry;
import be.rvponp.build.util.JiraStatus;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Link;

/**
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
        switch (status) {
            case Open:
                imageName = "status_open";
                break;
            case InProgress:
                imageName = "status_inprogress";
                break;
            case Closed:
                imageName = "status_closed";
                break;
            case Reopened:
                imageName = "status_reopened";
                break;
            case Resolved:
                imageName = "status_resolved";
                break;
        }

        return (new ThemeResource("img/"+imageName+".gif"));
    }

    public String toString(){
        return JiraEntry.JIRA_LINK +jiraEntry.getId()+" ("+jiraEntry.getStatus()+")";
    }
}

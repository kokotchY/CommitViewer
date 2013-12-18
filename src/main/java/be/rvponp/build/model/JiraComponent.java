package be.rvponp.build.model;

/**
 * A jira component
 * User: canas
 * Date: 12/17/13
 * Time: 11:56 PM
 */
public class JiraComponent {

    private String name;

    private JiraProject jiraProject;

    public JiraComponent(JiraProject jiraProject, String name) {
        this.jiraProject = jiraProject;
        this.name = name;
    }

    public JiraProject getJiraProject() {
        return jiraProject;
    }

    public void setJiraProject(JiraProject jiraProject) {
        this.jiraProject = jiraProject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

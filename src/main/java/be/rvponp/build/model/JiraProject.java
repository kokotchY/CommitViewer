package be.rvponp.build.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A jira project
 * User: canas
 * Date: 12/17/13
 * Time: 11:56 PM
 */
public class JiraProject {

    private String name;

    private List<JiraComponent> components = new ArrayList<JiraComponent>();

    public JiraProject(String name) {
        this.name = name;
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

    public void addComponent(JiraComponent component) {
        components.add(component);
    }

    public List<JiraComponent> getComponents() {
        return components;
    }
}

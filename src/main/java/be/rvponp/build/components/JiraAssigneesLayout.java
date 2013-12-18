package be.rvponp.build.components;

import be.rvponp.build.util.ADUserResolver;
import be.rvponp.build.model.JiraEntry;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import java.util.List;

/**
 * User: vermb
 * Date: 8/8/13
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class JiraAssigneesLayout extends HorizontalLayout{

    public JiraAssigneesLayout(List<JiraEntry> jiraIds){
        super();
        setSpacing(true);
        generateJiraAssignee(jiraIds);
    }

    private void generateJiraAssignee(List<JiraEntry> jiraIds) {
        if(!jiraIds.isEmpty())
        {
            for(JiraEntry s : jiraIds){
                if (s.isValid()){
                    Label j = new Label(ADUserResolver.getFullUsernameByID(s.getAssignee()));
                    j.setDescription(s.getId());
                    addComponent(j);
                }
            }
        }
    }

    public String toString(){
        StringBuilder returnedString = new StringBuilder();
        for(Component c : this){
            returnedString.append(c);
        }
        return returnedString.toString();
    }
}

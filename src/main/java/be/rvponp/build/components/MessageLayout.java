package be.rvponp.build.components;

import be.rvponp.build.util.JiraEntry;
import be.rvponp.build.util.JiraLinkCommitParser;
import be.rvponp.build.util.JiraStatus;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

import java.util.List;
import java.util.StringTokenizer;

/**
 * User: vermb
 * Date: 8/8/13
 * Time: 10:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class MessageLayout extends HorizontalLayout{


    public MessageLayout(String message, Boolean parsingJira){
        super();
        generateMessage(message, parsingJira);
    }

    private void generateMessage(String message, Boolean parsingJira){

        setSpacing(true);

        String PATTERN_JIRA ="([A-Z]+-[0-9]+)";
        String messageWithoutJiraIds = message.replaceAll(PATTERN_JIRA, "§");
        StringTokenizer tokens = new StringTokenizer(messageWithoutJiraIds, "§", true);

        List<JiraEntry> jiraIds = JiraLinkCommitParser.parseJiraIdentifier(message, parsingJira);

        if(!jiraIds.isEmpty())
        {
            while(tokens.hasMoreTokens()){
                String tok = tokens.nextToken();

                if(tok.equals("§")){
                    JiraEntry j = jiraIds.remove(0);

                    if(j.isValid()){
                        if(j.getStatus() != JiraStatus.Closed && j.getStatus() != JiraStatus.Resolved)
                        {
                            Link iconWarning = new Link();
                            iconWarning.setIcon(new ThemeResource("img/warning.png"));
                            iconWarning.setDescription("This jira is not closed or resolved");
                            addComponent(iconWarning);
                        }
                        JiraLink jiraLink = new JiraLink(j);

                        addComponent(jiraLink);
                    }else{
                        addComponent(new Label(j.getId()));
                    }
                }else{
                    addComponent(new Label(tok));
                }
            }
        }else{
            addComponent(new Label(message));
        }
    }



    public String toString(){
        StringBuilder returnedString = new StringBuilder();
        for(Component c : this){
            if((c instanceof JiraLink) || (c instanceof Label))
                returnedString.append(c.toString());
        }
        return returnedString.toString();
    }
}

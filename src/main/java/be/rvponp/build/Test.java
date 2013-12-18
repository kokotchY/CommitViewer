package be.rvponp.build;

import be.rvponp.build.model.JiraComponent;
import be.rvponp.build.model.JiraProject;
import be.rvponp.build.util.Jira;
import be.rvponp.build.util.SOAPSession;
import com.atlassian.jira.rpc.soap.beans.RemoteComponent;
import com.atlassian.jira.rpc.soap.beans.RemoteCustomFieldValue;
import com.atlassian.jira.rpc.soap.beans.RemoteIssue;
import com.atlassian.jira.rpc.soap.beans.RemoteProject;
import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: canas
 * Date: 7/11/13
 * Time: 10:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) {
        new Test();
    }

    public Test() {
//        String date = "2013-07-09T08:13:55.242656Z";
//        String date = "2013-07-09T08:13:55";
//        SimpleDateFormat revisionFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
//        SimpleDateFormat revisionFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        /*try {
            Date parse = revisionFormat.parse(date);
            DateTime dateTime = new DateTime(parse);
            DateTime dateTime1 = dateTime.withFieldAdded(DurationFieldType.hours(), 2);
            System.out.println(dateTime1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            SOAPSession jiraWebService = new SOAPSession(new URL("http://jira:8080/rpc/soap/jirasoapservice-v2"));
            jiraWebService.connect("vermb", "vermb");

            List<String> projets = new ArrayList<String>();
            projets.add("PRODUCTION");
            projets.add("VAAD");
            projets.add("ID");

            for (String project : projets) {
                RemoteComponent[] components = jiraWebService.getJiraSoapService().getComponents(jiraWebService.getAuthenticationToken(),
                        project);
                System.out.println("Components of "+project);
                for (RemoteComponent component : components) {
                    System.out.println(component.getName());
                }
            }
            RemoteIssue remoteIssue = jiraWebService.getJiraSoapService().getIssue(jiraWebService.getAuthenticationToken
                    (), "PRODUCTION-6906");
            for (RemoteComponent component : remoteIssue.getComponents()) {
                System.out.println(component.getName());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }*/

//        List<JiraProject> projects = new ArrayList<JiraProject>();
//        JiraProject prod = new JiraProject("PRODUCTION");
//        JiraProject vaad = new JiraProject("VAAD");
//        projects.add(prod);
//        projects.add(vaad);
//
//        List<JiraComponent> components = new ArrayList<JiraComponent>();
//        JiraComponent test1 = new JiraComponent(prod, "test1");
//        prod.addComponent(test1);
//        components.add(test1);
//        System.out.println(getStringFilter(projects, components));

        try {
            SOAPSession jiraWebService = new SOAPSession(new URL("http://jira:8080/rpc/soap/jirasoapservice-v2"));
            jiraWebService.connect("vermb", "vermb");
            RemoteIssue issue = jiraWebService.getJiraSoapService().getIssue(jiraWebService.getAuthenticationToken(), "PRODUCTION-6662");
            for (RemoteCustomFieldValue customFieldValue : issue.getCustomFieldValues()) {
                if (customFieldValue.getCustomfieldId().equals("customfield_10200")) {
                    System.out.println(getCustomFieldValue(customFieldValue));
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private static String getCustomFieldValue(RemoteCustomFieldValue field) {
        StringBuilder builder = new StringBuilder("[");
        int idx = 0;
        for (String value : field.getValues()) {
            builder.append(value);
            if (idx++ < field.getValues().length-1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    private String getStringFilter(List<JiraProject> projects, List<JiraComponent> components) {
        List<JiraComponent> cleanedComponents = new ArrayList<JiraComponent>();
        for (JiraComponent component : components) {
            boolean toBeKept = true;
            for (JiraProject project : projects) {
                if (component.getJiraProject() == project) {
                    toBeKept = false;
                }
            }
            if (toBeKept) {
                cleanedComponents.add(component);
            }
        }
        StringBuilder builderProject = new StringBuilder();
        if (projects.size() > 0) {
            builderProject.append("projects=[");
            int idx = 0;
            for (JiraProject project : projects) {
                builderProject.append(project.getName());
                if (idx++ < projects.size()-1) {
                    builderProject.append(",");
                }
            }
            builderProject.append("]");
        }

        StringBuilder builderComponents = new StringBuilder();
        boolean hasComponents = false;
        if (cleanedComponents.size() > 0) {
            hasComponents = true;
            builderComponents.append("components=[");
            int idx = 0;
            for (JiraComponent component : cleanedComponents) {
                builderComponents.append(component.getName() + "@" + component.getJiraProject().getName());
                if (idx++ < cleanedComponents.size()-1) {
                    builderComponents.append(",");
                }
            }
            builderComponents.append("]");
        }

        if (hasComponents) {
            return builderProject.append(",").append(builderComponents).toString();
        } else {
            return builderProject.toString();
        }
    }
}

package be.rvponp.build.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: canas
 * Date: 7/6/13
 * Time: 9:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class JiraLinkCommitParser {

    private static String jiraUrl = "http://jira:8080/browse/";

    /**
     * Replace any jira identifier ([A-Z]+-[0-9]+) with a link to it
     * @param text String to parse
     * @return New string with the jira identifier replaced with a link to it
     */
    public static String parseJiraLink(String text) {
        Pattern pattern = Pattern.compile("([A-Z]+-[0-9]+)");
        Matcher matcher = pattern.matcher(text);
        String result = text;
        while (matcher.find()) {
            String jiraId = matcher.group(1);
            String url = jiraUrl + jiraId;
            result = matcher.replaceAll("<a href=\"" + url + "\">" + jiraId + "</a>");
        }
        return result;
    }

    public static String parseJiraLink(String text, List<String> validProjects) {
        Pattern pattern = Pattern.compile("([A-Z]+)-([0-9]+)");
        Matcher matcher = pattern.matcher(text);
        StringBuffer s = new StringBuffer();
        while (matcher.find()) {
            String jiraProject = matcher.group(1);
            String jiraNb = matcher.group(2);
            if (validProjects.contains(jiraProject)) {
                String jiraId = jiraProject + "-" + jiraNb;
                String url = jiraUrl + jiraId;
                matcher.appendReplacement(s, "<a href=\"" + url + "\">" + jiraId + "</a>");
            }
        }
        matcher.appendTail(s);
        return s.toString();

    }
}

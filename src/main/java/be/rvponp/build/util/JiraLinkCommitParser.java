package be.rvponp.build.util;

import be.rvponp.build.model.JiraEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: canas
 * Date: 7/6/13
 * Time: 9:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class JiraLinkCommitParser {
    /**
     * Replace any jira identifier ([A-Z]+-[0-9]+) with a link to it
     *
     * @param text String to parse
     * @param parsingJira Flag to parse the jira status or not
     * @return New string with the jira identifier replaced with a link to it
     */
    public static List<JiraEntry> parseJiraIdentifier(String text, Boolean parsingJira) {
        Pattern pattern = Pattern.compile("([A-Z]+-[0-9]+)");
        Matcher matcher = pattern.matcher(text);
        List<String> matchResult = new ArrayList<String>();
        while(matcher.find()){
            matchResult.add(matcher.group());
        }

        return Jira.getJiraByIds(matchResult, parsingJira);
    }


}

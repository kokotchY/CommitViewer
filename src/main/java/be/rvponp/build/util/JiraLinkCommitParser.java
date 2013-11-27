package be.rvponp.build.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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



    /**
     * Replace any jira identifier ([A-Z]+-[0-9]+) with a link to it
     *
     * @param text String to parse
     * @param parsingJira
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

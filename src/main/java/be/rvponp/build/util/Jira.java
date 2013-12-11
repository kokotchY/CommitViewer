package be.rvponp.build.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;


import be.rvponp.build.CommitViewerConfiguration;
import com.atlassian.jira.rpc.exception.RemoteAuthenticationException;
import com.atlassian.jira.rpc.exception.RemotePermissionException;
import com.atlassian.jira.rpc.soap.beans.RemoteCustomFieldValue;
import com.atlassian.jira.rpc.soap.beans.RemoteIssue;
import com.atlassian.jira.rpc.soap.beans.RemoteVersion;
import org.apache.log4j.Logger;

public class Jira {
	
	private static final String KEY_SOAP_URL = "jira.url";
	private static final String KEY_USER = "jira.user";
	private static final String KEY_PASS = "jira.pass";
    private static final CommitViewerConfiguration config = CommitViewerConfiguration.getInstance();
    private static final String FIELD_RESOLVED_ON = "customfield_10200";
    private static SOAPSession jiraWebService = null;
    private static final Logger log = Logger.getLogger(Jira.class);

    private Jira()
    {
        try {
            jiraWebService = new SOAPSession(new URL(config.getProperty(KEY_SOAP_URL)));
            jiraWebService.connect(config.getProperty(KEY_USER), config.getProperty(KEY_PASS));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private static SOAPSession getJiraWebService(){
        if(jiraWebService == null){
            new Jira();
        }
        return jiraWebService;
    }

    public static JiraEntry getJiraById(String id, Boolean parsingJira){
        JiraEntry jiraEntry = new JiraEntry();
        jiraEntry.setId(id);
        if (parsingJira) {
            int connectionTry = 0;
            try {
                RemoteIssue remoteIssue = getJiraWebService().getJiraSoapService().getIssue(getJiraWebService().getAuthenticationToken(), id.trim());
                RemoteVersion[] version = remoteIssue.getFixVersions();
                StringBuilder buffer = new StringBuilder();
                for (RemoteCustomFieldValue field : remoteIssue.getCustomFieldValues()) {
                    if (FIELD_RESOLVED_ON.equals(field.getCustomfieldId())) {
                        jiraEntry.setResolvedOn(getCustomFieldValue(field));
                    }
                }

                for (RemoteVersion v : version) {
                    buffer.append(v.getName()).append(" ");
                }
                String fixVersions = buffer.toString();

                jiraEntry.setStatus(JiraStatus.values()[Integer.valueOf(remoteIssue.getStatus())]);
                jiraEntry.setFixVersion(fixVersions);
                jiraEntry.setAssignee(remoteIssue.getAssignee());
            } catch (RemoteAuthenticationException m){
                log.warn("Probably disconnected. Try to reconnect.");
                connectionTry++;
                if (connectionTry < 3) {
                    new Jira(); //Probably disconnected, try to reconnect
                    return getJiraById(id, parsingJira);
                } else {
                    log.error("Impossible to reconnect, set unknown status to jiraEntry");
                    setUnknownStatus(jiraEntry);
                }
            } catch (RemotePermissionException r){
                jiraEntry.setValid(false); //No permission or no valid
            } catch (RemoteException e) {
                jiraEntry.setValid(false); //Something else
            }
        } else {
            setUnknownStatus(jiraEntry);
        }

        return jiraEntry;
    }

    private static String getCustomFieldValue(RemoteCustomFieldValue field) {
        StringBuilder builder = new StringBuilder("[");
        for (String value : field.getValues()) {
            builder.append(value).append(",");
        }
        builder.append("]");
        return builder.toString();
    }

    private static void setUnknownStatus(JiraEntry jiraEntry) {
        jiraEntry.setStatus(JiraStatus.Undefined_A);
        jiraEntry.setFixVersion("Unknown");
        jiraEntry.setAssignee("Unknown");
        jiraEntry.setValid(true);
    }

    public static List<JiraEntry> getJiraByIds(List<String> ids, Boolean parsingJira){
        List<JiraEntry> results = new ArrayList<JiraEntry>();

        for(String id : ids){
            results.add(getJiraById(id, parsingJira));
        }

        return results;
    }

//	public Version getCurrentVersion()
//	{
//		versionSet = new TreeSet<Version>();
//
//		try {
//			RemoteVersion[] returnedVersion = jiraSoapService.getVersions(authToken, PROJECT);
//
//			for(RemoteVersion r : returnedVersion)
//			{
//				if(r.isArchived() == false && r.getName().matches("^theseos-[0-9]{2}.[0-9]{2}.[0-9]{2}"))
//					versionSet.add(new Version(r));
//			}
//
//			for(Version v : versionSet)
//			{
//				if(!v.isReleased())
//					return v;
//			}
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//
//		return null;
//	}


//	private List<Issue> getIssuesAttachedToAVersion(Version version, boolean isResolved)
//	{
//		RemoteIssue[] returnedFilter;
//		List<Issue> returnList = new LinkedList<Issue>();
//
//		try {
//			if(isResolved)
//
//					returnedFilter = jiraSoapService.getIssuesFromJqlSearch(authToken, "project = "+PROJECT+" AND fixVersion = \""+version.getName()+"\" AND status in (Resolved, Closed) ORDER BY priority DESC, key DESC", MAX_RESULT_LIMIT);
//
//			else
//				returnedFilter = jiraSoapService.getIssuesFromJqlSearch(authToken, "project = "+PROJECT+" AND fixVersion = \""+version.getName()+"\" AND status in (Open, Reopened) ORDER BY priority DESC, key DESC", MAX_RESULT_LIMIT);
//
//			for(RemoteIssue r : returnedFilter)
//			{
//				returnList.add(new Issue(r));
//			}
//
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//
//		return returnList;
//
//	}

    public static void main(String[] args) throws MalformedURLException, RemoteException {

       // http://athena/personnel/perk001.jsp?idUser=vermb

//        URL url = new URL("http://athena/personnel/perk001.jsp?idUser=vermb");
//        BufferedReader in = null;
//        try {
//            in = new BufferedReader(
//                    new InputStreamReader(url.openStream()));
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//        String inputLine;
//        StringBuilder response = new StringBuilder();
//        try {
//            while ((inputLine = in.readLine()) != null)
//            {
//                response.append(inputLine);
//                //System.out.println(inputLine);
//               // in.close();
//            }
//
//
//            Pattern pattern = Pattern.compile("<h3>(.*?)<img.*alt=\"(.*)\".*</h3>");
//            Matcher matcher = pattern.matcher(response);
//            while(matcher.find()){
//                System.out.println(matcher.group(0));
//                System.out.println(matcher.group(1));
//                System.out.println(matcher.group(2));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }


        System.out.println(ADUserResolver.getFullUsernameByID("can"));


    }

	
}



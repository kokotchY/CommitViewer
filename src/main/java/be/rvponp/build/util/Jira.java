package be.rvponp.build.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;


import com.atlassian.jira.rpc.exception.RemoteAuthenticationException;
import com.atlassian.jira.rpc.exception.RemotePermissionException;
import com.atlassian.jira.rpc.soap.beans.RemoteIssue;
import com.atlassian.jira.rpc.soap.beans.RemoteVersion;



public class Jira {
	
	private static final String SOAP_URL = "http://jira:8080/rpc/soap/jirasoapservice-v2";
    //private static final String SOAP_URL = "http://lts-jira01:8080/rpc/soap/jirasoapservice-v2";
	private static final String LOGIN_NAME = "vermb";
	private static final String LOGIN_PWD = "vermb";

	private static SOAPSession jiraWebService = null;

    private Jira()
    {
        try {

            jiraWebService = new SOAPSession(new URL(SOAP_URL));
            jiraWebService.connect(LOGIN_NAME, LOGIN_PWD);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private static SOAPSession getJiraWebService(){
        if(jiraWebService == null){
            System.out.println("Connection");
            new Jira();
        }
        return jiraWebService;
    }

    public static JiraEntry getJiraById(String id){
        JiraEntry jiraEntry = new JiraEntry();
        jiraEntry.setId(id);
        try {
            RemoteIssue remoteIssue = getJiraWebService().getJiraSoapService().getIssue(getJiraWebService().getAuthenticationToken(), id.trim());
            RemoteVersion[] version = remoteIssue.getFixVersions();
            String fixVersions = "";
            for(RemoteVersion v : version){
                fixVersions += v.getName()+" ";
            }

            jiraEntry.setStatus(JiraStatus.values()[Integer.valueOf(remoteIssue.getStatus())]);
            jiraEntry.setFixVersion(fixVersions);
            jiraEntry.setAssignee(remoteIssue.getAssignee());

        } catch (RemoteAuthenticationException m){
            new Jira(); //Probably disconnected, try to reconnect
            System.out.println("Probably disconnected. Try to reconnect.");
            return getJiraById(id);
        } catch (RemotePermissionException r){
            jiraEntry.setValid(false); //No permission or no valid
        } catch (RemoteException e) {
            jiraEntry.setValid(false); //Something else
        }

        return jiraEntry;
    }

    public static List<JiraEntry> getJiraByIds(List<String> ids){
        List<JiraEntry> results = new LinkedList<JiraEntry>();

        for(String id : ids){
            results.add(getJiraById(id));
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



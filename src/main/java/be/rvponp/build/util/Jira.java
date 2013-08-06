package be.rvponp.build.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.atlassian.jira.rpc.exception.RemotePermissionException;
import com.atlassian.jira.rpc.soap.beans.RemoteIssue;
import com.atlassian.jira.rpc.soap.beans.RemoteStatus;
import com.atlassian.jira.rpc.soap.beans.RemoteVersion;

import jira.rpc.soap.jirasoapservice_v2.JiraSoapService;


public class Jira {
	
	private static final String SOAP_URL = "http://jira:8080/rpc/soap/jirasoapservice-v2";
    //private static final String SOAP_URL = "http://lts-jira01:8080/rpc/soap/jirasoapservice-v2";
	private static final String LOGIN_NAME = "vermb";
	private static final String LOGIN_PWD = "vermb";
	//private static final int MAX_RESULT_LIMIT = 200;
	//private static final String PROJECT = "PRODUCTION";
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
        try {
            JiraEntry jiraEntry = new JiraEntry();

            RemoteIssue remoteIssue = getJiraWebService().getJiraSoapService().getIssue(getJiraWebService().getAuthenticationToken(), id.trim());
            RemoteVersion[] version = remoteIssue.getFixVersions();
            String fixVersions = "";
            for(RemoteVersion v : version){
                fixVersions += v.getName()+" ";
            }
            String status = JiraStatus.values()[Integer.valueOf(remoteIssue.getStatus())].toString();




            jiraEntry.setId(id);
            jiraEntry.setStatus(status);
            jiraEntry.setFixVersion(fixVersions);

            return jiraEntry;
        } catch (RemotePermissionException r){
            //Nothing to do, jira not found
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return null;
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
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return returnList;
//
//	}

    public static void main(String[] args) throws MalformedURLException, RemoteException {

        System.out.println(getJiraById("TELEOPE-144").getStatus());

        String str = "PRODUCTION-5456 : Pegaos Interface : 3rd party recuperation should be ventilated on standatrd rights only. PRODUCTION-5452 Taking standard rights only for third party ventilation.";


         Pattern pattern = Pattern.compile("([A-Z]+-[0-9]+)");
         Matcher matcher = pattern.matcher(str);


         System.out.println(str.replaceAll("([A-Z]+-[0-9]+)","{TOKEN}"));


    }

	
}



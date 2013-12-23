package be.rvponp.build.util;

import be.rvponp.build.CommitViewerConfiguration;
import be.rvponp.build.model.JiraEntry;
import be.rvponp.build.model.JiraStatus;
import com.atlassian.jira.rpc.exception.RemoteAuthenticationException;
import com.atlassian.jira.rpc.exception.RemotePermissionException;
import com.atlassian.jira.rpc.soap.beans.RemoteComponent;
import com.atlassian.jira.rpc.soap.beans.RemoteCustomFieldValue;
import com.atlassian.jira.rpc.soap.beans.RemoteIssue;
import com.atlassian.jira.rpc.soap.beans.RemoteVersion;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Jira {

	private static final String KEY_SOAP_URL = "jira.url";
	private static final String KEY_USER = "jira.user";
	private static final String KEY_PASS = "jira.pass";
	private static final CommitViewerConfiguration config = CommitViewerConfiguration.getInstance();
	private static final String FIELD_RESOLVED_ON = "customfield_10200";
	private static SOAPSession jiraWebService = null;
	private static final Logger log = Logger.getLogger(Jira.class);
	private static Jira instance;

	private Jira() {
		connect();
	}

	private void connect() {
		try {
			jiraWebService = new SOAPSession(new URL(config.getProperty(KEY_SOAP_URL)));
			jiraWebService.connect(config.getProperty(KEY_USER), config.getProperty(KEY_PASS));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public static SOAPSession getJiraWebService() {
		if (instance == null) {
			instance = new Jira();
		}
		return jiraWebService;
	}

	public static JiraEntry getJiraById(String id, Boolean parsingJira) {
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
				jiraEntry.setComponent(remoteIssue.getComponents());
			} catch (RemoteAuthenticationException m) {
				log.warn("Probably disconnected. Try to reconnect.");
				connectionTry++;
				if (connectionTry < 3) {
					//Probably disconnected, try to reconnect
					instance.connect();
					return getJiraById(id, parsingJira);
				} else {
					log.error("Impossible to reconnect, set unknown status to jiraEntry");
					setUnknownStatus(jiraEntry);
				}
			} catch (RemotePermissionException r) {
				//No permission or no valid
				jiraEntry.setValid(false);
			} catch (RemoteException e) {
				//Something else
				jiraEntry.setValid(false);
			}
		} else {
			setUnknownStatus(jiraEntry);
		}

		return jiraEntry;
	}

	private static String getCustomFieldValue(RemoteCustomFieldValue field) {
		StringBuilder builder = new StringBuilder("[");
		int idx = 0;
		for (String value : field.getValues()) {
			builder.append(value);
			if (idx++ < field.getValues().length - 1) {
				builder.append(",");
			}
		}
		builder.append("]");
		return builder.toString();
	}

	private static void setUnknownStatus(JiraEntry jiraEntry) {
		jiraEntry.setStatus(JiraStatus.Undefined_A);
		jiraEntry.setFixVersion("Unknown");
		jiraEntry.setAssignee("Unknown");
		jiraEntry.setValid(true);
		jiraEntry.setComponent(new RemoteComponent[0]);
	}

	public static List<JiraEntry> getJiraByIds(List<String> ids, Boolean parsingJira) {
		List<JiraEntry> results = new ArrayList<JiraEntry>();

		for (String id : ids) {
			results.add(getJiraById(id, parsingJira));
		}

		return results;
	}
}



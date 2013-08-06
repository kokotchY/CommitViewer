package be.rvponp.build.components;

import be.rvponp.build.util.Jira;
import be.rvponp.build.util.JiraEntry;
import be.rvponp.build.util.JiraLinkCommitParser;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNRevisionProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: canas
 * Date: 7/10/13
 * Time: 10:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class CompareButton extends Button implements Button.ClickListener {

    private final ComboBox fromVersion;
    private final ComboBox toVersion;
    private final Table table;
    private final VerticalLayout files;

    public CompareButton(ComboBox fromVersion, ComboBox toVersion, Table table, VerticalLayout files) {
        super("Compare");
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
        this.table = table;
        this.files = files;
        addClickListener(this);
    }


    @Override
    public void buttonClick(ClickEvent clickEvent) {
        DAVRepositoryFactory.setup();
        SVNRepository repository;
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded("http://lpr-therepo/svn/theseos/theseos/branches/theseos-13.03/"));
        } catch (SVNException e) {
            e.printStackTrace();
            Notification.show("Impossible to access the repository", Notification.Type.ERROR_MESSAGE);
            return;
        }
        Notification.show("Start comparison between " + fromVersion.getValue() + " and " + toVersion.getValue());
        table.removeAllItems();
        long startRevision = getRevision(repository, fromVersion.getValue());
        System.out.println("Start revision: "+startRevision);
        long endRevision;
        if (toVersion.getValue() == null) {
            endRevision = -1;
        } else {
            endRevision = getRevision(repository, toVersion.getValue());
        }
        System.out.println("End revision: "+endRevision);

        if (startRevision != 0L && endRevision != 0L) {
            try {
                Collection log = repository.log(new String[]{""}, null, startRevision, endRevision, true, true);
                int index = 0;
                for (Object o : log) {
                    SVNLogEntry entry = (SVNLogEntry) o;
                    int nbFiles = entry.getChangedPaths().size();
                    //message.setContentMode(ContentMode.HTML);
                    long revision = entry.getRevision();
                    RevisionButton buttonRevision = new RevisionButton(revision, entry.getChangedPaths(), files);
                    buttonRevision.setStyleName(BaseTheme.BUTTON_LINK);
                    table.addItem(new Object[]{buttonRevision, entry.getDate(), generateJiraButtonWithMessage(entry.getMessage()), entry.getAuthor(), nbFiles}, index++);
                    //table.addGeneratedColumn("Jiras",new MessageColumnGenerator(JiraLinkCommitParser.parseJiraIdentifier(entry.getMessage())));
                }
            } catch (SVNException e) {
                e.printStackTrace();
            }
        }
    }

    private long getRevision(SVNRepository repository, Object value) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet("http://thedev01:8080/hudson/job/Local%20-%20Production/ws/" + value + "/informations");
        HttpEntity entity = null;
        try {
            HttpResponse response = httpClient.execute(get);
            if (response != null) {
                entity = response.getEntity();
                Properties properties = new Properties();
                properties.load(entity.getContent());
                SimpleDateFormat startBuildFormat = new SimpleDateFormat("yyyyMMdd.HHmm");
                Object o = properties.get("startbuild");
                if (o != null) {
                    Date date = startBuildFormat.parse(o.toString());
                    long revision = repository.getDatedRevision(date);
                    SVNProperties revisionProperties = repository.getRevisionProperties(revision, null);
                    String dateRevision = revisionProperties.getStringValue(SVNRevisionProperty.DATE);
                    SimpleDateFormat revisionFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
                    DateTime dateTime1 = new DateTime(revisionFormat.parse(dateRevision)).withFieldAdded(DurationFieldType.hours(), 2);
                    if (dateTime1.isBefore(new DateTime(date))) {
                        System.out.println(dateTime1+" is before "+date);
                        return revision+1;
                    } else {
                        return revision;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SVNException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0L;
    }

    private Component generateJiraButton(List<String> jiraIds) {

        HorizontalLayout returnComponent = new HorizontalLayout();
        if( jiraIds != null)
        {
            for(String s : jiraIds){
                returnComponent.addComponent(new Button(s));
            }
        }

        return returnComponent;
    }

    private Component generateJiraButtonWithMessage(String message){
        String PATTERN_JIRA = "([A-Z]+-[0-9]+)";
        String messageWithoutJiraIds = message.replaceAll(PATTERN_JIRA, "§");
        StringTokenizer tokens = new StringTokenizer(messageWithoutJiraIds, "§", true);

        List<JiraEntry> jiraIds = JiraLinkCommitParser.parseJiraIdentifier(message);
        HorizontalLayout returnComponent = new HorizontalLayout();
        int i = 0;
        if(!jiraIds.isEmpty())
        {
            while(tokens.hasMoreTokens()){
                String tok = tokens.nextToken();
                if(tok.equals("§")){
                    Button b = new Button(jiraIds.get(i++).toString());
                    b.setIcon(new ThemeResource("http://jira:8080/images/icons/status_open.gif"));
                    returnComponent.addComponent(b);
                }else{
                    returnComponent.addComponent(new Label(tok));
                }
            }
        }else{
            returnComponent.addComponent(new Label(message));
        }

        return returnComponent;
    }

}

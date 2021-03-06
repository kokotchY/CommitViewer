package be.rvponp.build.components;

import be.rvponp.build.model.JiraComponent;
import be.rvponp.build.model.JiraProject;
import be.rvponp.build.util.ADUserResolver;
import be.rvponp.build.model.JiraEntry;
import be.rvponp.build.util.JiraLinkCommitParser;
import be.rvponp.build.util.Util;
import com.atlassian.jira.rpc.soap.beans.RemoteComponent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
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
    private final CheckBox jiraParsing;
    private static final Logger log = Logger.getLogger(CompareButton.class);
    private final Tree tree;
    private final VerticalLayout filesLayout;

    public CompareButton(ComboBox fromVersion, ComboBox toVersion, Table table, VerticalLayout files, CheckBox jiraParsing, Tree tree, VerticalLayout filesLayout) {
        super("Compare");
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
        this.table = table;
        this.files = files;
        addClickListener(this);
        this.setIcon(new ThemeResource("img/view.png"));
        this.jiraParsing = jiraParsing;
        this.tree = tree;
        this.filesLayout = filesLayout;
    }


    @Override
    public void buttonClick(ClickEvent clickEvent) {
        DAVRepositoryFactory.setup();
        SVNRepository repository;
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded
                    ("http://lpr-therepo/svn/theseos/theseos/branches/theseos-13.11/"));
        } catch (SVNException e) {
            e.printStackTrace();
            Notification.show("Impossible to access the repository", Notification.Type.ERROR_MESSAGE);
            return;
        }
        Notification.show("Start comparison between " + fromVersion.getValue() + " and " + toVersion.getValue());
        table.removeAllItems();
        files.removeAllComponents();
        long startRevision = getRevision(repository, fromVersion.getValue());
        log.debug("Start revision: " + startRevision);
        long endRevision;
        if (toVersion.getValue() == null) {
            endRevision = -1;
        } else {
            endRevision = getRevision(repository, toVersion.getValue());
        }
        log.debug("End revision: " + endRevision);

        if (startRevision != 0L && endRevision != 0L) {
            try {
                Collection log = repository.log(new String[]{""}, null, startRevision, endRevision, true, true);
                int index = 0;
                List<JiraProject> projects = getProjects(tree);
                List<JiraComponent> components = getCleanedComponents(projects, getComponents(tree));
                for (Object o : log) {
                    SVNLogEntry entry = (SVNLogEntry) o;
                    int nbFiles = getNumberFiles(entry);
                    //message.setContentMode(ContentMode.HTML);
                    long revision = entry.getRevision();
                    RevisionButton buttonRevision = new RevisionButton(revision, entry.getChangedPaths(), files,
                            filesLayout);
                    buttonRevision.setStyleName(BaseTheme.BUTTON_LINK);
                    List<JiraEntry> listJira = JiraLinkCommitParser.parseJiraIdentifier(entry.getMessage(), jiraParsing.getValue());
                    StringBuilder resolvedOn = new StringBuilder();
                    for (JiraEntry jiraEntry : listJira) {
                        resolvedOn.append(jiraEntry.getResolvedOn());
                    }
                    boolean displayCommit = false;

                    // Nothing selected
                    if (projects.size() == 0 && components.size() == 0) {
                        displayCommit = true;
                    } else {
                        for (JiraEntry jiraEntry : listJira) {
                            for (JiraProject project : projects) {
                                if (jiraEntry.getId().startsWith(project.getName())) {
                                    displayCommit = true;
                                }
                                if (displayCommit) {
                                    break;
                                }
                            }
                            if (!displayCommit) {
                                for (JiraComponent component : components) {
                                    for (RemoteComponent remoteComponent : jiraEntry.getComponent()) {
                                        if (remoteComponent.getName().equals(component.getName())) {
                                            displayCommit = true;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (displayCommit) {
                        table.addItem(new Object[]{
                                buttonRevision,
                                entry.getDate(),
                                new MessageLayout(entry.getMessage(), jiraParsing.getValue()),
                                new Label(resolvedOn.toString()),
                                new JiraAssigneesLayout(listJira),
                                new Label(ADUserResolver.getFullUsernameByID(entry.getAuthor())),
                                nbFiles},
                                index++);
                    }
                    //table.addGeneratedColumn("Jiras",new MessageColumnGenerator(JiraLinkCommitParser.parseJiraIdentifier(entry.getMessage())));
                }
                String stringFilter = getStringFilter(projects, components);
                if (!stringFilter.isEmpty()) {
                    table.setCaption("Commits (" + table.getItemIds().size() + "," + stringFilter + ")");
                } else {
                    table.setCaption("Commits (" + table.getItemIds().size() + ")");
                }
            } catch (SVNException e) {
                e.printStackTrace();
            }
        }
    }

    private String getStringFilter(List<JiraProject> projects, List<JiraComponent> components) {
        StringBuilder builderProject = new StringBuilder();
        if (projects.size() > 0) {
            builderProject.append("projects=[");
            int idx = 0;
            for (JiraProject project : projects) {
                builderProject.append(project.getName());
                if (idx++ < projects.size() - 1) {
                    builderProject.append(",");
                }
            }
            builderProject.append("]");
        }

        StringBuilder builderComponents = new StringBuilder();
        boolean hasComponents = false;
        if (components.size() > 0) {
            hasComponents = true;
            builderComponents.append("components=[");
            int idx = 0;
            for (JiraComponent component : components) {
                builderComponents.append(component.getName()).append("@").append(component.getJiraProject().getName());
                if (idx++ < components.size() - 1) {
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

    private List<JiraComponent> getCleanedComponents(List<JiraProject> projects, List<JiraComponent> components) {
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
        return cleanedComponents;
    }

    private List<JiraComponent> getComponents(Tree tree) {
        List<JiraComponent> result = new ArrayList<JiraComponent>();
        Set set = (Set) tree.getValue();
        for (Object o : set) {
            if (o instanceof JiraComponent) {
                result.add((JiraComponent) o);
            }
        }
        return result;
    }

    private List<JiraProject> getProjects(Tree tree) {
        List<JiraProject> result = new ArrayList<JiraProject>();
        Set set = (Set) tree.getValue();
        for (Object o : set) {
            if (o instanceof JiraProject) {
                result.add((JiraProject) o);
            }
        }
        return result;
    }

    private int getNumberFiles(SVNLogEntry entry) {
        int nb = 0;
        for (Object o : entry.getChangedPaths().values()) {
            if (Util.isFile(o)) {
                nb++;
            }
        }
        return nb;
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
                        log.debug(dateTime1 + " is before " + date);
                        return revision + 1;
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
}

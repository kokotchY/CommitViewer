/*
 * Copyright 2009 IT Mill Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package be.rvponp.build;

import be.rvponp.build.components.CompareButton;
import be.rvponp.build.components.ExportXLSButton;
import be.rvponp.build.components.MessageLayout;
import be.rvponp.build.components.RefreshButton;
import be.rvponp.build.model.JiraComponent;
import be.rvponp.build.model.JiraProject;
import be.rvponp.build.util.Jira;
import be.rvponp.build.util.SOAPSession;
import com.atlassian.jira.rpc.soap.beans.RemoteComponent;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
@Theme("mytheme")
@Title("Commit Viewer")
public class CommitViewerUI extends UI {

    private ComboBox fromVersion;
    private ComboBox toVersion;
    private Table table;
    private VerticalLayout files;
    private static final Logger log = Logger.getLogger(CommitViewerUI.class);

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        VerticalLayout layout = new VerticalLayout();

        VerticalLayout infoLayout = new VerticalLayout();
        layout.setSizeFull();
        HorizontalLayout buildDateLayout = createBuildDateLayout();
        infoLayout.addComponent(buildDateLayout);

        table = createCommitsTable();
        files = new VerticalLayout();
        Label filesLabel = new Label("Files");
        VerticalLayout filesLayout = new VerticalLayout();

        HorizontalLayout filtersLayout = createFiltersLayout(table, files, filesLayout);

        VerticalLayout tableLayout = new VerticalLayout();
        tableLayout.addComponent(table);
        tableLayout.setSizeFull();

        filesLayout.addComponent(filesLabel);
        filesLayout.addComponent(files);
        filesLayout.setVisible(false);
        filesLayout.setSizeFull();
        infoLayout.addComponent(new Panel(filtersLayout));
        infoLayout.setSizeUndefined();

        layout.addComponent(infoLayout);
        layout.addComponent(tableLayout);
        layout.setExpandRatio(tableLayout, 1);
        layout.addComponent(filesLayout);
        layout.setExpandRatio(filesLayout, 0);

        layout.addComponent(new ExportXLSButton("Export XLS", table, fromVersion, toVersion));
        layout.setMargin(true);
        setContent(layout);

    }

    private HorizontalLayout createBuildDateLayout() {
        HorizontalLayout buildDateLayout = new HorizontalLayout();
        Label labelBuildDate = new Label("Build date: " + getBuildDate());
        buildDateLayout.addComponent(labelBuildDate);
        labelBuildDate.setSizeUndefined();
        buildDateLayout.setSizeUndefined();
        return buildDateLayout;
    }

    private HorizontalLayout createFiltersLayout(Table table, VerticalLayout files, VerticalLayout filesLayout) {
        HorizontalLayout filtersLayout = new HorizontalLayout();

        FormLayout formReleaseLayout = new FormLayout();
        fromVersion = new ComboBox("From release");
        toVersion = new ComboBox("To release");
        filtersLayout.addComponent(formReleaseLayout);
        formReleaseLayout.setDescription("formReleaseLayout");

        formReleaseLayout.addComponent(fromVersion);
        formReleaseLayout.addComponent(toVersion);
        formReleaseLayout.setSizeUndefined();

//        FormLayout jiraTreeLayout = new FormLayout();
        final Tree tree = createTreeComponent();
//        jiraTreeLayout.addComponent(tree);
        filtersLayout.addComponent(tree);


        CheckBox jiraParsing = new CheckBox("Jira Parsing");
        jiraParsing.setValue(true);
//        filtersLayout.addComponent(jiraParsing);

        FormLayout buttonsLayout = new FormLayout();
        CompareButton compareButton = new CompareButton(fromVersion, toVersion, table, files,
                jiraParsing, tree, filesLayout);
        RefreshButton refreshButton = new RefreshButton(this, fromVersion, toVersion);
//        refreshButton.buttonClick(null);
//        compareButton.buttonClick(null);
        buttonsLayout.addComponent(refreshButton);
        buttonsLayout.addComponent(compareButton);
        buttonsLayout.setSizeUndefined();
        filtersLayout.addComponent(buttonsLayout);
        filtersLayout.setSizeUndefined();
        return filtersLayout;
    }

    private Table createCommitsTable() {
        Table table = new Table("Commits");
        table.addContainerProperty("Revision", Button.class, 0L);
        table.addContainerProperty("Date", Date.class, new Date());
        table.addContainerProperty("Message", MessageLayout.class, "Message");
        table.addContainerProperty("Resolved On", Label.class, "");
        table.addContainerProperty("Jira Assignee(s)", HorizontalLayout.class, "JiraAssignees");
        table.addContainerProperty("Committer", Label.class, "Committer");
        table.addContainerProperty("# Files", Integer.class, 0);
        //Object[] columns = new Object[]{"Revision", "Date","Jiras","Message", "Author","# Files"};
        //table.setVisibleColumns(columns);
        table.setSizeFull();
        return table;
    }

    private Tree createTreeComponent() {
        Tree tree = new Tree("Projects and components");
        tree.setMultiSelect(true);
        List<JiraProject> projects = new ArrayList<JiraProject>();
        projects.add(new JiraProject("PRODUCTION"));
        projects.add(new JiraProject("VAAD"));
        projects.add(new JiraProject("ID"));
        projects.add(new JiraProject("PAYTWO"));
        try {
            SOAPSession jiraWebService = Jira.getJiraWebService();

            for (JiraProject project : projects) {
                tree.addItem(project);
                RemoteComponent[] components = jiraWebService.getJiraSoapService().getComponents(jiraWebService
                        .getAuthenticationToken(), project.getName());
                if (components.length == 0) {
                    tree.setChildrenAllowed(project, false);
                } else {
                    for (RemoteComponent component : components) {
                        JiraComponent jiraComponent = new JiraComponent(project, component.getName());
                        project.addComponent(jiraComponent);
                        tree.addItem(jiraComponent);
                        tree.setParent(jiraComponent, project);
                        tree.setChildrenAllowed(jiraComponent, false);
                    }
                }
            }
        } catch (RemoteException e) {
            log.error("Error when retrieving the components", e);
            tree.removeAllItems();
            for (JiraProject project : projects) {
                tree.addItem(project);
                tree.setChildrenAllowed(project, false);
            }
        }
        return tree;
    }

    private String getBuildDate() {
        InputStream inputStream = CommitViewerUI.class.getResourceAsStream("buildDate.txt");
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            try {
                String s = reader.readLine();
                if (s != null) {
                    return s;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "Unknown";
    }

    public void cleanComponents() {
        fromVersion.removeAllItems();
        toVersion.removeAllItems();
        files.removeAllComponents();
        table.removeAllItems();
    }
}

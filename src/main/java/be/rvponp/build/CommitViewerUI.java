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
import be.rvponp.build.components.MessageLayout;
import be.rvponp.build.components.RefreshButton;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
@Theme("mytheme")
@Title("Commit Viewer")
public class CommitViewerUI extends UI
{

    private ComboBox fromVersion;
    private ComboBox toVersion;
    private ListSelect filterJira;
    private Table table;
    private VerticalLayout files;

    @Override
    protected void init(VaadinRequest vaadinRequest)
    {
        VerticalLayout layout = new VerticalLayout();
        Label labelBuildDate = new Label("Build date: " + getBuildDate());
        layout.addComponent(labelBuildDate);
        FormLayout formLayout = new FormLayout();
        fromVersion = new ComboBox("From release");
        toVersion = new ComboBox("To release");
        filterJira = new ListSelect("Jira filter");
        filterJira.setMultiSelect(true);
        filterJira.addItem("No filter");
        filterJira.addItem("PRODUCTION");
        filterJira.addItem("ID");
        filterJira.addItem("PAYTWO");
        filterJira.addItem("ATTRIB");
        filterJira.setSizeUndefined();

        formLayout.addComponent(fromVersion);
        formLayout.addComponent(toVersion);
//        formLayout.addComponent(filterJira);
        table = new Table("Commits");
        table.addContainerProperty("Revision", Button.class, 0L);
        table.addContainerProperty("Date", Date.class, new Date());
        table.addContainerProperty("Message", MessageLayout.class, "Message");
        table.addContainerProperty("Jira Assignee(s)", HorizontalLayout.class, "JiraAssignees");
        table.addContainerProperty("Committer", Label.class, "Committer");
        table.addContainerProperty("# Files", Integer.class, 0);
        //Object[] columns = new Object[]{"Revision", "Date","Jiras","Message", "Author","# Files"};
        //table.setVisibleColumns(columns);

        table.setSizeFull();
        files = new VerticalLayout();
        CheckBox jiraParsing = new CheckBox("Jira Parsing");
        formLayout.addComponent(new CompareButton(fromVersion, toVersion, table, files, filterJira, jiraParsing));
        RefreshButton refreshButton = new RefreshButton(this, fromVersion, toVersion);
        refreshButton.buttonClick(null);
        formLayout.addComponent(refreshButton);
        formLayout.addComponent(jiraParsing);
        layout.addComponent(formLayout);
        layout.addComponent(table);
        layout.addComponent(new Label("Files"));
        layout.addComponent(files);

        Button exportToXLS = new Button("Export XLS");
        exportToXLS.setIcon(new ThemeResource("img/table.png"));
        layout.addComponent(exportToXLS);

        setContent(layout);

        exportToXLS.addClickListener(new Button.ClickListener() {
            private ExcelExport excelExport;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                excelExport = new ExcelExport(table);
                excelExport.excludeCollapsedColumns();
                excelExport.setReportTitle("CommitViewer Report - from " + fromVersion.getValue() + " to " + toVersion.getValue());
                excelExport.export();

            }
        });        
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

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
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;

import java.util.Date;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
@Theme("mytheme")
public class CommitViewerUI extends UI
{

    private ComboBox fromVersion;
    private ComboBox toVersion;
    private Table table;
    private VerticalLayout files;

    @Override
    protected void init(VaadinRequest vaadinRequest)
    {


        VerticalLayout layout = new VerticalLayout();
        FormLayout formLayout = new FormLayout();
        fromVersion = new ComboBox("From release");
        toVersion = new ComboBox("To release");

        formLayout.addComponent(fromVersion);
        formLayout.addComponent(toVersion);
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
        formLayout.addComponent(new CompareButton(fromVersion, toVersion, table, files));
        RefreshButton refreshButton = new RefreshButton(this, fromVersion, toVersion);
        refreshButton.buttonClick(null);
        formLayout.addComponent(refreshButton);
        layout.addComponent(formLayout);
        layout.addComponent(table);
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

    public void cleanComponents() {
        fromVersion.removeAllItems();
        toVersion.removeAllItems();
        files.removeAllComponents();
        table.removeAllItems();
    }
}

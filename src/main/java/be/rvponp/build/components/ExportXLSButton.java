package be.rvponp.build.components;

import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Table;

/**
 * Created with IntelliJ IDEA.
 * User: canas
 * Date: 12/18/13
 * Time: 11:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class ExportXLSButton extends Button implements Button.ClickListener {

    private final Table table;
    private final ComboBox toVersion;
    private final ComboBox fromVersion;

    public ExportXLSButton(String caption, Table table, ComboBox fromVersion, ComboBox toVersion) {
        super(caption);
        setIcon(new ThemeResource("img/table.png"));
        addClickListener(this);
        this.table = table;
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        ExcelExport excelExport = new ExcelExport(table);
        excelExport.excludeCollapsedColumns();
        excelExport.setReportTitle("CommitViewer Report - from " + fromVersion.getValue() + " to " + toVersion.getValue());
        excelExport.export();
    }
}

package be.rvponp.build.components;

import be.rvponp.build.CommitViewerUI;
import be.rvponp.build.model.Release;
import be.rvponp.build.util.ReleaseUtil;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

import java.util.List;

/**
 * User: canas
 * Date: 7/11/13
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class RefreshButton extends Button implements Button.ClickListener {

    private final ComboBox fromVersion;
    private final ComboBox toVersion;
    private final CommitViewerUI ui;

    public RefreshButton(CommitViewerUI ui, ComboBox fromVersion, ComboBox toVersion) {
        super("Refresh");
        addClickListener(this);
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
        this.ui = ui;
        this.setIcon(new ThemeResource("img/refresh.png"));
    }

    @Override
    public void buttonClick(ClickEvent clickEvent) {
        List<Release> releases = ReleaseUtil.getValidRelease();
        ui.cleanComponents();
        for (Release release : releases) {
            fromVersion.addItem(release);
            toVersion.addItem(release);
        }
        fromVersion.setValue(releases.get(releases.size() - 2));
        toVersion.setValue(releases.get(releases.size() - 1));
    }
}

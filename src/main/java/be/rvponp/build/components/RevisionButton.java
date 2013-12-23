package be.rvponp.build.components;

import be.rvponp.build.util.Util;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Map;

/**
 * User: canas
 * Date: 7/10/13
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class RevisionButton extends Button implements Button.ClickListener {

    private final VerticalLayout files;
    private final Map changedPaths;
    private final long revision;
    private static final String VIEWVC_URL = "http://lpr-therepo/viewvc/theseos";
    private static final Logger log = Logger.getLogger(RevisionButton.class);
    private final VerticalLayout filesLayout;

    public RevisionButton(long revision, Map changedPaths, VerticalLayout files, VerticalLayout filesLayout) {
        super(""+revision);
        addClickListener(this);
        this.changedPaths = changedPaths;
        this.files = files;
        this.revision = revision;
        this.filesLayout = filesLayout;
    }

    @Override
    public void buttonClick(ClickEvent clickEvent) {
        files.removeAllComponents();
        filesLayout.setVisible(true);
        filesLayout.setImmediate(true);
        for (Object o : changedPaths.entrySet()) {
            Map.Entry entry = (Map.Entry)o;
            if (Util.isFile(entry.getKey())) {
                HorizontalLayout layout = new HorizontalLayout();
                String key = entry.getKey().toString();
                char type = entry.getValue().toString().charAt(0);
                switch (type) {
                    case 'M':
                        layout.addComponent(createImageFromName("edit"));
                        break;
                    case 'A':
                        layout.addComponent(createImageFromName("add"));
                        break;
                    case 'D':
                        layout.addComponent(createImageFromName("delete"));
                        break;
                    default:
                        layout.addComponent(createImageFromName("edit"));
                }
                layout.addComponent(new Label(""+type));
                layout.addComponent(new Link(key, createLinkViewVc(entry, revision)));
                files.addComponent(layout);
            }
        }
    }

    private Image createImageFromName(String imageName) {
        String path = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        return new Image(null, new FileResource(new File(path + "/WEB-INF/images/" + imageName + ".png")));
    }

    private Resource createLinkViewVc(Map.Entry entry, long revision) {
        String link = VIEWVC_URL + entry.getKey();
        char value = entry.getValue().toString().charAt(0);
        if (value == 'A') {
            link += "?revision="+revision+"&view=markup";
        } else if (value == 'M') {
            link += getParam(revision);
        } else {
            log.warn("Unknown value: " + value);
        }
        return new ExternalResource(link);
    }

    private static String getParam(long revision) {
        return "?r1=" + (revision - 1) + "&r2=" + revision;
    }

    public String toString(){
        return Long.toString(revision);
    }
}

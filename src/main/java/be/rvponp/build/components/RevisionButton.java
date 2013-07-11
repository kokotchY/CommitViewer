package be.rvponp.build.components;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
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

    public RevisionButton(long revision, Map changedPaths, VerticalLayout files) {
        super(""+revision);
        addClickListener(this);
        this.changedPaths = changedPaths;
        this.files = files;
        this.revision = revision;
    }

    @Override
    public void buttonClick(ClickEvent clickEvent) {
        files.removeAllComponents();
        for (Object o : changedPaths.entrySet()) {
            Map.Entry entry = (Map.Entry)o;
            files.addComponent(new Link(entry.getValue()+" "+entry.getKey(), createLinkViewVc(entry, revision)));
        }
    }

    private Resource createLinkViewVc(Map.Entry entry, long revision) {
        String link = VIEWVC_URL + entry.getKey();
        char value = entry.getValue().toString().charAt(0);
        if (value == 'A') {
            link += "?revision="+revision+"&view=markup";
        } else if (value == 'M') {
            link += getParam(revision);
        } else {
            System.out.println("Unknown value: "+value);
        }
        return new ExternalResource(link);
    }

    private static String getParam(long revision) {
        return "?r1=" + (revision - 1) + "&r2=" + revision;
    }
}

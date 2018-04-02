package com.sirolf2009.caesar.component;

import com.sirolf2009.caesar.model.IDashboardNode;
import javafx.scene.Node;
import org.dockfx.DockEvent;
import org.dockfx.DockNode;
import org.dockfx.DockPane;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class DashboardDockNode extends DockNode {

    static Method dockImpl;
    static {
        try {
            dockImpl = DockNode.class.getDeclaredMethod("dockImpl", DockPane.class);
            dockImpl.setAccessible(true); //Fuck outta here
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private final IDashboardNode data;

    public DashboardDockNode(IDashboardNode data, Node contents, String title, Node graphic) {
        super(contents, title, graphic);
        this.data = data;
        getStylesheets().add("/styles/styles.css");
    }

    public DashboardDockNode(IDashboardNode data, Node contents, String title) {
        super(contents, title);
        this.data = data;
        getStylesheets().add("/styles/styles.css");
    }

    public DashboardDockNode(IDashboardNode data, Node contents) {
        super(contents);
        this.data = data;
        getStylesheets().add("/styles/styles.css");
    }

    public void setDashboard(DashboardTab dashboard) {
        try {
            dockImpl.invoke(this, (DockPane)dashboard);
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        } catch(InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public IDashboardNode getData() {
        return data;
    }
}

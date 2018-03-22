package com.sirolf2009.caesar.component;

import com.sirolf2009.caesar.component.hierarchy.IHierarchicalData;
import com.sirolf2009.caesar.model.IDashboardNode;
import javafx.scene.Node;
import org.dockfx.DockNode;

public class DashboardDockNode extends DockNode {

    private final IDashboardNode data;

    public DashboardDockNode(IDashboardNode data, Node contents, String title, Node graphic) {
        super(contents, title, graphic);
        this.data = data;
    }

    public DashboardDockNode(IDashboardNode data, Node contents, String title) {
        super(contents, title);
        this.data = data;
    }

    public DashboardDockNode(IDashboardNode data, Node contents) {
        super(contents);
        this.data = data;
    }

    public IDashboardNode getData() {
        return data;
    }
}

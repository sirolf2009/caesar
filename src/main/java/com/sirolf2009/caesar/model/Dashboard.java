package com.sirolf2009.caesar.model;

import com.sirolf2009.caesar.model.dashboard.SplitNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

public class Dashboard {

    private final StringProperty name;
    private final ObjectProperty<SplitNode> root;

    public Dashboard(StringProperty name, ObjectProperty<SplitNode> root) {
        this.name = name;
        this.root = root;
    }

    public String getName() {
        return name.get();
    }

    public SplitNode getRoot() {
        return root.get();
    }

    public void setRoot(SplitNode node) {
        root.setValue(node);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<SplitNode> rootProperty() {
        return root;
    }
}

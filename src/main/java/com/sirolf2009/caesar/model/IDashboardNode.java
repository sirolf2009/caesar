package com.sirolf2009.caesar.model;

import javafx.beans.property.StringProperty;
import javafx.scene.Node;

public interface IDashboardNode {

    StringProperty nameProperty();
    Node createNode();

    default String getName() {
        return nameProperty().get();
    }

}

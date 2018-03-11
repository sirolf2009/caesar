package com.sirolf2009.model;

import com.sirolf2009.component.hierarchy.IHierarchicalData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;

public class JMXAttributes extends HashMap<JMXAttribute, Object> implements IHierarchicalData {
    @Override
    public ObservableList getChildren() {
        return FXCollections.observableArrayList(entrySet());
    }
}

package com.sirolf2009.caesar.model;

import com.sirolf2009.caesar.component.hierarchy.IHierarchicalData;
import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;

public class JMXAttributes extends HashMap<IDataPointer, Object> implements IHierarchicalData {
    @Override
    public ObservableList getChildren() {
        return FXCollections.observableArrayList(entrySet());
    }
}

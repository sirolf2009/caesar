package com.sirolf2009.model;

import com.sirolf2009.JMXPuller;
import com.sirolf2009.component.TableTab;
import com.sirolf2009.component.hierarchy.IHierarchicalData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;

public class Table implements IHierarchicalData<JMXAttribute>, Serializable {

    private final SimpleStringProperty name;
    private final ObservableList<JMXAttribute> attributes;
    private final ObservableList<JMXAttributes> items;

    public Table(String name) {
        this.name = new SimpleStringProperty(name);
        this.attributes = FXCollections.observableArrayList();
        this.items = FXCollections.observableArrayList();
    }

    @Override
    public ObservableList<JMXAttribute> getChildren() {
        return attributes;
    }

    public ObservableList<JMXAttributes> getItems() {
        return items;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    @Override
    public String toString() {
        return name.get();
    }
}

package com.sirolf2009.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.component.hierarchy.IHierarchicalData;
import com.sirolf2009.model.serializer.TableSerializer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;

@DefaultSerializer(TableSerializer.class)
public class Table implements IHierarchicalData<JMXAttribute>, Serializable {

    private final SimpleStringProperty name;
    private final ObservableList<JMXAttribute> attributes;
    private final ObservableList<JMXAttributes> items;

    public Table(String name) {
        this(new SimpleStringProperty(name), FXCollections.observableArrayList(), FXCollections.observableArrayList());
    }

    public Table(SimpleStringProperty name, ObservableList<JMXAttribute> attributes, ObservableList<JMXAttributes> items) {
        this.name = name;
        this.attributes = attributes;
        this.items = items;
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

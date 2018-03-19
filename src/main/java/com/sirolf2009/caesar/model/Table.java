package com.sirolf2009.caesar.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.component.hierarchy.IHierarchicalData;
import com.sirolf2009.caesar.model.serializer.TableSerializer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(getName(), table.getName()) &&
                Objects.equals(attributes, table.attributes) &&
                Objects.equals(items, table.items);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, attributes, items);
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

package com.sirolf2009.caesar.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.component.hierarchy.IHierarchicalData;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.Serializable;
import java.util.Objects;

@DefaultSerializer(Table.TableSerializer.class)
public class Table implements IHierarchicalData<IDataPointer>, IDashboardNode {

    private final StringProperty name;
    private final LongProperty updateTimeout;
    private final ObservableList<IDataPointer> attributes;
    private final ObservableList<JMXAttributes> items;

    public Table(String name) {
        this(new SimpleStringProperty(name), new SimpleLongProperty(1000), FXCollections.observableArrayList(), FXCollections.observableArrayList());
    }

    public Table(StringProperty name, LongProperty updateTimeout, ObservableList<IDataPointer> attributes, ObservableList<JMXAttributes> items) {
        this.name = name;
        this.updateTimeout = updateTimeout;
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
                Objects.equals(updateTimeout, table.updateTimeout) &&
                Objects.equals(items, table.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, updateTimeout, attributes, items);
    }

    @Override
    public ObservableList<IDataPointer> getChildren() {
        return attributes;
    }

    public LongProperty updateTimeoutProperty() {
        return updateTimeout;
    }

    public long getUpdateTimeout() {
        return updateTimeout.get();
    }

    public ObservableList<JMXAttributes> getItems() {
        return items;
    }

    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public Node createNode() {
        TableView< JMXAttributes > tableView = new TableView<>();
        getChildren().forEach(pointer -> tableView.getColumns().add(new TableColumn<JMXAttributes, String>(){{
            textProperty().bind(pointer.nameProperty());
            setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOrDefault(pointer, "").toString()));
        }}));
        tableView.setItems(getItems());
        return tableView;
    }

    @Override
    public String toString() {
        return name.get();
    }

    public static class TableSerializer extends CaesarSerializer<Table> {

        @Override public void write(Kryo kryo, Output output, Table table) {
            output.writeString(table.getName());
            output.writeLong(table.getUpdateTimeout());
            writeObservableListWithClass(kryo, output, table.getChildren());
        }

        @Override public Table read(Kryo kryo, Input input, Class<Table> type) {
            SimpleStringProperty name = readStringProperty(input);
            SimpleLongProperty updateTimeout = readLongProperty(input);
            ObservableList<IDataPointer> attributes = readObservableListWithClass(kryo, input, IDataPointer.class);
            return new Table(name, updateTimeout, attributes, FXCollections.observableArrayList());
        }
    }

}

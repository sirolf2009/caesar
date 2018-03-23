package com.sirolf2009.caesar.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;

@DefaultSerializer(TableAndPointer.TableAndPointerSerializer.class)
public class TableAndPointer implements IDashboardNode {

    private final StringProperty name;
    private final Table table;
    private final IDataPointer pointer;

    public TableAndPointer(String name, Table table, IDataPointer pointer) {
        this(new SimpleStringProperty(name), table, pointer);
    }

    public TableAndPointer(StringProperty name, Table table, IDataPointer pointer) {
        this.name = name;
        this.table = table;
        this.pointer = pointer;
    }

    @Override public StringProperty nameProperty() {
        return name;
    }

    @Override public Node createNode() {
        Label label = new Label();
        table.getItems().addListener((InvalidationListener) e -> {
            label.setText(table.getItems().get(table.getItems().size() - 1).get(pointer) + "");
        });
        return label;
    }

    public static class TableAndPointerSerializer extends CaesarSerializer<TableAndPointer> {

        @Override public void write(Kryo kryo, Output output, TableAndPointer object) {
            output.writeString(object.name.get());
            kryo.writeObject(output, object.table);
            kryo.writeClassAndObject(output, object.pointer);
        }

        @Override public TableAndPointer read(Kryo kryo, Input input, Class<TableAndPointer> type) {
            StringProperty name = readStringProperty(input);
            Table table = kryo.readObject(input, Table.class);
            IDataPointer pointer = (IDataPointer) kryo.readClassAndObject(input);
            return new TableAndPointer(name, table, pointer);
        }
    }

}

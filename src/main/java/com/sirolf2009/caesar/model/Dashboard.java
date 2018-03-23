package com.sirolf2009.caesar.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.dashboard.SplitNode;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@DefaultSerializer(Dashboard.DashboardSerializer.class)
public class Dashboard {

    private final StringProperty name;
    private final ObjectProperty<SplitNode> root;

    public Dashboard(String name) {
        this(new SimpleStringProperty(name), new SimpleObjectProperty<>(null));
    }

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

    public static class DashboardSerializer extends CaesarSerializer<Dashboard> {

        @Override public void write(Kryo kryo, Output output, Dashboard object) {
            output.writeString(object.getName());
            kryo.writeObject(output, object.getRoot());
        }

        @Override public Dashboard read(Kryo kryo, Input input, Class<Dashboard> type) {
            return new Dashboard(readStringProperty(input), new SimpleObjectProperty<>(kryo.readObject(input, SplitNode.class)));
        }
    }

}

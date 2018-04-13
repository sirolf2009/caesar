package com.sirolf2009.caesar.model.dashboard;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.component.DashboardDockNode;
import com.sirolf2009.caesar.component.DashboardTab;
import com.sirolf2009.caesar.model.IDashboardNode;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.util.Pair;
import org.dockfx.DockPos;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@DefaultSerializer(SplitNode.SplitNodeSerializer.class)
public class SplitNode implements IDashboardNode {

    private final StringProperty name = new SimpleStringProperty("");
    private final Orientation orientation;
    private final List<IDashboardNode> children;
    private final List<Double> dividers;

    public SplitNode(Orientation orientation, List<IDashboardNode> children, List<Double> dividers) {
        this.orientation = orientation;
        this.children = children;
        this.dividers = dividers;
    }

    @Override
    public Node createNode() {
        SplitPane pane = new SplitPane();
        pane.setOrientation(orientation);
        pane.getStyleClass().add("dashboard-split-pane");
        children.forEach(child -> {
            if(child instanceof SplitNode) {
                pane.getItems().add(child.createNode());
            } else {
                DashboardDockNode node = new DashboardDockNode(child, child.createNode());
                node.titleProperty().bind(child.nameProperty());
                pane.getItems().add(node);
            }
        });
        for (int i = 0; i < dividers.size(); i++) {
            pane.setDividerPosition(i, dividers.get(i));
        }
        return pane;
    }

    @Override public String toString() {
        return "SplitNode{" + "orientation=" + orientation + ", children=" + children + ", dividers=" + dividers + '}';
    }

    @Override public StringProperty nameProperty() {
        return name;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public List<IDashboardNode> getChildren() {
        return children;
    }

    public List<Double> getDividers() {
        return dividers;
    }

    public static class SplitNodeSerializer extends CaesarSerializer<SplitNode> {

        @Override public void write(Kryo kryo, Output output, SplitNode object) {
            kryo.writeObject(output, object.orientation);
            output.writeInt(object.children.size());
            object.children.forEach(node -> kryo.writeClassAndObject(output, node));
            output.writeInt(object.dividers.size());
            object.dividers.forEach(divider -> output.writeDouble(divider));
        }

        @Override public SplitNode read(Kryo kryo, Input input, Class<SplitNode> type) {
            Orientation orientation = kryo.readObject(input, Orientation.class);
            List<IDashboardNode> children = IntStream.range(0, input.readInt()).mapToObj(index -> (IDashboardNode)kryo.readClassAndObject(input)).collect(Collectors.toList());
            List<Double> dividers = IntStream.range(0, input.readInt()).mapToObj(index -> input.readDouble()).collect(Collectors.toList());
            return new SplitNode(orientation, children, dividers);
        }
    }
}

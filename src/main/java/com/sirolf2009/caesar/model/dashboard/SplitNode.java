package com.sirolf2009.caesar.model.dashboard;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;

import java.util.List;

public class SplitNode implements IDataNode {

    private final List<IDataNode> children;
    private final List<Double> dividers;

    public SplitNode(List<IDataNode> children, List<Double> dividers) {
        this.children = children;
        this.dividers = dividers;
    }

    @Override
    public Node createNode() {
        SplitPane pane = new SplitPane();
        children.stream().map(child -> child.createNode()).forEach(child -> {
            pane.getItems().add(child);
        });
        for (int i = 0; i < dividers.size(); i++) {
            pane.setDividerPosition(i, dividers.get(i));
        }
        return pane;
    }
}

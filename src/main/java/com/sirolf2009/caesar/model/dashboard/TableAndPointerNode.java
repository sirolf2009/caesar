package com.sirolf2009.caesar.model.dashboard;

import com.sirolf2009.caesar.component.TablesTreeView;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.TableAndPointer;
import com.sirolf2009.caesar.model.table.IDataPointer;
import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class TableAndPointerNode implements IDataNode {

    private final TableAndPointer tableAndPointer;

    public TableAndPointerNode(TableAndPointer tableAndPointer) {
        this.tableAndPointer = tableAndPointer;
    }

    @Override
    public Node createNode() {
        Label label = new Label();
        Table table = TablesTreeView.table;
        IDataPointer pointer = TablesTreeView.pointer;
        table.getItems().addListener((InvalidationListener) e -> {
            label.setText(table.getItems().get(table.getItems().size() - 1).get(pointer) + "");
        });
        return label;
    }
}

package com.sirolf2009.caesar.model.dashboard;

import com.sirolf2009.caesar.model.JMXAttributes;
import com.sirolf2009.caesar.model.Table;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TableNode implements IDataNode {

    private final Table table;

    public TableNode(Table table) {
        this.table = table;
    }

    @Override
    public Node createNode() {
        TableView< JMXAttributes > tableView = new TableView<>();
        table.getChildren().forEach(pointer -> tableView.getColumns().add(new TableColumn<JMXAttributes, String>(){{
            textProperty().bind(pointer.nameProperty());
            setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOrDefault(pointer, "").toString()));
        }}));
        tableView.setItems(table.getItems());
        return tableView;
    }
}

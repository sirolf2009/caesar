package com.sirolf2009.component;

import com.sirolf2009.caesar.server.CaesarTable;
import com.sirolf2009.caesar.server.JMXServer;
import com.sirolf2009.caesar.server.model.Attribute;
import com.sirolf2009.caesar.server.model.NewValues;
import com.sirolf2009.controller.CaesarTableWindow;
import com.sirolf2009.util.ControllerUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import org.w3c.dom.Attr;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import java.util.UUID;

public class Table extends AnchorPane {

    private final ObservableList<NewValues> values = FXCollections.observableArrayList();
    private final ObservableList<Attribute> attributes = FXCollections.observableArrayList();
    private final JMXServer server;
    private final Attribute attribute;
    private final CaesarTable facade;
    private final UUID ID;

    @FXML
    TextField name;
    @FXML
    TableView<NewValues> table;

    public Table(JMXServer server, Attribute attribute) {
        this.server = server;
        this.attribute = attribute;
        this.ID = UUID.randomUUID();
        this.facade = server.createNewTable(ID.toString(), attribute, newValues -> {
                values.add(newValues);
        });
        ControllerUtil.load(this, "/fxml/table.fxml");
    }

    @FXML
    public void initialize() {
        name.setText(ID.toString());
        table.setItems(values);
        table.getColumns().add(getColumn(attribute));
        table.setOnDragOver(event1 -> {
            if (event1.getGestureSource() != table && event1.getDragboard().hasString()) {
                event1.acceptTransferModes(TransferMode.LINK);
                table.setStyle("-fx-effect: innershadow(gaussian, #039ed3, 10, 1.0, 0, 0);");
            }
            event1.consume();
        });
        table.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                table.setStyle("");
                event.consume();
            }
        });
        table.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                server.getAttribute(event.getDragboard().getString()).ifPresent(attr -> {
                    if(!attributes.contains(attr)) {
                        attributes.add(attr);
                        facade.add(attr);
                        table.getColumns().add(getColumn(attr));
                        event.consume();
                    }
                });
            }
        });
    }

    public TableColumn getColumn(Attribute attribute) {
        String name = attribute.getAttributeInfo().getName().toString();
        if (attribute.getAttributeInfo().getType().equals("int")) {
            TableColumn<NewValues, Integer> column = new CaesarTableColumn(name);
            column.setCellValueFactory(cellData -> new SimpleObjectProperty<>((Integer) cellData.getValue().getValues().getOrDefault(attribute, -1)));
            return column;
        } else if (attribute.getAttributeInfo().getType().equals("long")) {
            TableColumn<NewValues, Long> column = new CaesarTableColumn(name);
            column.setCellValueFactory(cellData -> new SimpleObjectProperty<>((Long) cellData.getValue().getValues().getOrDefault(attribute, -1l)));
            return column;
        } else if (attribute.getAttributeInfo().getType().equals("boolean")) {
            TableColumn<NewValues, Boolean> column = new CaesarTableColumn(name);
            column.setCellValueFactory(cellData -> new SimpleObjectProperty<>((Boolean) cellData.getValue().getValues().getOrDefault(attribute, false)));
            return column;
        } else if (attribute.getAttributeInfo().getType().equals("javax.management.openmbean.CompositeData") && attribute.getAttributeInfo() instanceof OpenMBeanAttributeInfoSupport) {
            CompositeType openType = (CompositeType) ((OpenMBeanAttributeInfoSupport) attribute.getAttributeInfo()).getOpenType();
            TableColumn<NewValues, String> column = new CaesarTableColumn(name);
            openType.keySet().forEach(key -> {
                String subType = openType.getType(key).getTypeName();
                if (subType.equals("java.lang.Long")) {
                    TableColumn<NewValues, Long> keyColumn = new CaesarTableColumn(key);
                    column.getColumns().add(keyColumn);
                    keyColumn.setCellValueFactory(cellData -> {
                        CompositeData data = (CompositeData) cellData.getValue().getValues().get(attribute);
                        try {
                            return new SimpleObjectProperty<Long>((Long) data.get(key));
                        } catch(Exception e) {
                            throw new IllegalArgumentException("Could not get data for key: "+key+" from "+data, e);
                        }
                    });
                } else {
                    TableColumn<NewValues, String> keyColumn = new CaesarTableColumn(key);
                    column.getColumns().add(keyColumn);
                    keyColumn.setCellValueFactory(cellData -> {
                        CompositeData data = (CompositeData) cellData.getValue().getValues().get(attribute);
                        return new SimpleStringProperty(data.get(key).toString());
                    });
                }
            });
            return column;
        } else {
            TableColumn<NewValues, String> column = new CaesarTableColumn(name);
            column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValues().getOrDefault(attribute, "").toString()));
            return column;
        }
    }

    static class CaesarTableColumn<S, T> extends TableColumn<S, T> {

        public CaesarTableColumn(String name) {
            super(name);
            setSortable(false);
        }

    }

}

package com.sirolf2009.controller;

import com.sirolf2009.caesar.server.CaesarTable;
import com.sirolf2009.caesar.server.JMXServer;
import com.sirolf2009.caesar.server.model.Attribute;
import com.sirolf2009.caesar.server.model.NewValues;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.*;
import jfxtras.scene.control.window.CloseIcon;
import jfxtras.scene.control.window.Window;

import javax.management.openmbean.*;
import java.io.IOException;
import java.util.UUID;

public class CaesarTableWindow extends Window {

    private final ObservableList<NewValues> values = FXCollections.observableArrayList();
    private final TreeView<Object> mBeans;
    private final JMXServer server;
    private final CaesarTable facade;
    private final TableView<NewValues> table;

    public CaesarTableWindow(TreeView<Object> mBeans, JMXServer server, Attribute attribute) {
        this.mBeans = mBeans;
        this.server = server;
        table = new TableView<>(values);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn column = getColumn(attribute);
        table.getColumns().add(column);
        this.facade = server.createNewTable(UUID.randomUUID().toString(), attribute, newValues -> {
            values.add(newValues);
        });

        setTitle(attribute.getAttributeInfo().getName().toString());
        table.prefWidthProperty().bind(prefWidthProperty());
        table.prefHeightProperty().bind(prefHeightProperty());
        setPrefWidth(300);
        setPrefHeight(300);
        setScaleX(1);
        setScaleY(1);
        getRightIcons().add(new CloseIcon(this));
        getContentPane().getChildren().add(table);

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
                Attribute newAttribute = (Attribute) mBeans.getSelectionModel().getSelectedItem().getValue();
                facade.add(newAttribute);
                table.getColumns().add(getColumn(newAttribute));
                event.consume();
            }
        });
        table.addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
            if(event.isShortcutDown() || event.isShiftDown())
                event.consume();
        });
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.getFocusModel().focusedCellProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal.getTableColumn() != null){
                Platform.runLater(() -> table.getSelectionModel().clearSelection());
                System.out.println("Selected TableColumn: "+ newVal.getTableColumn().getText());
                System.out.println("Selected column index: "+ newVal.getColumn());
            }
        });
    }

    public TableColumn getColumn(Attribute attribute) {
        String name = attribute.getAttributeInfo().getName().toString();
        String type = attribute.getAttributeInfo().getType();
        System.out.println(name + ": " + type);
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
                System.out.println(subType);
                if (subType.equals("java.lang.Long")) {
                    TableColumn<NewValues, Long> keyColumn = new CaesarTableColumn(key);
                    column.getColumns().add(keyColumn);
                    keyColumn.setCellValueFactory(cellData -> {
                        CompositeData data = (CompositeData) cellData.getValue().getValues().get(attribute);
                        return new SimpleObjectProperty<Long>((Long) data.get(key));
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

    @Override
    public void close() {
        super.close();
        try {
            facade.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class CaesarTableColumn<S, T> extends TableColumn<S, T> {

        public CaesarTableColumn(String name) {
            super(name);
            setSortable(false);
        }

    }
}

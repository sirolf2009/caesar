package com.sirolf2009.dialogs;

import com.sirolf2009.model.JMXAttribute;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TableConfigurationDialog extends Dialog<TableConfigurationDialog.TableConfiguration> {

    private final MBeanServerConnection connection;

    public TableConfigurationDialog(MBeanServerConnection connection) throws IOException {
        setTitle("Choose an attribute");
        this.connection = connection;
        List<JMXAttribute> attributes = connection.queryNames(null, null).parallelStream().flatMap(name -> {
            try {
                return Arrays.stream(connection.getMBeanInfo(name).getAttributes()).map(info -> {
                    return new JMXAttribute(name, info);
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        TextField name = new TextField();
        HBox nameContainer = new HBox(new Label("Table Name: "));

        ListView<JMXAttribute> unselectedAttributes = new ListView<>(FXCollections.observableArrayList(attributes));
        ListView<JMXAttribute> selectedAttributes = new ListView<>(FXCollections.observableArrayList());

        Button add = new Button("Add");
        add.setOnAction(event -> {
            unselectedAttributes.getSelectionModel().getSelectedItems().forEach(attribute -> {
                selectedAttributes.getItems().add(attribute);
            });
            selectedAttributes.getItems().forEach(attribute -> {
                unselectedAttributes.getItems().remove(attribute);
            });
        });
        Button remove = new Button("Remove");
        remove.setOnAction(event -> {
            selectedAttributes.getSelectionModel().getSelectedItems().forEach(attribute -> {
                unselectedAttributes.getItems().add(attribute);
            });
            unselectedAttributes.getItems().forEach(attribute -> {
                selectedAttributes.getItems().remove(attribute);
            });
        });
        Button ok = new Button("Ok");
        ok.setOnAction(event -> {
            setResult(new TableConfiguration(name.getText(), selectedAttributes.getItems()));
            hide();
        });
        VBox controls = new VBox(add, remove, ok);
        HBox attributeContainer = new HBox(unselectedAttributes, controls, selectedAttributes);

        getDialogPane().setContent(new VBox(nameContainer, attributeContainer));
    }

    public static class TableConfiguration {
        private final String tableName;
        private final List<JMXAttribute> attributes;

        public TableConfiguration(String tableName, List<JMXAttribute> attributes) {
            this.tableName = tableName;
            this.attributes = attributes;
        }

        public String getTableName() {
            return tableName;
        }

        public List<JMXAttribute> getAttributes() {
            return attributes;
        }
    }

}

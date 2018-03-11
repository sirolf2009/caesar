package com.sirolf2009.component;

import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.JMXAttributes;
import com.sirolf2009.model.Table;
import com.sirolf2009.util.ControllerUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import java.util.Arrays;

public class ChartTab extends VBox {

    private final ObservableList<Table> tables = FXCollections.observableArrayList();

    @FXML
    HBox columns;
    @FXML
    HBox rows;

    public ChartTab(ObservableList<Table> tables) {
        ControllerUtil.load(this, "/fxml/chart.fxml");
    }

    @FXML
    public void initialize() {
        columns.setOnDragOver(event1 -> {
            if (event1.getGestureSource() != columns && event1.getDragboard().hasString() && event1.getDragboard().getString().split("@").length == 3) {
                event1.acceptTransferModes(TransferMode.LINK);
                columns.setStyle("-fx-effect: innershadow(gaussian, #039ed3, 10, 1.0, 0, 0);");
            }
            event1.consume();
        });
        columns.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                columns.setStyle("");
                event.consume();
            }
        });
        columns.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                try {
                    String newVariable = event.getDragboard().getString();
                    String[] data = newVariable.split("@");
                    tables.stream().filter(table -> table.getName().equals(data[2])).findAny().ifPresent(table -> {
                        table.getChildren().stream().filter(attribute -> attribute.getObjectName().toString().equals(data[1])).filter(attribute -> attribute.getAttributeInfo().getName().equals(data[0])).findAny().ifPresent(attribute -> {
                            //TODO: create ColumnOrRow
                            //TODO: update chart based on selected columns and rows
                        });

                        System.out.println(Arrays.toString(data)); //[Uptime, java.lang:type=Runtime, Untitled 1]
                        event.consume();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

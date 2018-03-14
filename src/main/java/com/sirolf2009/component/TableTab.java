package com.sirolf2009.component;

import com.sirolf2009.JMXPuller;
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
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import org.fxmisc.easybind.EasyBind;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import java.util.Arrays;

public class TableTab extends AnchorPane {

    private final Table tableModel;
    private final MBeanServerConnection connection;
    @FXML
    private TableView<JMXAttributes> table;
    @FXML
    private ToggleButton runningButton;
    @FXML
    private TextField intervalTextfield;

    public TableTab(Table table, MBeanServerConnection connection) {
        this.connection = connection;
        tableModel = table;
        ControllerUtil.load(this, "/fxml/table.fxml");
    }

    @FXML
    public void initialize() {
        runningButton.selectedProperty().bindBidirectional(tableModel.getPuller().runningProperty());
        tableModel.getPuller().timeoutProperty().bind(EasyBind.map(intervalTextfield.textProperty(), string -> string.length() == 0 ? 0 : Integer.parseInt(string)));

        table.setItems(tableModel.getItems());
        table.setOnDragOver(event1 -> {
            if (event1.getGestureSource() != table && event1.getDragboard().hasString() && event1.getDragboard().getString().split("@").length == 2) {
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
                try {
                    String newVariable = event.getDragboard().getString();
                    String[] data = newVariable.split("@");
                    ObjectName objectName = new ObjectName(data[1]);
                    Arrays.stream(connection.getMBeanInfo(objectName).getAttributes()).filter(mBeanAttributeInfo -> {
                        return mBeanAttributeInfo.getName().equals(data[0]);
                    }).findAny().ifPresent(mBeanAttributeInfo -> {
                        JMXAttribute attribute = new JMXAttribute(objectName, mBeanAttributeInfo);
                        tableModel.getChildren().add(attribute);
                        table.getColumns().add(getColumn(attribute));
                        if (table.getColumns().size() == 1) {
                            Thread pullerThread = new Thread(tableModel.getPuller());
                            pullerThread.setDaemon(true);
                            pullerThread.start();
                        }
                    });
                    event.consume();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public TableColumn getColumn(JMXAttribute attribute) {
        String name = attribute.getAttributeInfo().getName().toString();
        if (attribute.getAttributeInfo().getType().equals("int")) {
            TableColumn<JMXAttributes, Integer> column = new CaesarTableColumn(name);
            column.setCellValueFactory(cellData -> {
                return new SimpleObjectProperty<>((Integer) cellData.getValue().getOrDefault(attribute, -1));
            });
            return column;
        } else if (attribute.getAttributeInfo().getType().equals("long")) {
            TableColumn<JMXAttributes, Long> column = new CaesarTableColumn(name);
            column.setCellValueFactory(cellData -> new SimpleObjectProperty<>((Long) cellData.getValue().getOrDefault(attribute, -1l)));
            return column;
        } else if (attribute.getAttributeInfo().getType().equals("boolean")) {
            TableColumn<JMXAttributes, Boolean> column = new CaesarTableColumn(name);
            column.setCellValueFactory(cellData -> new SimpleObjectProperty<>((Boolean) cellData.getValue().getOrDefault(attribute, false)));
            return column;
        } else if (attribute.getAttributeInfo().getType().equals("javax.management.openmbean.CompositeData") && attribute.getAttributeInfo() instanceof OpenMBeanAttributeInfoSupport) {
            CompositeType openType = (CompositeType) ((OpenMBeanAttributeInfoSupport) attribute.getAttributeInfo()).getOpenType();
            TableColumn<JMXAttributes, String> column = new CaesarTableColumn(name);
            openType.keySet().forEach(key -> {
                String subType = openType.getType(key).getTypeName();
                if (subType.equals("java.lang.Long")) {
                    TableColumn<JMXAttributes, Long> keyColumn = new CaesarTableColumn(key);
                    column.getColumns().add(keyColumn);
                    keyColumn.setCellValueFactory(cellData -> {
                        if (cellData.getValue().containsKey(key)) {
                            CompositeData data = (CompositeData) cellData.getValue().get(attribute);
                            try {
                                return new SimpleObjectProperty<Long>((Long) data.get(key));
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to get " + key + " " + data);
                            }
                        } else {
                            return new SimpleObjectProperty<Long>(-1l);
                        }
                    });
                } else {
                    TableColumn<JMXAttributes, String> keyColumn = new CaesarTableColumn(key);
                    column.getColumns().add(keyColumn);
                    keyColumn.setCellValueFactory(cellData -> {
                        CompositeData data = (CompositeData) cellData.getValue().get(attribute);
                        return new SimpleStringProperty(data.get(key).toString());
                    });
                }
            });
            return column;
        } else {
            TableColumn<JMXAttributes, String> column = new CaesarTableColumn(name);
            column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOrDefault(attribute, "").toString()));
            return column;
        }
    }

    public TableView<JMXAttributes> getTable() {
        return table;
    }

    public Table getTableModel() {
        return tableModel;
    }

    static class CaesarTableColumn<S, T> extends TableColumn<S, T> {

        public CaesarTableColumn(String name) {
            super(name);
            setSortable(false);
        }

    }
}

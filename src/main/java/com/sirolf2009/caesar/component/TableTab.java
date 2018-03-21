package com.sirolf2009.caesar.component;

import com.sirolf2009.caesar.Connection;
import com.sirolf2009.caesar.JMXPuller;
import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.model.JMXAttributes;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.table.JMXCompositeAttribute;
import com.sirolf2009.caesar.model.table.map.LongToDate;
import com.sirolf2009.caesar.util.ControllerUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import org.fxmisc.easybind.EasyBind;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TableTab extends AnchorPane {

	private final Table tableModel;
	private final Connection connection;
	private final JMXPuller puller;
	@FXML private EditableTableView<JMXAttributes> table;
	@FXML private ToggleButton runningButton;
	@FXML private TextField intervalTextfield;

	public TableTab(Table table, Connection connection) {
		this.connection = connection;
		tableModel = table;
		puller = new JMXPuller(connection, tableModel.getChildren(), tableModel.getItems(), 1000);
		ControllerUtil.load(this, "/fxml/table.fxml");
		Thread pullerThread = new Thread(puller);
		pullerThread.setDaemon(true);
		pullerThread.start();
	}

	@FXML public void initialize() {
		runningButton.selectedProperty().bindBidirectional(puller.runningProperty());
		puller.timeoutProperty().bind(EasyBind.map(intervalTextfield.textProperty(), string -> string.length() == 0 ? 0 : Integer.parseInt(string)));

		table.setItems(tableModel.getItems());
		tableModel.getChildren().forEach(pointer -> addPointer(pointer));
		table.setOnDragOver(event1 -> {
			if(event1.getGestureSource() != table && event1.getDragboard().hasString() && event1.getDragboard().getString().split("@").length == 2) {
				event1.acceptTransferModes(TransferMode.LINK);
				table.setStyle("-fx-effect: innershadow(gaussian, #039ed3, 10, 1.0, 0, 0);");
			}
			event1.consume();
		});
		table.setOnDragExited(new EventHandler<DragEvent>() {
			@Override public void handle(DragEvent event) {
				table.setStyle("");
				event.consume();
			}
		});
		table.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override public void handle(DragEvent event) {
				try {
					String newVariable = event.getDragboard().getString();
					String[] data = newVariable.split("@");
					ObjectName objectName = new ObjectName(data[1]);
					connection.getConnection().ifPresent(connection -> {
						try {
							Arrays.stream(connection.getMBeanInfo(objectName).getAttributes()).filter(mBeanAttributeInfo -> {
								return mBeanAttributeInfo.getName().equals(data[0]);
							}).findAny().ifPresent(mBeanAttributeInfo -> {
								JMXAttribute attribute = new JMXAttribute(objectName, mBeanAttributeInfo);
								getDataPointers(attribute).forEach(pointer -> {
									Platform.runLater(() -> {
										tableModel.getChildren().add(pointer);
										addPointer(pointer);
									});
								});
							});
						} catch(Exception e) {
							e.printStackTrace();
						}
						event.consume();
					});
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void addPointer(IDataPointer pointer) {
		table.getColumns().add(new CaesarTableColumn(pointer));
	}

	public List<IDataPointer> getDataPointers(JMXAttribute attribute) {
		if(attribute.getAttributeInfo().getType().equals("javax.management.openmbean.CompositeData") && attribute.getAttributeInfo() instanceof OpenMBeanAttributeInfoSupport) {
			CompositeType openType = (CompositeType) ((OpenMBeanAttributeInfoSupport) attribute.getAttributeInfo()).getOpenType();
			return openType.keySet().stream().map(key -> {
				return new JMXCompositeAttribute(attribute.getObjectName(), (OpenMBeanAttributeInfoSupport) attribute.getAttributeInfo(), key);
			}).collect(Collectors.toList());
		} else {
			return Arrays.asList(attribute);
		}
	}

	public TableView<JMXAttributes> getTable() {
		return table;
	}

	public Table getTableModel() {
		return tableModel;
	}

	public JMXPuller getPuller() {
		return puller;
	}

	class CaesarTableColumn extends TableColumn<JMXAttributes, String> {

		private final IDataPointer pointer;

		public CaesarTableColumn(IDataPointer pointer) {
			super(pointer.getName());
			this.pointer = pointer;
			setSortable(false);
			pointer.nameProperty().bind(textProperty());
			setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOrDefault(pointer, "").toString()));
		}

		public List<MenuItem> getContextItems() {
			List<MenuItem> items = new ArrayList<>();
			if(pointer.getType().equals("long") || pointer.getType().equals("java.lang.Long")) {
				MenuItem item = new MenuItem("Map to date");
				item.setOnAction(e -> {
					IDataPointer newPointer = new LongToDate(pointer);
					tableModel.getChildren().add(newPointer);
					addPointer(newPointer);
				});
				return Arrays.asList(item);
			}
			return Arrays.asList();
		}

	}
}

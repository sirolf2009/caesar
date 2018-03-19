package com.sirolf2009.caesar.component;

import com.sirolf2009.caesar.JMXPuller;
import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.model.JMXAttributes;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.table.JMXCompositeAttribute;
import com.sirolf2009.caesar.util.ControllerUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import org.fxmisc.easybind.EasyBind;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TableTab extends AnchorPane {

	private final Table tableModel;
	private final MBeanServerConnection connection;
	private final JMXPuller puller;
	@FXML private TableView<JMXAttributes> table;
	@FXML private ToggleButton runningButton;
	@FXML private TextField intervalTextfield;

	public TableTab(Table table, MBeanServerConnection connection) {
		this.connection = connection;
		tableModel = table;
		puller = new JMXPuller(tableModel.getChildren(), tableModel.getItems(), 1000);
		puller.setConnection(connection);
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
					Arrays.stream(connection.getMBeanInfo(objectName).getAttributes()).filter(mBeanAttributeInfo -> {
						return mBeanAttributeInfo.getName().equals(data[0]);
					}).findAny().ifPresent(mBeanAttributeInfo -> {
						JMXAttribute attribute = new JMXAttribute(objectName, mBeanAttributeInfo);
						getDataPointers(attribute).forEach(pointer -> {
							tableModel.getChildren().add(pointer);
							addPointer(pointer);
						});
					});
					event.consume();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void addPointer(IDataPointer pointer) {
		table.getColumns().add(getColumn(pointer));
	}

	public TableColumn getColumn(IDataPointer pointer) {
		TableColumn<JMXAttributes, String> column = new CaesarTableColumn(pointer.toString());
		column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOrDefault(pointer, "").toString()));
		return column;
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

	static class CaesarTableColumn<S, T> extends TableColumn<S, T> {

		public CaesarTableColumn(String name) {
			super(name);
			setSortable(false);
		}

	}
}

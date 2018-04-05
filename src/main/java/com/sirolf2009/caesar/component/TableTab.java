package com.sirolf2009.caesar.component;

import com.sirolf2009.caesar.Connection;
import com.sirolf2009.caesar.JMXPuller;
import com.sirolf2009.caesar.model.JMXAttributes;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.table.CurrentTime;
import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.model.table.JMXCompositeAttribute;
import com.sirolf2009.caesar.model.table.map.*;
import com.sirolf2009.caesar.util.ControllerUtil;
import com.sirolf2009.caesar.util.TypeUtil;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import org.fxmisc.easybind.EasyBind;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static com.sirolf2009.caesar.util.TypeUtil.*;

public class TableTab extends AnchorPane {

	private final Table tableModel;
	private final Connection connection;
	private final JMXPuller puller;
	@FXML private EditableTableView<JMXAttributes> table;
	@FXML private ToggleButton runningButton;
	@FXML private TextField intervalTextfield;
	@FXML private ProgressIndicator sleepProgress;

	public TableTab(Table table, Connection connection) {
		this.connection = connection;
		tableModel = table;
		puller = new JMXPuller(connection, tableModel.getChildren(), tableModel.getItems(), 1000);
		ControllerUtil.load(this, "/fxml/table.fxml");
		Thread pullerThread = new Thread(puller);
		pullerThread.setDaemon(true);
		pullerThread.start();
		sleepProgress.progressProperty().bind(puller.sleepProgressProperty());
	}

	@FXML public void initialize() {
		runningButton.selectedProperty().bindBidirectional(puller.runningProperty());
		runningButton.textProperty().bind(EasyBind.map(runningButton.selectedProperty(), value -> value ? "Running" : "Paused"));

		puller.timeoutProperty().bind(tableModel.updateTimeoutProperty());
		intervalTextfield.setText(tableModel.getUpdateTimeout()+"");
		tableModel.updateTimeoutProperty().bind(EasyBind.map(intervalTextfield.textProperty(), string -> string.length() == 0 ? 0 : Integer.parseInt(string)));

		table.setItems(tableModel.getItems());
		tableModel.getChildren().forEach(pointer -> addPointer(pointer));
		table.widthProperty().addListener(new ChangeListener<Number>() {
			@Override public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) {
				TableHeaderRow header = (TableHeaderRow) table.lookup("TableHeaderRow");
				header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
					@Override public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
						if(!newValue) {
							new Thread(() -> {
								try {
									Thread.sleep(100);
								} catch(InterruptedException e) {
									e.printStackTrace();
								}
								Platform.runLater(() -> {
									tableModel.getChildren().setAll(header.getRootHeader().getColumnHeaders().stream().map(header -> (CaesarTableColumn) header.getTableColumn()).map(col -> col.pointer).collect(Collectors.toList()));
								});
							}).start();
						}
					}
				});
			}
		});
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

	public void checkNow() {
		puller.update();
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

	public void addCurrentTime() {
		CurrentTime time = new CurrentTime();
		tableModel.getChildren().add(time);
		addPointer(time);
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
			if(pointer.getType().startsWith("[")) {
				setCellValueFactory(cellData -> new SimpleStringProperty(Arrays.toString((Object[]) cellData.getValue().getOrDefault(pointer, new Object[0]))));
			} else {
				setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOrDefault(pointer, "").toString()));
			}
		}

		public List<MenuItem> getContextItems() {
			List<MenuItem> items = new ArrayList<>();
			MenuItem remove = new MenuItem("Remove");
			remove.setOnAction(e -> {
				tableModel.getChildren().remove(pointer);
				table.getColumns().remove(this);
			});
			items.add(remove);
			if(isLong(pointer)) {
				MenuItem item = new MenuItem("Map to date");
				item.setOnAction(e -> {
					IDataPointer newPointer = new LongToDate(pointer);
					tableModel.getChildren().add(newPointer);
					addPointer(newPointer);
				});
				items.add(item);
			}
			if(isNumber(pointer)) {
				MenuItem item = new MenuItem("ABS");
				item.setOnAction(e -> {
					IDataPointer newPointer = new Abs(pointer);
					tableModel.getChildren().add(newPointer);
					addPointer(newPointer);
				});
				items.add(item);
			}
			if(isNumberArray(pointer.getType())) {
				MenuItem item = new MenuItem("Max");
				item.setOnAction(e -> {
					IDataPointer newPointer = new Max(pointer);
					tableModel.getChildren().add(newPointer);
					addPointer(newPointer);
				});
				items.add(item);
			}
			if(isNumberArray(pointer.getType())) {
				MenuItem item = new MenuItem("Min");
				item.setOnAction(e -> {
					IDataPointer newPointer = new Min(pointer);
					tableModel.getChildren().add(newPointer);
					addPointer(newPointer);
				});
				items.add(item);
			}
			if(isNumber(pointer.getType()) || isBoolean(pointer)) {
				MenuItem item = new MenuItem("Negate");
				item.setOnAction(e -> {
					IDataPointer newPointer = new Negate(pointer);
					tableModel.getChildren().add(newPointer);
					addPointer(newPointer);
				});
				items.add(item);
			}
			return items;
		}

	}
}

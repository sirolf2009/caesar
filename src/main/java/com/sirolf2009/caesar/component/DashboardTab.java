package com.sirolf2009.caesar.component;

import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.JMXAttributes;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.table.IDataPointer;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Pair;
import org.dockfx.DockNode;
import org.dockfx.DockPane;
import org.dockfx.DockPos;

import java.util.Arrays;

public class DashboardTab extends DockPane {

	public DashboardTab() {
		setOnDragOver(event1 -> {
			if(event1.getGestureSource() != this && event1.getDragboard().hasContent(TablesTreeView.TABLE) || event1.getDragboard().hasContent(TablesTreeView.TABLE_AND_POINTER) || event1.getDragboard().hasContent(ChartsTreeView.CHART)) {
				event1.acceptTransferModes(TransferMode.LINK);
				setStyle("-fx-effect: innershadow(gaussian, rgb(114, 137, 218), 10, 1.0, 0, 0);");
			}
			event1.consume();
		});
		setOnDragExited(new EventHandler<DragEvent>() {
			@Override public void handle(DragEvent event) {
				setStyle("");
				event.consume();
			}
		});
		setOnDragDropped(new EventHandler<DragEvent>() {
			@Override public void handle(DragEvent event) {
				try {
					if(event.getDragboard().hasContent(TablesTreeView.TABLE)) {
						Table table = TablesTreeView.table;
						TableView<JMXAttributes> tableView = new TableView<>();
						table.getChildren().forEach(pointer -> tableView.getColumns().add(new TableColumn<JMXAttributes, String>(){{
							textProperty().bind(pointer.nameProperty());
							setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOrDefault(pointer, "").toString()));
						}}));
						tableView.setItems(table.getItems());
						DockNode node = new DockNode(tableView);
						node.titleProperty().bind(table.nameProperty());
						dock(node, getDockPos(event));
						event.consume();
					} else if(event.getDragboard().hasContent(TablesTreeView.TABLE_AND_POINTER)) {
						Label label = new Label();
						Table table = TablesTreeView.table;
						IDataPointer pointer = TablesTreeView.pointer;
						table.getItems().addListener((InvalidationListener) e -> {
							label.setText(table.getItems().get(table.getItems().size() - 1).get(pointer) + "");
						});
						DockNode node = new DockNode(label);
						node.titleProperty().bind(pointer.nameProperty());
						dock(node, getDockPos(event));
						event.consume();
					} else if(event.getDragboard().hasContent(ChartsTreeView.CHART)) {
						Chart chart = ChartsTreeView.chart;
						ChartTab.chartTypes.stream().filter(type -> type.getPredicate().test(chart)).findAny().ifPresent(chartType -> {
							Node chartNode = chartType.getChart(chart);
							DockNode node = new DockNode(chartNode);
							node.titleProperty().bind(chart.nameProperty());
							dock(node, getDockPos(event));
							event.consume();
						});
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		DockPane.initializeDefaultUserAgentStylesheet();
	}

	private DockPos getDockPos(DragEvent event) {
		Pair<DockPos, Double> distanceLeft = new Pair(DockPos.LEFT, event.getX());
		Pair<DockPos, Double> distanceRight = new Pair(DockPos.RIGHT, getWidth() - event.getX());
		Pair<DockPos, Double> distanceTop = new Pair(DockPos.TOP, event.getY());
		Pair<DockPos, Double> distanceBottom = new Pair(DockPos.BOTTOM, getHeight() - event.getY());
		Pair<DockPos, Double> distanceCenter = new Pair(DockPos.CENTER, Math.sqrt(Math.pow(event.getX()-getWidth()/2, 2)+Math.pow(event.getY()-getHeight()/2, 2)));
		return Arrays.asList(distanceLeft, distanceRight, distanceTop, distanceBottom).stream().min((a,b) -> a.getValue().compareTo(b.getValue())).get().getKey();
	}

}
package com.sirolf2009.caesar.component;

import com.sirolf2009.caesar.model.*;
import com.sirolf2009.caesar.model.dashboard.*;
import com.sirolf2009.caesar.model.table.IDataPointer;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Pair;
import org.dockfx.DockPane;
import org.dockfx.DockPos;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class DashboardTab extends DockPane {

	private final Dashboard dashboard;

	public DashboardTab(Dashboard dashboard) {
		this.dashboard = dashboard;
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
						addDockNode(event, table, tableView);
						event.consume();
					} else if(event.getDragboard().hasContent(TablesTreeView.TABLE_AND_POINTER)) {
						Label label = new Label();
						Table table = TablesTreeView.table;
						IDataPointer pointer = TablesTreeView.pointer;
						table.getItems().addListener((InvalidationListener) e -> {
							label.setText(table.getItems().get(table.getItems().size() - 1).get(pointer) + "");
						});
						addDockNode(event, new TableAndPointer(table, pointer), label);
						event.consume();
					} else if(event.getDragboard().hasContent(ChartsTreeView.CHART)) {
						Chart chart = ChartsTreeView.chart;
						ChartTab.chartTypes.stream().filter(type -> type.getPredicate().test(chart)).findAny().ifPresent(chartType -> {
							Node chartNode = chartType.getChart(chart);
							addDockNode(event, chart, chartNode);
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

	public void addDockNode(DragEvent event, IDashboardNode dataNode, Node content) {
		DashboardDockNode node = new DashboardDockNode(dataNode, content);
		node.titleProperty().bind(dataNode.nameProperty());
		node.dock(DashboardTab.this, getDockPos(event));
		node.parentProperty().addListener(e -> updateModel());
		updateModel();
	}

	private DockPos getDockPos(DragEvent event) {
		Pair<DockPos, Double> distanceLeft = new Pair(DockPos.LEFT, event.getX());
		Pair<DockPos, Double> distanceRight = new Pair(DockPos.RIGHT, getWidth() - event.getX());
		Pair<DockPos, Double> distanceTop = new Pair(DockPos.TOP, event.getY());
		Pair<DockPos, Double> distanceBottom = new Pair(DockPos.BOTTOM, getHeight() - event.getY());
		return Arrays.asList(distanceLeft, distanceRight, distanceTop, distanceBottom).stream().min((a,b) -> a.getValue().compareTo(b.getValue())).get().getKey();
	}

	public void updateModel() {
		SplitPane root = (SplitPane) getChildren().get(0);
		dashboard.setRoot(serialize(root));
	}

	public SplitNode serialize(SplitPane root) {
		return new SplitNode(root.getItems().stream().map(child -> serialize(child)).collect(Collectors.toList()), DoubleStream.of(root.getDividerPositions()).boxed().collect(Collectors.toList()));
	}

	public IDataNode serialize(Node node) {
		if(node instanceof SplitPane) {
			return serialize((SplitPane)node);
		} else if(node instanceof DashboardDockNode) {
			return serialize((DashboardDockNode)node);
		} else {
			throw new IllegalArgumentException("Unknown childtype: " + node);
		}
	}

	public IDataNode serialize(DashboardDockNode dockNode) {
		IDashboardNode node = dockNode.getData();
		if(node instanceof Chart) {
			return new ChartNode((Chart) node);
		} else if(node instanceof Table) {
			return new TableNode((Table) node);
		} else if(node instanceof TableAndPointer) {
			return new TableAndPointerNode((TableAndPointer) node);
		} else {
			throw new IllegalArgumentException("Unknown nodetype: "+node);
		}
	}

}

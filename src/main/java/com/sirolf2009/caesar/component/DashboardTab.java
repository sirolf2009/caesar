package com.sirolf2009.caesar.component;

import com.sirolf2009.caesar.model.*;
import com.sirolf2009.caesar.model.dashboard.SplitNode;
import com.sirolf2009.caesar.model.table.IDataPointer;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Pair;
import org.dockfx.DockEvent;
import org.dockfx.DockPane;
import org.dockfx.DockPos;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class DashboardTab extends DockPane {

	private static Constructor dockNodeEventHandlerConstructor;
	private static Field filtersField;
	private static Field rootField;

	static {
		try {
			dockNodeEventHandlerConstructor = Arrays.stream(DockPane.class.getDeclaredClasses()).filter(clazz -> clazz.toString().equals("class org.dockfx.DockPane$DockNodeEventHandler")).findAny().get().getDeclaredConstructors()[0];
			dockNodeEventHandlerConstructor.setAccessible(true);
			filtersField = DockPane.class.getDeclaredField("dockNodeEventFilters");
			filtersField.setAccessible(true);
			rootField = DockPane.class.getDeclaredField("root");
			rootField.setAccessible(true);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

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
						addDockNode(event, table, table.createNode());
						event.consume();
					} else if(event.getDragboard().hasContent(TablesTreeView.TABLE_AND_POINTER)) {
						Table table = TablesTreeView.table;
						IDataPointer pointer = TablesTreeView.pointer;
						TableAndPointer tableAndPointer = new TableAndPointer(table.getName() + "/" + pointer.getName(), table, pointer);
						addDockNode(event, tableAndPointer, tableAndPointer.createNode());
						event.consume();
					} else if(event.getDragboard().hasContent(ChartsTreeView.CHART)) {
						Chart chart = ChartsTreeView.chart;
						addDockNode(event, chart, chart.createNode());
						event.consume();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		if(dashboard.getRoot() != null) {
			SplitPane root = (SplitPane) dashboard.getRoot().createNode();
			try {
				rootField.set(this, root);
			} catch(IllegalAccessException e) {
				e.printStackTrace();
			}
			getChildren().add(root);
			initSplitPane(root);
		}
		DockPane.initializeDefaultUserAgentStylesheet();
	}

	private void initSplitPane(SplitPane splitPane) {
		splitPane.getItems().forEach(child -> {
			if(child instanceof SplitPane) {
				initSplitPane((SplitPane) child);
			} else if(child instanceof DashboardDockNode) {
				initDashboardDockNode((DashboardDockNode) child);
			} else {
				throw new IllegalArgumentException("Unknown type: " + child);
			}
		});
	}

	private void initDashboardDockNode(DashboardDockNode dashboardDockNode) {
		dashboardDockNode.setDashboard(this);
		try {
			Map filters = (Map) filtersField.get(this);
			EventHandler handler = (EventHandler) dockNodeEventHandlerConstructor.newInstance(this, dashboardDockNode);
			filters.put(dashboardDockNode, handler);
			dashboardDockNode.addEventFilter(DockEvent.DOCK_OVER, handler);
		} catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
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
		return Arrays.asList(distanceLeft, distanceRight, distanceTop, distanceBottom).stream().min(Comparator.comparing(Pair::getValue)).get().getKey();
	}

	public void updateModel() {
		SplitPane root = (SplitPane) getChildren().get(0);
		dashboard.setRoot(serialize(root));
	}

	public SplitNode serialize(SplitPane root) {
		return new SplitNode(root.getOrientation(), root.getItems().stream().map(child -> serialize(child)).collect(Collectors.toList()), DoubleStream.of(root.getDividerPositions()).boxed().collect(Collectors.toList()));
	}

	public IDashboardNode serialize(Node node) {
		if(node instanceof SplitPane) {
			return serialize((SplitPane) node);
		} else if(node instanceof DashboardDockNode) {
			return ((DashboardDockNode) node).getData();
		} else {
			throw new IllegalArgumentException("Unknown childtype: " + node);
		}
	}

}

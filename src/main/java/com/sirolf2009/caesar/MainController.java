package com.sirolf2009.caesar;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.component.*;
import com.sirolf2009.caesar.model.*;
import com.sirolf2009.caesar.model.serializer.CaesarKryo;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.util.FXUtil;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.management.MBeanInfo;
import java.io.*;
import java.util.Arrays;

public class MainController {

	private CaesarModel model;
	private Connection connection;

	@FXML private ToolBar toolbar;
	@FXML private AnchorPane variablesAnchor;
	@FXML private AnchorPane tablesAnchor;
	@FXML private AnchorPane chartsAnchor;
	@FXML private TabPane tabs;
	@FXML private Label lblUrl;
	@FXML private CheckBox connected;
	@FXML private ProgressIndicator connecting;
	private VariablesTreeView variables;
	private TablesTreeView tablesTreeView;
	private ChartsTreeView chartsTreeView;

	public MainController() {
		this(new CaesarModel());
	}

	public MainController(CaesarModel model) {
		this.model = model;
	}

	@FXML public void initialize() {
		variables = new VariablesTreeView();
		variablesAnchor.getChildren().add(variables);
		FXUtil.maximize(variables);

		tablesTreeView = new TablesTreeView(model.getTables());
		tablesAnchor.getChildren().add(tablesTreeView);
		FXUtil.maximize(tablesTreeView);

		chartsTreeView = new ChartsTreeView(model.getCharts());
		chartsAnchor.getChildren().add(chartsTreeView);
		FXUtil.maximize(chartsTreeView);
	}

	public void save() {
		FileChooser chooser = new FileChooser();
		File file = chooser.showSaveDialog(toolbar.getScene().getWindow());
		if(file != null) {
			try {
				CaesarKryo kryo = new CaesarKryo();
				Output out = new Output(new FileOutputStream(file));
				kryo.writeObject(out, model);
				out.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void load() {
		FileChooser chooser = new FileChooser();
		File file = chooser.showOpenDialog(toolbar.getScene().getWindow());
		if(file != null) {
			try {
				load(file);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void load(File file) throws FileNotFoundException {
		CaesarKryo kryo = new CaesarKryo();
		try(Input input = new Input(new FileInputStream(file))) {
			switchTo(kryo.readObject(input, CaesarModel.class));
		}
	}

	public void switchTo(CaesarModel newModel) {
		tabs.getTabs().stream().filter(tab -> tab.getContent() instanceof TableTab).forEach(tab -> ((TableTab) tab.getContent()).getPuller().runningProperty().set(false));
		tabs.getTabs().clear();
		this.model = newModel;
		tablesTreeView.setItems(model.getTables());
		chartsTreeView.setItems(model.getCharts());
		model.getTables().forEach(table -> addTable(table));
		model.getCharts().forEach(chart -> addChart(chart));
		model.getDashboards().forEach(dashboard -> addDashboard(dashboard));
		tabs.getTabs().stream().filter(tab -> tab.getContent() instanceof TableTab).forEach(tab -> ((TableTab) tab.getContent()).getPuller().runningProperty().set(true));
	}

	public void newTable() {
		Table table = new Table(getNewTabName());
		model.getTables().add(table);
		addTable(table);
	}

	public void addTable(Table table) {
		Tab newTableTab = new RenameableTab(table.nameProperty());
		tabs.getTabs().add(newTableTab);
		newTableTab.setOnCloseRequest(e -> {
			model.getTables().remove(table);
		});
		tabs.getSelectionModel().select(newTableTab);
		newTableTab.setContent(new TableTab(table, connection));
	}

	public void newChart() {
		Chart chart = new Chart(getNewTabName());
		model.getCharts().add(chart);
		addChart(chart);
	}

	public void addChart(Chart chart) {
		Tab newChartTab = new RenameableTab(chart.nameProperty());
		newChartTab.setContent(new ChartTab(chart, model.getTables()));
		newChartTab.setOnCloseRequest(e -> {
			model.getCharts().remove(chart);
		});
		tabs.getTabs().add(newChartTab);
		tabs.getSelectionModel().select(newChartTab);
	}

	public void newDashboard() {
		Dashboard dashboard = new Dashboard(getNewTabName());
		model.getDashboards().add(dashboard);
		addDashboard(dashboard);
	}

	public void addDashboard(Dashboard dashboard) {
		Tab newDashboardTab = new RenameableTab(dashboard.nameProperty());
		newDashboardTab.setContent(new DashboardTab(dashboard));
		newDashboardTab.setOnCloseRequest(e -> {
			model.getDashboards().remove(dashboard);
		});
		tabs.getTabs().add(newDashboardTab);
		tabs.getSelectionModel().select(newDashboardTab);
	}

	public void queryAttributes() {
		ObservableList<JMXObject> objects = FXCollections.observableArrayList();
		connection.getConnection().ifPresent(connection -> {
			try {
				connection.queryNames(null, null).forEach(objectName -> {
					ObservableList<JMXAttribute> attributes = FXCollections.observableArrayList();
					try {
						MBeanInfo bean = connection.getMBeanInfo(objectName);
						Arrays.stream(bean.getAttributes()).forEach(attributeInfo -> {
							attributes.add(new JMXAttribute(objectName, attributeInfo));
						});
					} catch(Exception e) {
						e.printStackTrace();
					}
					objects.add(new JMXObject(objectName, attributes));
				});
			} catch(IOException e) {
				e.printStackTrace();
			}
		});
		variables.getVariables().setItems(objects.sorted());
	}

	private String getNewTabName() {
		return "Untitled " + (tabs.getTabs().size() + 1);
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;

		lblUrl.setText(connection.getServiceURL().toString());
		connected.visibleProperty().bind(connection.connectedProperty());
		connecting.visibleProperty().bind(connection.connectedProperty().not());
		connecting.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
	}

}

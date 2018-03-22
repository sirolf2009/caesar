package com.sirolf2009.caesar;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.component.*;
import com.sirolf2009.caesar.model.CaesarModel;
import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.JMXObject;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.util.FXUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
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
				Kryo kryo = new Kryo();
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
		Kryo kryo = new Kryo();
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
		tabs.getTabs().stream().filter(tab -> tab.getContent() instanceof TableTab).forEach(tab -> ((TableTab) tab.getContent()).getPuller().runningProperty().set(true));
	}

	public void newTable() {
		Table table = new Table("Untitled " + (tabs.getTabs().size() + 1));
		model.getTables().add(table);
		addTable(table);
	}

	public void addTable(Table table) {
		Tab newTableTab = new RenameableTab(table.nameProperty());
		tabs.getTabs().add(newTableTab);
		tabs.getSelectionModel().select(newTableTab);
		newTableTab.setContent(new TableTab(table, connection));
	}

	public void newChart() {
		Chart chart = new Chart("Untitled " + (tabs.getTabs().size() + 1));
		model.getCharts().add(chart);
		addChart(chart);
	}

	public void addChart(Chart chart) {
		Tab newChartTab = new RenameableTab(chart.nameProperty());
		newChartTab.setContent(new ChartTab(chart, model.getTables()));
		tabs.getTabs().add(newChartTab);
		tabs.getSelectionModel().select(newChartTab);
	}

	public void newDashboard() {
		Tab newDashboardTab = new Tab();
		newDashboardTab.setContent(new DashboardTab());
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
		variables.setItems(objects.sorted());
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}

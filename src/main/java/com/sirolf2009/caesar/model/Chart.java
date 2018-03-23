package com.sirolf2009.caesar.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.component.hierarchy.IHierarchicalData;
import com.sirolf2009.caesar.model.chart.type.BarChartType;
import com.sirolf2009.caesar.model.chart.type.IChartType;
import com.sirolf2009.caesar.model.chart.type.LineChartType;
import com.sirolf2009.caesar.model.chart.type.TimeseriesChartType;
import com.sirolf2009.caesar.model.serializer.ChartSerializer;
import com.sirolf2009.caesar.util.FXUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@DefaultSerializer(ChartSerializer.class) public class Chart implements IHierarchicalData<ColumnOrRow>, IDashboardNode {

	public static List<IChartType> chartTypes = Arrays.asList(new LineChartType(), new BarChartType(), new TimeseriesChartType());

	private final SimpleStringProperty name;
	private final ObservableList<ColumnOrRow> seriesList;

	public Chart(String name) {
		this(new SimpleStringProperty(name), FXCollections.observableArrayList());
	}

	public Chart(SimpleStringProperty name, ObservableList<ColumnOrRow> seriesList) {
		this.name = name;
		this.seriesList = seriesList;
	}

	@Override public String toString() {
		return "Chart{" + "name=" + name + ", seriesList=" + seriesList + '}';
	}

	@Override public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		Chart chart = (Chart) o;
		return Objects.equals(getName(), chart.getName()) && Objects.equals(seriesList, chart.seriesList);
	}

	@Override public int hashCode() {
		return Objects.hash(name, seriesList);
	}

	public SimpleStringProperty nameProperty() {
		return name;
	}

	@Override public ObservableList<ColumnOrRow> getChildren() {
		return seriesList;
	}

	@Override public Node createNode() {
		return chartTypes.stream().filter(type -> type.getPredicate().test(this)).findAny().map(chartType -> chartType.getChart(this)).orElse(new Label("No suitable chart found"));
	}

	public Stream<ColumnOrRow.Column> getColumns() {
		return seriesList.stream().filter(series -> series.isColumn()).map(series -> (ColumnOrRow.Column) series);
	}

	public Stream<ColumnOrRow.Row> getRows() {
		return seriesList.stream().filter(series -> series.isRow()).map(series -> (ColumnOrRow.Row) series);
	}

}

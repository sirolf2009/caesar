package com.sirolf2009.caesar.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.component.hierarchy.IHierarchicalData;
import com.sirolf2009.caesar.model.chart.type.*;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.util.*;
import java.util.stream.Stream;

@DefaultSerializer(Chart.ChartSerializer.class) public class Chart implements IHierarchicalData<ColumnOrRow>, IDashboardNode {

	public static List<IChartType> chartTypes = new ArrayList(Arrays.asList(new LineChartType(), new BarChartType(), new TimeseriesChartType(), new PieChartType(), new GaugeChartType()));

	private final SimpleStringProperty name;
	private final ObservableList<ColumnOrRow> seriesList;
	private final ObjectProperty<IChartType> chartType;

	public Chart(String name) {
		this(new SimpleStringProperty(name), FXCollections.observableArrayList(), new SimpleObjectProperty<>(null));
	}

	public Chart(SimpleStringProperty name, ObservableList<ColumnOrRow> seriesList, ObjectProperty<IChartType> chartType) {
		this.name = name;
		this.seriesList = seriesList;
		this.chartType = chartType;
	}

	public Stream<IChartType> getPossibleChartTypes() {
		return chartTypes.stream().filter(type -> type.getPredicate().test(this));
	}

	@Override
	public String toString() {
		return "Chart{" +
				"name=" + name +
				", seriesList=" + seriesList +
				", chartType=" + chartType +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Chart chart = (Chart) o;
		return Objects.equals(getName(), chart.getName()) &&
				Objects.equals(seriesList, chart.seriesList) &&
				Objects.equals(getChartType(), chart.getChartType());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), seriesList, getChartType());
	}

	public SimpleStringProperty nameProperty() {
		return name;
	}

	public ObjectProperty<IChartType> chartTypeProperty() {
		return chartType;
	}

	public IChartType getChartType() {
		return chartType.get();
	}

	@Override public ObservableList<ColumnOrRow> getChildren() {
		return seriesList;
	}

	@Override public Node createNode() {
		return Optional.ofNullable(getChartType()).map(chartType -> chartType.getChart(this)).orElse(new Label("No suitable chart found"));
	}

	public Stream<ColumnOrRow.Column> getColumns() {
		return seriesList.stream().filter(series -> series.isColumn()).map(series -> (ColumnOrRow.Column) series);
	}

	public Stream<ColumnOrRow.Row> getRows() {
		return seriesList.stream().filter(series -> series.isRow()).map(series -> (ColumnOrRow.Row) series);
	}

	public static class ChartSerializer extends CaesarSerializer<Chart> {

		@Override public void write(Kryo kryo, Output output, Chart object) {
			output.writeString(object.getName());
			writeObservableListWithClass(kryo, output, object.getChildren());
			kryo.writeClassAndObject(output, object.getChartType());
		}

		@Override public Chart read(Kryo kryo, Input input, Class<Chart> type) {
			SimpleStringProperty name = readStringProperty(input);
			ObservableList<ColumnOrRow> series = readObservableListWithClass(kryo, input, ColumnOrRow.class);
			ObjectProperty<IChartType> chartType = readObjectAndClassProperty(kryo, input, IChartType.class);
			return new Chart(name, series, chartType);
		}
	}

}

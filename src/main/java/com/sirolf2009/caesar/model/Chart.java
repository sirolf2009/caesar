package com.sirolf2009.caesar.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.component.hierarchy.IHierarchicalData;
import com.sirolf2009.caesar.model.chart.type.*;
import com.sirolf2009.caesar.model.chart.type.GaugeChartType;
import com.sirolf2009.caesar.model.chart.type.xy.LineChartType;
import com.sirolf2009.caesar.model.chart.type.xy.TimeseriesChartType;
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

	public static List<IChartType> chartTypes = new ArrayList(Arrays.asList(new LineChartType(), new BarChartType(), new TimeseriesChartType(), new PieChartType(), new GaugeChartType(), new LEDChartType()));

	private final SimpleStringProperty name;
	private final ColumnOrRows seriesList;
	private final ObjectProperty<IChartTypeSetup> chartTypeSetup;

	public Chart(String name) {
		this(new SimpleStringProperty(name), new ColumnOrRows(FXCollections.observableArrayList()), new SimpleObjectProperty<>(null));
	}

	public Chart(SimpleStringProperty name, ColumnOrRows seriesList, ObjectProperty<IChartTypeSetup> chartTypeSetup) {
		this.name = name;
		this.seriesList = seriesList;
		this.chartTypeSetup = chartTypeSetup;
	}

	public Stream<IChartType> getPossibleChartTypes() {
		return chartTypes.stream().filter(type -> type.getPredicate().test(this));
	}

	@Override public String toString() {
		return "Chart{" + "name=" + name + ", seriesList=" + seriesList + ", chartTypeSetup=" + chartTypeSetup + '}';
	}

	@Override public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		Chart chart = (Chart) o;
		return Objects.equals(getName(), chart.getName()) && Objects.equals(seriesList, chart.seriesList) && Objects.equals(getChartTypeSetup(), chart.getChartTypeSetup());
	}

	@Override public int hashCode() {
		return Objects.hash(getName(), seriesList, getChartTypeSetup());
	}

	public SimpleStringProperty nameProperty() {
		return name;
	}

	public ObjectProperty<IChartTypeSetup> chartTypeSetupProperty() {
		return chartTypeSetup;
	}

	public IChartTypeSetup getChartTypeSetup() {
		return chartTypeSetup.get();
	}

	@Override public ObservableList<ColumnOrRow> getChildren() {
		return seriesList.getData();
	}

	public ColumnOrRows getSeriesList() {
		return seriesList;
	}

	@Override public Node createNode() {
		return Optional.ofNullable(getChartTypeSetup()).map(chartType -> chartType.createChart()).orElse(new Label("No suitable chart found"));
	}

	public Stream<ColumnOrRow.Column> getColumns() {
		return seriesList.getData().stream().filter(series -> series.isColumn()).map(series -> (ColumnOrRow.Column) series);
	}

	public Stream<ColumnOrRow.Row> getRows() {
		return seriesList.getData().stream().filter(series -> series.isRow()).map(series -> (ColumnOrRow.Row) series);
	}

	public static class ChartSerializer extends CaesarSerializer<Chart> {

		@Override public void write(Kryo kryo, Output output, Chart object) {
			output.writeString(object.getName());
			kryo.writeObject(output, object.getSeriesList());
			kryo.writeClassAndObject(output, object.getChartTypeSetup());
		}

		@Override public Chart read(Kryo kryo, Input input, Class<Chart> type) {
			SimpleStringProperty name = readStringProperty(input);
			ColumnOrRows series = kryo.readObject(input, ColumnOrRows.class);
			ObjectProperty<IChartTypeSetup> chartType = readObjectAndClassProperty(kryo, input, IChartTypeSetup.class);
			return new Chart(name, series, chartType);
		}
	}

}

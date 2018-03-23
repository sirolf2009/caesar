package com.sirolf2009.caesar.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;

@DefaultSerializer(CaesarModel.CaesarModelSerializer.class) public class CaesarModel {

	private final ObservableList<Table> tables;
	private final ObservableList<Chart> charts;
	private final ObservableList<Dashboard> dashboards;

	public CaesarModel() {
		this(FXCollections.observableArrayList(), FXCollections.observableArrayList(), FXCollections.observableArrayList());
	}

	public CaesarModel(ObservableList<Table> tables, ObservableList<Chart> charts, ObservableList<Dashboard> dashboards) {
		this.tables = tables;
		this.charts = charts;
		this.dashboards = dashboards;
	}

	@Override public String toString() {
		return "CaesarModel{" + "tables=" + tables + ", charts=" + charts + ", dashboards=" + dashboards + '}';
	}

	@Override public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		CaesarModel that = (CaesarModel) o;
		return Objects.equals(tables, that.tables) && Objects.equals(charts, that.charts) && Objects.equals(dashboards, that.dashboards);
	}

	@Override public int hashCode() {
		return Objects.hash(tables, charts, dashboards);
	}

	public ObservableList<Table> getTables() {
		return tables;
	}

	public ObservableList<Chart> getCharts() {
		return charts;
	}

	public ObservableList<Dashboard> getDashboards() {
		return dashboards;
	}

	public static class CaesarModelSerializer extends CaesarSerializer<CaesarModel> {

		@Override public void write(Kryo kryo, Output output, CaesarModel object) {
			writeObservableList(kryo, output, object.getTables());
			writeObservableList(kryo, output, object.getCharts());
			writeObservableList(kryo, output, object.getDashboards());
		}

		@Override public CaesarModel read(Kryo kryo, Input input, Class<CaesarModel> type) {
			ObservableList<Table> tables = readObservableList(kryo, input, Table.class);
			ObservableList<Chart> charts = readObservableList(kryo, input, Chart.class);
			ObservableList<Dashboard> dashboards = readObservableList(kryo, input, Dashboard.class);
			return new CaesarModel(tables, charts, dashboards);
		}
	}


}

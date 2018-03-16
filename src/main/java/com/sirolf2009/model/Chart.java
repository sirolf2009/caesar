package com.sirolf2009.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.component.hierarchy.IHierarchicalData;
import com.sirolf2009.model.chart.ISeries;
import com.sirolf2009.model.serializer.ChartSerializer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.Objects;

@DefaultSerializer(ChartSerializer.class)
public class Chart implements IHierarchicalData<ISeries>, Serializable {

    private final SimpleStringProperty name;
    private final ObservableList<ISeries> columnsList;
    private final ObservableList<ISeries> rowsList;
    private final ObservableList<ISeries> seriesList;

    public Chart(String name) {
        this(new SimpleStringProperty(name), FXCollections.observableArrayList(), FXCollections.observableArrayList());
    }

    public Chart(SimpleStringProperty name, ObservableList<ISeries> columnsList, ObservableList<ISeries> rowsList) {
        this.name = name;
        this.columnsList = columnsList;
        this.rowsList = rowsList;
        this.seriesList = FXCollections.concat(columnsList, rowsList);
    }

    @Override
    public String toString() {
        return "Chart{" +
                "name=" + name +
                ", columnsList=" + columnsList +
                ", rowsList=" + rowsList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chart chart = (Chart) o;
        return Objects.equals(getName(), chart.getName()) &&
                Objects.equals(columnsList, chart.columnsList) &&
                Objects.equals(rowsList, chart.rowsList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, columnsList, rowsList, seriesList);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    @Override
    public ObservableList<ISeries> getChildren() {
        return seriesList;
    }

    public ObservableList<ISeries> getColumnsList() {
        return columnsList;
    }

    public ObservableList<ISeries> getRowsList() {
        return rowsList;
    }
}

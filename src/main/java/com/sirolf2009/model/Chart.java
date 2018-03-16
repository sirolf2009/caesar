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

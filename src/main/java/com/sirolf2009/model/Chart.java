package com.sirolf2009.model;

import com.sirolf2009.component.hierarchy.IHierarchicalData;
import com.sirolf2009.model.chart.ISeries;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;

public class Chart implements IHierarchicalData<ISeries>, Serializable {

    private final SimpleStringProperty name;
    private final ObservableList<ISeries> columnsList = FXCollections.observableArrayList();
    private final ObservableList<ISeries> rowsList = FXCollections.observableArrayList();
    private final ObservableList<ISeries> seriesList = FXCollections.concat(columnsList, rowsList);

    public Chart(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public SimpleStringProperty nameProperty() {
        return name;
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

package com.sirolf2009.model;

import com.sirolf2009.component.hierarchy.IHierarchicalData;
import com.sirolf2009.model.chart.ISeries;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Chart implements IHierarchicalData<ISeries> {

    private final ObservableList<ISeries> columnsList = FXCollections.observableArrayList();
    private final ObservableList<ISeries> rowsList = FXCollections.observableArrayList();
    private final ObservableList<ISeries> seriesList = FXCollections.concat(columnsList, rowsList);

    public Chart() {

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

package com.sirolf2009.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;

public class CaesarModel implements Serializable {

    private final ObservableList<Table> tables = FXCollections.observableArrayList();
    private final ObservableList<Chart> charts = FXCollections.observableArrayList();

    public ObservableList<Table> getTables() {
        return tables;
    }

    public ObservableList<Chart> getCharts() {
        return charts;
    }
}

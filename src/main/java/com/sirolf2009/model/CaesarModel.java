package com.sirolf2009.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.model.serializer.CaesarModelSerializer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@DefaultSerializer(CaesarModelSerializer.class)
public class CaesarModel {

    private final ObservableList<Table> tables;
    private final ObservableList<Chart> charts;

    public CaesarModel() {
        this(FXCollections.observableArrayList(), FXCollections.observableArrayList());
    }

    public CaesarModel(ObservableList<Table> tables, ObservableList<Chart> charts) {
        this.tables = tables;
        this.charts = charts;
    }

    public ObservableList<Table> getTables() {
        return tables;
    }

    public ObservableList<Chart> getCharts() {
        return charts;
    }
}

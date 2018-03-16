package com.sirolf2009.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.model.serializer.CaesarModelSerializer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "CaesarModel{" +
                "tables=" + tables +
                ", charts=" + charts +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaesarModel that = (CaesarModel) o;
        return Objects.equals(tables, that.tables) &&
                Objects.equals(charts, that.charts);
    }

    @Override
    public int hashCode() {

        return Objects.hash(tables, charts);
    }

    public ObservableList<Table> getTables() {
        return tables;
    }

    public ObservableList<Chart> getCharts() {
        return charts;
    }
}

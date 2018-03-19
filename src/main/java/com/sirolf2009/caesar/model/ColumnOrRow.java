package com.sirolf2009.caesar.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.sirolf2009.caesar.component.hierarchy.IHierarchicalData;
import com.sirolf2009.caesar.model.serializer.ColumnSerializer;
import com.sirolf2009.caesar.model.serializer.RowSerializer;
import com.sirolf2009.caesar.model.chart.series.ISeries;
import javafx.collections.ObservableList;

import java.util.Objects;

public abstract class ColumnOrRow implements IHierarchicalData {

    private final ISeries series;
    private final boolean isColumn;

    public ColumnOrRow(ISeries series, boolean isColumn) {
        this.series = series;
        this.isColumn = isColumn;
    }

    public boolean isColumn() {
        return isColumn;
    }

    public boolean isRow() {
        return !isColumn;
    }

    @Override public String toString() {
        return series.toString();
    }

    @Override public ObservableList getChildren() {
        return series.getChildren();
    }

    @Override public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        ColumnOrRow that = (ColumnOrRow) o;
        return isColumn == that.isColumn && Objects.equals(series, that.series);
    }

    @Override public int hashCode() {

        return Objects.hash(series, isColumn);
    }

    public ISeries getSeries() {
        return series;
    }

    @DefaultSerializer(ColumnSerializer.class)
    public static class Column extends ColumnOrRow {
        public Column(ISeries series) {
            super(series, true);
        }
    }

    @DefaultSerializer(RowSerializer.class)
    public static class Row extends ColumnOrRow {
        public Row(ISeries series) {
            super(series, false);
        }
    }

}

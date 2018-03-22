package com.sirolf2009.caesar.model.chart.series;

import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.table.IDataPointer;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fxmisc.easybind.EasyBind;

import java.util.Arrays;
import java.util.Objects;

public abstract class ArraySeries<T> extends SimpleSeries<T[]> {

    public ArraySeries(Table table, IDataPointer attribute) {
        super(table, attribute);
    }

}

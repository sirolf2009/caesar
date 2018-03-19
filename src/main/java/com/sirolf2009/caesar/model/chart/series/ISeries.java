package com.sirolf2009.caesar.model.chart.series;

import com.sirolf2009.caesar.component.hierarchy.IHierarchicalData;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.table.IDataPointer;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;

public interface ISeries<T> extends IHierarchicalData, Serializable {

    ObservableList<T> get();
    String getName();
    StringProperty nameProperty();
    Table getTable();
    IDataPointer getAttribute();

    @Override
    default ObservableList getChildren() {
        return FXCollections.emptyObservableList();
    }
}

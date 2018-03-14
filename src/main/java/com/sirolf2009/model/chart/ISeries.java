package com.sirolf2009.model.chart;

import com.sirolf2009.component.hierarchy.IHierarchicalData;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public interface ISeries<T> extends IHierarchicalData {

    ObservableList<T> get();
    StringProperty getName();

    @Override
    default ObservableList getChildren() {
        return FXCollections.emptyObservableList();
    }
}

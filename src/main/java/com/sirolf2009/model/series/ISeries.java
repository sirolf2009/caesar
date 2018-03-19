package com.sirolf2009.model.series;

import com.sirolf2009.component.hierarchy.IHierarchicalData;
import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.Table;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;

public interface ISeries<T> extends IHierarchicalData, Serializable {

    ObservableList<T> get();
    String getName();
    StringProperty nameProperty();
    Table getTable();
    JMXAttribute getAttribute();

    @Override
    default ObservableList getChildren() {
        return FXCollections.emptyObservableList();
    }
}

package com.sirolf2009.caesar.model.table;

import com.sirolf2009.caesar.model.table.IDataPointer;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class SimplePointer implements IDataPointer {

    private final StringProperty name;

    public SimplePointer(StringProperty name) {
        this.name = name;
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public ObservableList getChildren() {
        return FXCollections.emptyObservableList();
    }
}

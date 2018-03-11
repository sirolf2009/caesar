package com.sirolf2009.component.hierarchy;

import javafx.collections.ObservableList;

public interface IHierarchicalData<T extends IHierarchicalData> {

    public ObservableList<T> getChildren();

}

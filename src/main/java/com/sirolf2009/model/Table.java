package com.sirolf2009.model;

import com.sirolf2009.component.TableTab;
import com.sirolf2009.component.hierarchy.IHierarchicalData;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.ObservableList;

public class Table implements IHierarchicalData<JMXAttribute> {

    private final ObservableStringValue name;
    private final TableTab tableTab;

    public Table(ObservableStringValue name, TableTab tableTab) {
        this.name = name;
        this.tableTab = tableTab;
    }

    @Override
    public ObservableList<JMXAttribute> getChildren() {
        return tableTab.getAttributes();
    }

    public ObservableList<JMXAttributes> getItems() {
        return tableTab.getTable().getItems();
    }

    public String getName() {
        return name.get();
    }

    @Override
    public String toString() {
        return name.get();
    }
}

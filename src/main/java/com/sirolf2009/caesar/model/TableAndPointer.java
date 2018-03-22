package com.sirolf2009.caesar.model;

import com.sirolf2009.caesar.model.table.IDataPointer;

public class TableAndPointer implements IDashboardNode {

    private final Table table;
    private final IDataPointer pointer;

    public TableAndPointer(Table table, IDataPointer pointer) {
        this.table = table;
        this.pointer = pointer;
    }

}

package com.sirolf2009.model;

public class ColumnOrRow {

    private final Table table;
    private final JMXAttribute attribute;

    public ColumnOrRow(Table table, JMXAttribute attribute) {
        this.table = table;
        this.attribute = attribute;
    }
}

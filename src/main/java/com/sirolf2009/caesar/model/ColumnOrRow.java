package com.sirolf2009.caesar.model;

import javafx.collections.ObservableList;
import org.fxmisc.easybind.EasyBind;

public class ColumnOrRow {

    private final Table table;
    private final JMXAttribute attribute;
    private final ObservableList<Number> numbers;

    public ColumnOrRow(Table table, JMXAttribute attribute) {
        this.table = table;
        this.attribute = attribute;

        numbers = EasyBind.map(table.getItems(), row -> {
            if(row.containsKey(attribute)) {
                return (Number) row.get(attribute);
            }
            return 0;
        });
    }

    public ObservableList<Number> getNumbers() {
        return numbers;
    }
}

package com.sirolf2009.model.chart;

import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.Table;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import org.fxmisc.easybind.EasyBind;

public abstract class SimpleNumberSeries<T extends Number> implements INumberSeries<T> {

    private final Table table;
    private final JMXAttribute attribute;
    private final ObservableList<T> numbers;
    private final StringProperty name;

    public SimpleNumberSeries(Table table, JMXAttribute attribute) {
        this.table = table;
        this.attribute = attribute;
        this.name = new SimpleStringProperty(attribute.getAttributeInfo().getName());

        numbers = EasyBind.map(table.getItems(), row -> {
            if(row.containsKey(attribute)) {
                return (T) row.get(attribute);
            }
            return getDefault();
        });
    }

    public abstract T getDefault();

    @Override
    public ObservableList get() {
        return numbers;
    }

    @Override
    public StringProperty getName() {
        return name;
    }

    public Table getTable() {
        return table;
    }

    public JMXAttribute getAttribute() {
        return attribute;
    }

}

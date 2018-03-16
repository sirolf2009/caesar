package com.sirolf2009.model.chart;

import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.Table;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import org.fxmisc.easybind.EasyBind;

import java.util.Objects;

public abstract class SimpleSeries<T> {

    private final Table table;
    private final JMXAttribute attribute;
    private final ObservableList<T> values;
    private final StringProperty name;

    public SimpleSeries(Table table, JMXAttribute attribute) {
        this.table = table;
        this.attribute = attribute;
        this.name = new SimpleStringProperty(attribute.getAttributeInfo().getName());

        values = EasyBind.map(table.getItems(), row -> {
            if(row.containsKey(attribute)) {
                return (T) row.get(attribute);
            }
            return getDefault();
        });
    }

    @Override
    public String toString() {
        return "SimpleSeries{" +
                "table=" + table +
                ", attribute=" + attribute +
                ", values=" + values +
                ", name=" + name +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleSeries<?> that = (SimpleSeries<?>) o;
        return Objects.equals(table, that.table) &&
                Objects.equals(attribute, that.attribute) &&
                Objects.equals(values, that.values) &&
                Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, attribute, values, name);
    }

    public abstract T getDefault();

    public ObservableList get() {
        return values;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public Table getTable() {
        return table;
    }

    public JMXAttribute getAttribute() {
        return attribute;
    }
}

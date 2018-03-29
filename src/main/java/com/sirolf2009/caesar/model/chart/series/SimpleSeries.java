package com.sirolf2009.caesar.model.chart.series;

import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.table.IDataPointer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import org.fxmisc.easybind.EasyBind;

import java.util.Objects;
import java.util.Optional;

public abstract class SimpleSeries<T> {

    private final Table table;
    private final IDataPointer attribute;
    private final ObservableList<T> values;
    private final StringProperty name;

    public SimpleSeries(Table table, IDataPointer attribute) {
        this.table = table;
        this.attribute = attribute;
        this.name = new SimpleStringProperty(attribute.toString());
        values = EasyBind.map(table.getItems(), row -> (T)row.get(attribute));
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

    public IDataPointer getAttribute() {
        return attribute;
    }
}

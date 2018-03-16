package com.sirolf2009.model.chart;

import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.Table;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.ObservableList;
import org.fxmisc.easybind.EasyBind;

public abstract class SimpleCategorySeries<T> extends SimpleSeries<T> implements ICategorySeries<T> {

    public SimpleCategorySeries(Table table, JMXAttribute attribute) {
        super(table, attribute);
    }

}

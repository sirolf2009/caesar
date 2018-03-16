package com.sirolf2009.model.chart;

import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.Table;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import org.fxmisc.easybind.EasyBind;

public abstract class SimpleNumberSeries<T extends Number> extends SimpleSeries<T> implements INumberSeries<T> {

    public SimpleNumberSeries(Table table, JMXAttribute attribute) {
        super(table, attribute);
    }

}

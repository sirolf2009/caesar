package com.sirolf2009.caesar.model.chart.series;

import com.sirolf2009.caesar.model.JMXAttribute;
import com.sirolf2009.caesar.model.Table;

public abstract class SimpleNumberSeries<T extends Number> extends SimpleSeries<T> implements INumberSeries<T> {

    public SimpleNumberSeries(Table table, JMXAttribute attribute) {
        super(table, attribute);
    }

}

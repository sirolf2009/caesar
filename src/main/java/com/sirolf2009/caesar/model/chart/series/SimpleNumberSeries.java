package com.sirolf2009.caesar.model.chart.series;

import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.model.Table;

public abstract class SimpleNumberSeries<T extends Number> extends SimpleSeries<T> implements INumberSeries<T> {

    public SimpleNumberSeries(Table table, IDataPointer attribute) {
        super(table, attribute);
    }

}

package com.sirolf2009.caesar.model.chart.series;

import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.model.Table;

public abstract class SimpleCategorySeries<T> extends SimpleSeries<T> implements ICategorySeries<T> {

    public SimpleCategorySeries(Table table, IDataPointer attribute) {
        super(table, attribute);
    }

}

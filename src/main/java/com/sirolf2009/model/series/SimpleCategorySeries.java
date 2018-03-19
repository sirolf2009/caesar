package com.sirolf2009.model.series;

import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.Table;

public abstract class SimpleCategorySeries<T> extends SimpleSeries<T> implements ICategorySeries<T> {

    public SimpleCategorySeries(Table table, JMXAttribute attribute) {
        super(table, attribute);
    }

}

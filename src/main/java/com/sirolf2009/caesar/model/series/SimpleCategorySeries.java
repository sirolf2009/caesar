package com.sirolf2009.caesar.model.series;

import com.sirolf2009.caesar.model.JMXAttribute;
import com.sirolf2009.caesar.model.Table;

public abstract class SimpleCategorySeries<T> extends SimpleSeries<T> implements ICategorySeries<T> {

    public SimpleCategorySeries(Table table, JMXAttribute attribute) {
        super(table, attribute);
    }

}

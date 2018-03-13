package com.sirolf2009.model.chart;

import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.Table;

public class BooleanSeries extends SimpleCategorySeries<Boolean> {

    public BooleanSeries(Table table, JMXAttribute attribute) {
        super(table, attribute);
    }

    @Override
    public Boolean getDefault() {
        return false;
    }
}

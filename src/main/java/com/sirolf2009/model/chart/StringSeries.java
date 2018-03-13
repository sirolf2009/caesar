package com.sirolf2009.model.chart;

import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.Table;

public class StringSeries extends SimpleCategorySeries<String> {

    public StringSeries(Table table, JMXAttribute attribute) {
        super(table, attribute);
    }

    @Override
    public String getDefault() {
        return "";
    }
}

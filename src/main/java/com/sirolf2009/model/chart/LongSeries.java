package com.sirolf2009.model.chart;

import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.Table;

public class LongSeries extends SimpleNumberSeries<Long> {

    public LongSeries(Table table, JMXAttribute attribute) {
        super(table, attribute);
    }

    @Override
    public Long getDefault() {
        return 0l;
    }
}

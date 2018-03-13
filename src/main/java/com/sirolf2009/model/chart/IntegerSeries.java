package com.sirolf2009.model.chart;

import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.Table;
import javafx.collections.ObservableList;
import org.fxmisc.easybind.EasyBind;

public class IntegerSeries extends SimpleNumberSeries<Integer> {

    public IntegerSeries(Table table, JMXAttribute attribute) {
        super(table, attribute);
    }

    @Override
    public Integer getDefault() {
        return 0;
    }
}

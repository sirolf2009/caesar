package com.sirolf2009.model.chart;

import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.Table;
import javafx.collections.ObservableList;
import org.fxmisc.easybind.EasyBind;

public class DoubleSeries extends SimpleNumberSeries<Double> {

    public DoubleSeries(Table table, JMXAttribute attribute) {
        super(table, attribute);
    }

    @Override
    public Double getDefault() {
        return 0d;
    }
}

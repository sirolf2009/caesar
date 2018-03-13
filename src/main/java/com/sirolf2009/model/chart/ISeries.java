package com.sirolf2009.model.chart;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public interface ISeries<T> {

    ObservableList<T> get();
    StringProperty getName();

}

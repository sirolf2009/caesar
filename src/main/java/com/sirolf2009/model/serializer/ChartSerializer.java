package com.sirolf2009.model.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.model.Chart;
import com.sirolf2009.model.series.ISeries;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

public class ChartSerializer extends CaesarSerializer<Chart> {

	@Override public void write(Kryo kryo, Output output, Chart object) {
		output.writeString(object.getName());
		writeObservableListWithClass(kryo, output, object.getColumnsList());
		writeObservableListWithClass(kryo, output, object.getRowsList());
	}

	@Override public Chart read(Kryo kryo, Input input, Class<Chart> type) {
		SimpleStringProperty name = readStringProperty(input);
		ObservableList<ISeries> columns = readObservableListWithClass(kryo, input, ISeries.class);
		ObservableList<ISeries> rows = readObservableListWithClass(kryo, input, ISeries.class);
		return new Chart(name, columns, rows);
	}
}

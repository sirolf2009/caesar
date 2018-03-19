package com.sirolf2009.caesar.model.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.ColumnOrRow;
import com.sirolf2009.caesar.model.chart.series.ISeries;

public class ColumnSerializer extends Serializer<ColumnOrRow.Column> {

	@Override public void write(Kryo kryo, Output output, ColumnOrRow.Column object) {
		kryo.writeClassAndObject(output, object.getSeries());
	}

	@Override public ColumnOrRow.Column read(Kryo kryo, Input input, Class<ColumnOrRow.Column> type) {
		return new ColumnOrRow.Column((ISeries) kryo.readClassAndObject(input));
	}
}

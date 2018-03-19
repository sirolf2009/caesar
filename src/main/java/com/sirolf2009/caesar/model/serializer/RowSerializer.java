package com.sirolf2009.caesar.model.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.ColumnOrRow;
import com.sirolf2009.caesar.model.series.ISeries;

public class RowSerializer extends Serializer<ColumnOrRow.Row> {

	@Override public void write(Kryo kryo, Output output, ColumnOrRow.Row object) {
		kryo.writeClassAndObject(output, object.getSeries());
	}

	@Override public ColumnOrRow.Row read(Kryo kryo, Input input, Class<ColumnOrRow.Row> type) {
		return new ColumnOrRow.Row((ISeries) kryo.readClassAndObject(input));
	}
}

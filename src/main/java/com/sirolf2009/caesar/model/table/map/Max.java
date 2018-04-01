package com.sirolf2009.caesar.model.table.map;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.JMXAttributes;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.SimplePointer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.management.MBeanServerConnection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

@DefaultSerializer(Max.MaxSerializer.class)
public class Max extends SimplePointer {

	private final IDataPointer pointer;
	private final Function<Object, Object> maxFunction;

	public Max(IDataPointer pointer) {
		this(new SimpleStringProperty(pointer.getName()+" Max"), pointer);
	}

	public Max(StringProperty name, IDataPointer pointer) {
		super(name);
		this.pointer = pointer;
		this.maxFunction = getMaxFunction(pointer);
	}

	@Override
	public void pullData(MBeanServerConnection connection, JMXAttributes attributes) {
		attributes.put(this, maxFunction.apply(attributes.get(pointer)));
	}

	public Function<Object, Object> getMaxFunction(IDataPointer pointer) {
		switch(pointer.getType()) {
			case "[Ljava.lang.Integer;":
				return object -> Arrays.stream((Integer[])object).max(Comparator.naturalOrder()).orElse(null);
			case "[Ljava.lang.Double;":
				return object -> Arrays.stream((Double[])object).max(Comparator.naturalOrder()).orElse(null);
			case "[Ljava.lang.Long;":
				return object -> Arrays.stream((Long[])object).max(Comparator.naturalOrder()).orElse(null);
			case "[Ljava.lang.Float;":
				return object -> Arrays.stream((Long[])object).max(Comparator.naturalOrder()).orElse(null);
		}
		throw new IllegalArgumentException("Unknown type: "+pointer.getType());
	}

	@Override public String getType() {
		return pointer.getType().substring(2, pointer.getType().length()-1);
	}

	public IDataPointer getPointer() {
		return pointer;
	}

	public static class MaxSerializer extends CaesarSerializer<Max> {

		@Override public void write(Kryo kryo, Output output, Max object) {
			output.writeString(object.getName());
			kryo.writeClassAndObject(output, object.getPointer());
		}

		@Override public Max read(Kryo kryo, Input input, Class<Max> type) {
			return new Max(readStringProperty(input), (IDataPointer) kryo.readClassAndObject(input));
		}
	}

}

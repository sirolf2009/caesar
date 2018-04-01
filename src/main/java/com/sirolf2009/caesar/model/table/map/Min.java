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

@DefaultSerializer(Min.MinSerializer.class)
public class Min extends SimplePointer {

	private final IDataPointer pointer;
	private final Function<Object, Object> minFunction;

	public Min(IDataPointer pointer) {
		this(new SimpleStringProperty(pointer.getName()+" Min"), pointer);
	}

	public Min(StringProperty name, IDataPointer pointer) {
		super(name);
		this.pointer = pointer;
		this.minFunction = getMinFunction(pointer);
	}

	@Override
	public void pullData(MBeanServerConnection connection, JMXAttributes attributes) {
		attributes.put(this, minFunction.apply(attributes.get(pointer)));
	}

	public Function<Object, Object> getMinFunction(IDataPointer pointer) {
		switch(pointer.getType()) {
			case "[Ljava.lang.Integer;":
				return object -> Arrays.stream((Integer[])object).min(Comparator.naturalOrder()).orElse(null);
			case "[Ljava.lang.Double;":
				return object -> Arrays.stream((Double[])object).min(Comparator.naturalOrder()).orElse(null);
			case "[Ljava.lang.Long;":
				return object -> Arrays.stream((Long[])object).min(Comparator.naturalOrder()).orElse(null);
			case "[Ljava.lang.Float;":
				return object -> Arrays.stream((Long[])object).min(Comparator.naturalOrder()).orElse(null);
		}
		throw new IllegalArgumentException("Unknown type: "+pointer.getType());
	}

	@Override public String getType() {
		return pointer.getType().substring(2, pointer.getType().length()-1);
	}

	public IDataPointer getPointer() {
		return pointer;
	}

	public static class MinSerializer extends CaesarSerializer<Min> {

		@Override public void write(Kryo kryo, Output output, Min object) {
			output.writeString(object.getName());
			kryo.writeClassAndObject(output, object.getPointer());
		}

		@Override public Min read(Kryo kryo, Input input, Class<Min> type) {
			return new Min(readStringProperty(input), (IDataPointer) kryo.readClassAndObject(input));
		}
	}

}

package com.sirolf2009.caesar.model.table.map;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.JMXAttributes;
import com.sirolf2009.caesar.model.chart.series.*;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import com.sirolf2009.caesar.model.table.IDataPointer;
import com.sirolf2009.caesar.model.table.SimplePointer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.management.MBeanServerConnection;
import java.util.Date;
import java.util.function.Function;

@DefaultSerializer(Abs.AbsSerializer.class)
public class Abs extends SimplePointer {

	private final IDataPointer pointer;
	private final Function<Object, Object> absFunction;

	public Abs(IDataPointer pointer) {
		this(new SimpleStringProperty(pointer.getName()+" ABS"), pointer);
	}

	public Abs(StringProperty name, IDataPointer pointer) {
		super(name);
		this.pointer = pointer;
		this.absFunction = getAbsFunction(pointer);
	}

	@Override
	public void pullData(MBeanServerConnection connection, JMXAttributes attributes) {
		attributes.put(this, absFunction.apply(attributes.get(pointer)));
	}

	public Function<Object, Object> getAbsFunction(IDataPointer pointer) {
		switch(pointer.getType()) {
			case "int":
				return object -> Math.abs((Integer)object);
			case "java.lang.Integer":
				return object -> Math.abs((Integer)object);
			case "long":
				return object -> Math.abs((Long)object);
			case "java.lang.Long":
				return object -> Math.abs((Long)object);
			case "double":
				return object -> Math.abs((Double)object);
			case "java.lang.Double":
				return object -> Math.abs((Double)object);
			case "float":
				return object -> Math.abs((Float)object);
			case "java.lang.Float":
				return object -> Math.abs((Float)object);
		}
		throw new IllegalArgumentException("Unknown type: "+pointer.getType());
	}

	@Override public String getType() {
		return pointer.getType();
	}

	public IDataPointer getPointer() {
		return pointer;
	}

	public static class AbsSerializer extends CaesarSerializer<Abs> {

		@Override public void write(Kryo kryo, Output output, Abs object) {
			output.writeString(object.getName());
			kryo.writeClassAndObject(output, object.getPointer());
		}

		@Override public Abs read(Kryo kryo, Input input, Class<Abs> type) {
			return new Abs(readStringProperty(input), (IDataPointer) kryo.readClassAndObject(input));
		}
	}

}

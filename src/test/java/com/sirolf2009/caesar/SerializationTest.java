package com.sirolf2009.caesar;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.serializer.CaesarKryo;
import org.junit.Assert;

public class SerializationTest {

	public static void testCloning(Object object, Class<?> objectClass) {
		CaesarKryo kryo = new CaesarKryo();

		Output output = new ByteBufferOutput(1024);
		kryo.writeObject(output, object);
		byte[] serialized = output.toBytes();
		output.close();

		Input input = new ByteBufferInput(serialized);
		Object retrieved = kryo.readObject(input, objectClass);
		input.close();

		Assert.assertEquals(object, retrieved);
	}
}

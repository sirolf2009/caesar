package com.sirolf2009.caesar.model;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.model.CaesarModel;
import com.sirolf2009.model.Chart;
import com.sirolf2009.model.JMXAttribute;
import com.sirolf2009.model.Table;
import com.sirolf2009.model.chart.IntegerSeries;
import org.junit.Test;

import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class CaesarModelTest {

	@Test
	public void test() throws IntrospectionException, MalformedObjectNameException, FileNotFoundException {
		CaesarModel model = new CaesarModel();

		JMXAttribute attribute1 = new JMXAttribute(new ObjectName("com.sirolf2009.test:Type=Unexisting1"), new MBeanAttributeInfo("name1", "int", "desc1", false, false, false));
		JMXAttribute attribute2 = new JMXAttribute(new ObjectName("com.sirolf2009.test:Type=Unexisting2"), new MBeanAttributeInfo("name1", "int", "desc1", false, false, false));
		Table table = new Table("table");
		table.getChildren().add(attribute1);
		table.getChildren().add(attribute2);
		model.getTables().add(table);

		Chart chart = new Chart("chart");
		chart.getColumnsList().add(new IntegerSeries(table, attribute1));
		chart.getColumnsList().add(new IntegerSeries(table, attribute2));
		model.getCharts().add(chart);

		Kryo kryo = new Kryo();

		Output output = new Output(new FileOutputStream("file.bin"));
		kryo.writeObject(output, model);
		output.close();

		Input input = new Input(new FileInputStream("file.bin"));
		CaesarModel retrievedModel = kryo.readObject(input, CaesarModel.class);
		input.close();

		System.out.println(retrievedModel);
	}

}

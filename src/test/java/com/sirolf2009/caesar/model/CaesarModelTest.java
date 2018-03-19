package com.sirolf2009.caesar.model;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.chart.series.IntegerSeries;
import org.junit.Assert;
import org.junit.Test;

import javax.management.MBeanAttributeInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class CaesarModelTest {

	@Test
	public void test() throws MalformedObjectNameException, FileNotFoundException {
		CaesarModel model = new CaesarModel();

		JMXAttribute attribute1 = new JMXAttribute(new ObjectName("com.sirolf2009.test:Type=Unexisting1"), new MBeanAttributeInfo("name1", "int", "desc1", false, false, false));
		JMXAttribute attribute2 = new JMXAttribute(new ObjectName("com.sirolf2009.test:Type=Unexisting2"), new MBeanAttributeInfo("name1", "int", "desc1", false, false, false));
		Table table = new Table("table");
		table.getChildren().add(attribute1);
		table.getChildren().add(attribute2);
		model.getTables().add(table);

		Chart chart = new Chart("series");
		chart.getChildren().add(new ColumnOrRow.Column(new IntegerSeries(table, attribute1)));
		chart.getChildren().add(new ColumnOrRow.Row(new IntegerSeries(table, attribute2)));
		model.getCharts().add(chart);

		Kryo kryo = new Kryo();

		Output output = new Output(new FileOutputStream("src/test/resources/CaesarModelTest.bin"));
		kryo.writeObject(output, model);
		output.close();

		Input input = new Input(new FileInputStream("src/test/resources/CaesarModelTest.bin"));
		CaesarModel retrievedModel = kryo.readObject(input, CaesarModel.class);
		input.close();

		Assert.assertEquals(model.getTables().get(0).getChildren(), retrievedModel.getTables().get(0).getChildren());
		Assert.assertEquals(model.getTables().get(0), retrievedModel.getTables().get(0));
		Assert.assertEquals(model.getTables(), retrievedModel.getTables());
		Assert.assertEquals(model.getCharts().get(0).getChildren(), retrievedModel.getCharts().get(0).getChildren());
		Assert.assertEquals(model.getCharts().get(0), retrievedModel.getCharts().get(0));
		Assert.assertEquals(model.getCharts(), retrievedModel.getCharts());
		Assert.assertEquals(model, retrievedModel);
	}

}

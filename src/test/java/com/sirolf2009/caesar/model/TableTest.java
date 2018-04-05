package com.sirolf2009.caesar.model;

import com.sirolf2009.caesar.SerializationTest;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import org.junit.Test;

import javax.management.MBeanAttributeInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class TableTest {

	@Test
	public void testSerialization() throws MalformedObjectNameException {
		JMXAttribute attribute1 = new JMXAttribute(new ObjectName("com.sirolf2009.test:Type=Unexisting1"), new MBeanAttributeInfo("name1", "int", "desc1", false, false, false));
		JMXAttribute attribute2 = new JMXAttribute(new ObjectName("com.sirolf2009.test:Type=Unexisting2"), new MBeanAttributeInfo("name1", "int", "desc1", false, false, false));
		Table table = new Table("table");
		table.getChildren().add(attribute1);
		table.getChildren().add(attribute2);
		SerializationTest.testCloning(table, Table.class);
	}

}

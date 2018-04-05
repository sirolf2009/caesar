package com.sirolf2009.caesar.model;

import com.sirolf2009.caesar.SerializationTest;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import org.junit.Test;

import javax.management.MBeanAttributeInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class JMXAttributeTest {

	@Test
	public void testSerialization() throws MalformedObjectNameException {
		JMXAttribute attribute = new JMXAttribute(new ObjectName("com.sirolf2009.caesar:Type=doesnotexist"), new MBeanAttributeInfo("name", "type", "desc", false, false, false));
		SerializationTest.testCloning(attribute, JMXAttribute.class);
	}

}

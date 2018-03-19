package com.sirolf2009.caesar.model.table;

import com.sirolf2009.caesar.component.hierarchy.IHierarchicalData;
import com.sirolf2009.caesar.model.JMXAttributes;

import javax.management.MBeanServerConnection;

public interface IDataPointer extends IHierarchicalData {

	void pullData(MBeanServerConnection connection, JMXAttributes attributes);
	String getType();

}

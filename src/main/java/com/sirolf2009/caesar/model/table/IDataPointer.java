package com.sirolf2009.caesar.model.table;

import com.sirolf2009.caesar.component.hierarchy.IHierarchicalData;
import com.sirolf2009.caesar.model.JMXAttributes;
import javafx.beans.property.StringProperty;

import javax.management.*;
import java.io.IOException;

public interface IDataPointer extends IHierarchicalData {

	void pullData(MBeanServerConnection connection, JMXAttributes attributes) throws Exception;
	String getType();
	StringProperty nameProperty();

	default String getName() {
		return nameProperty().get();
	}

}

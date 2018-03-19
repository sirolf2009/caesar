package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.ColumnOrRow;
import com.sirolf2009.caesar.model.JMXAttribute;
import com.sirolf2009.caesar.model.Table;
import com.sirolf2009.caesar.model.series.DoubleSeries;
import com.sirolf2009.caesar.model.series.StringSeries;

import javax.management.MBeanAttributeInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class ChartTypeTest {

	public ColumnOrRow.Column doubleColumn() {
		return new ColumnOrRow.Column(doubleSeries());
	}

	public ColumnOrRow.Row doubleRow() {
		return new ColumnOrRow.Row(doubleSeries());
	}

	public ColumnOrRow.Column stringColumn() {
		return new ColumnOrRow.Column(stringSeries());
	}

	public ColumnOrRow.Row stringRow() {
		return new ColumnOrRow.Row(stringSeries());
	}

	public DoubleSeries doubleSeries() {
		return new DoubleSeries(new Table("test-table"), mockJMXAttribute());
	}

	public StringSeries stringSeries() {
		return new StringSeries(new Table("test-table"), mockJMXAttribute());
	}

	public JMXAttribute mockJMXAttribute() {
		return new JMXAttribute(mockObjectName(), mockMBeanAttribute());
	}

	public MBeanAttributeInfo mockMBeanAttribute() {
		return new MBeanAttributeInfo("name", "int", "desc", false, false, false);
	}

	public ObjectName mockObjectName() {
		try {
			return new ObjectName("com.sirolf2009.caesar:Type=Test");
		} catch(MalformedObjectNameException e) {
			throw new RuntimeException(e);
		}
	}

}

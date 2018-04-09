package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.ColumnOrRow;
import com.sirolf2009.caesar.model.chart.series.*;
import com.sirolf2009.caesar.model.table.JMXAttribute;
import com.sirolf2009.caesar.model.Table;

import javax.management.MBeanAttributeInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class ChartTypeTest {

	public ColumnOrRow.Column dateColumn() {
		return new ColumnOrRow.Column(dateSeries());
	}

	public ColumnOrRow.Row dateRow() {
		return new ColumnOrRow.Row(dateSeries());
	}

	public ColumnOrRow.Column longColumn() {
		return new ColumnOrRow.Column(longSeries());
	}

	public ColumnOrRow.Row longRow() {
		return new ColumnOrRow.Row(longSeries());
	}

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

	public ColumnOrRow.Column booleanColumn() {
		return new ColumnOrRow.Column(booleanSeries());
	}

	public ColumnOrRow.Row booleanRow() {
		return new ColumnOrRow.Row(booleanSeries());
	}

	public DateSeries dateSeries() {
		return new DateSeries(new Table("test-table"), mockJMXAttribute());
	}

	public LongSeries longSeries() {
		return new LongSeries(new Table("test-table"), mockJMXAttribute());
	}

	public DoubleSeries doubleSeries() {
		return new DoubleSeries(new Table("test-table"), mockJMXAttribute());
	}

	public StringSeries stringSeries() {
		return new StringSeries(new Table("test-table"), mockJMXAttribute());
	}

	public BooleanSeries booleanSeries() {
		return new BooleanSeries(new Table("test-table"), mockJMXAttribute());
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

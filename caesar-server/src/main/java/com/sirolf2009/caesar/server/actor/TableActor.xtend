package com.sirolf2009.caesar.server.actor

import akka.actor.AbstractActor
import com.sirolf2009.caesar.annotations.JMXBean
import com.sirolf2009.caesar.annotations.Match
import com.sirolf2009.caesar.server.model.Attribute
import com.sirolf2009.caesar.server.model.NewValues
import java.util.HashMap
import java.util.Map
import tech.tablesaw.api.Table
import tech.tablesaw.columns.Column
import tech.tablesaw.api.BooleanColumn
import tech.tablesaw.api.CategoryColumn
import tech.tablesaw.api.DateColumn
import java.util.Date
import tech.tablesaw.api.DateTimeColumn
import tech.tablesaw.api.DoubleColumn
import tech.tablesaw.api.FloatColumn
import tech.tablesaw.api.IntColumn
import tech.tablesaw.api.LongColumn
import tech.tablesaw.api.ShortColumn
import tech.tablesaw.api.TimeColumn
import java.time.LocalTime
import org.eclipse.xtend.lib.annotations.Data

@JMXBean class TableActor extends AbstractActor {
		
	val Table table
	val Map<Attribute, Column> columnMap
	
	new(String name) {
		this(Table.create(name))
	}
	
	new(Table table) {
		this.table = table
		columnMap = new HashMap()
	}
	
	@Match def void onNewValues(NewValues it) {
		values.forEach[attribute,value|
			columnMap.get(attribute).append(value)
		]
		table.write.csv(System.out)
	}
	
	@Match def void addColumn(Column it) {
		table.addColumn(it)
	}
	
	@Match def void addColumn(AddColumn it) {
		columnMap.put(attribute, column)
		table.addColumn(column)
	}
	
	def static void append(Column column, Object object) {
		if(column instanceof BooleanColumn) {
			if(object instanceof Boolean) {
				column.append(object)
			} else {
				throw new IllegalArgumentException('''The value «object» cannot be added to the boolean column «column»''')
			}
		} else if(column instanceof CategoryColumn) {
			if(object instanceof String) {
				column.append(object)
			} else {
				throw new IllegalArgumentException('''The value «object» cannot be added to the category column «column»''')
			}
		} else if(column instanceof DateColumn) {
			if(object instanceof Date) {
				column.append(object)
			} else {
				throw new IllegalArgumentException('''The value «object» cannot be added to the date column «column»''')
			}
		} else if(column instanceof DateTimeColumn) {
			if(object instanceof Date) {
				column.append(object)
			} else {
				throw new IllegalArgumentException('''The value «object» cannot be added to the datetime column «column»''')
			}
		} else if(column instanceof DoubleColumn) {
			if(object instanceof Double) {
				column.append(object)
			} else {
				throw new IllegalArgumentException('''The value «object» cannot be added to the double column «column»''')
			}
		} else if(column instanceof FloatColumn) {
			if(object instanceof Float) {
				column.append(object)
			} else {
				throw new IllegalArgumentException('''The value «object» cannot be added to the float column «column»''')
			}
		} else if(column instanceof IntColumn) {
			if(object instanceof Integer) {
				column.append(object)
			} else {
				throw new IllegalArgumentException('''The value «object» cannot be added to the int column «column»''')
			}
		} else if(column instanceof LongColumn) {
			if(object instanceof Long) {
				column.append(object)
			} else {
				throw new IllegalArgumentException('''The value «object» cannot be added to the long column «column»''')
			}
		} else if(column instanceof ShortColumn) {
			if(object instanceof Short) {
				column.append(object)
			} else {
				throw new IllegalArgumentException('''The value «object» cannot be added to the short column «column»''')
			}
		} else if(column instanceof TimeColumn) {
			if(object instanceof LocalTime) {
				column.append(object)
			} else {
				throw new IllegalArgumentException('''The value «object» cannot be added to the time column «column»''')
			}
		} else {
			throw new IllegalArgumentException('''Unknown column type «column»''')
		}
	}
	
	@Data static class AddColumn {
		val Attribute attribute
		val Column column
	}
	
}
package com.sirolf2009.caesar.server.actor

import akka.actor.AbstractActor
import akka.actor.ActorRef
import com.sirolf2009.caesar.annotations.Expose
import com.sirolf2009.caesar.annotations.JMXBean
import com.sirolf2009.caesar.annotations.Match
import com.sirolf2009.caesar.server.model.Attribute
import com.sirolf2009.caesar.server.model.NewValues
import java.io.StringWriter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.ArrayList
import java.util.Date
import java.util.HashMap
import java.util.List
import java.util.Map
import java.util.function.Supplier
import org.eclipse.xtend.lib.annotations.Data
import tech.tablesaw.api.BooleanColumn
import tech.tablesaw.api.CategoryColumn
import tech.tablesaw.api.DateColumn
import tech.tablesaw.api.DateTimeColumn
import tech.tablesaw.api.DoubleColumn
import tech.tablesaw.api.FloatColumn
import tech.tablesaw.api.IntColumn
import tech.tablesaw.api.LongColumn
import tech.tablesaw.api.ShortColumn
import tech.tablesaw.api.Table
import tech.tablesaw.api.TimeColumn
import tech.tablesaw.columns.Column

@JMXBean class TableActor extends AbstractActor {
		
	val String name
	val Map<Attribute, Column> columnMap
	val List<Supplier<Column>> mappingColumns
	val List<ActorRef> subscribtions
	
	new(String name) {
		this.name = name
		registerAs("com.sirolf2009.caesar:type=TableActor,table="+name)
		columnMap = new HashMap()
		mappingColumns = new ArrayList()
		subscribtions = new ArrayList()
	}
	
	@Match def void onNewValues(NewValues it) {
		values.forEach[attribute,value|
			columnMap.get(attribute).append(value)
		]
		val table = getTable()
		subscribtions.forEach[tell(table, self())]
	}
	
	@Match def void addColumn(AddColumn it) {
		columnMap.put(attribute, column)
	}
	
	@Match def void addMappingColumn(AddMappingColumn it) {
		mappingColumns.add(supplier)
	}
	
	@Match def void getTable(GetTable get) {
		sender().tell(getTable(), self())
	}
	
	@Match def void onSubscribe(Subscribe subscribe) {
		subscribtions.add(sender())
	}
	
	@Match def void onUnsubscribe(Unsubscribe subscribe) {
		subscribtions.remove(sender())
	}
	
	@Expose override String csvTable() {
		val writer = new StringWriter()
		table.write.csv(writer)
		writer.close()
		return writer.buffer.toString
	}
	
	def getTable() {
		return Table.create(name, columnMap.values) => [
			mappingColumns.forEach[col| addColumn(col.get())]
		]
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
				column.append(object.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
			} else if(object instanceof LocalDate) {
				column.append(object)
			} else {
				throw new IllegalArgumentException('''The value «object» cannot be added to the date column «column»''')
			}
		} else if(column instanceof DateTimeColumn) {
			if(object instanceof Date) {
				column.append(object.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
			} else if(object instanceof LocalDateTime) {
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
	
	@Data static class Subscribe {
	}
	@Data static class Unsubscribe {
	}
	@Data static class GetTable {
	}
	@Data static class AddColumn {
		val Attribute attribute
		val Column column
	}
	@Data static class AddMappingColumn {
		val Supplier<Column> supplier
	}
	
}
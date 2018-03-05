package com.sirolf2009.caesar.server.actor.jmx

import akka.actor.AbstractActor
import akka.actor.ActorRef
import com.sirolf2009.caesar.annotations.JMXBean
import com.sirolf2009.caesar.annotations.Match
import java.util.Set
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import javax.management.QueryExp
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor
import javax.management.ObjectInstance

@JMXBean @FinalFieldsConstructor class JMXConnectionActor extends AbstractActor {
	
	val MBeanServerConnection connection
	
	@Match def void onQueryBeans(QueryBeans it) {
		target.tell(new ObjectInstances(connection.queryMBeans(objectName, queryExpression)), self())
	}
	
	@Data static class QueryBeans {
		val ActorRef target
		val ObjectName objectName
		val QueryExp queryExpression 
	}
	@Data static class ObjectInstances {
		Set<ObjectInstance> objectInstances
	}
	
}
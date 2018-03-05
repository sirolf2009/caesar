package com.sirolf2009.caesar.server.actor.jmx

import akka.actor.AbstractActor
import akka.actor.ActorRef
import com.sirolf2009.caesar.annotations.JMXBean
import java.util.List
import javax.management.ObjectName
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor
import com.sirolf2009.caesar.annotations.Match
import javax.management.MBeanServerConnection

@JMXBean @FinalFieldsConstructor class JMXBeanActor extends AbstractActor {
	
	val ObjectName objectName
	
	@Match def void onGetAttributes(GetAttributes it) {
		target.tell(connection.getAttributes(objectName, attributes), self())
	}
	
	@Data static class GetAttributes {
		ActorRef target
		MBeanServerConnection connection
		List<String> attributes
	}
	
}
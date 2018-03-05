package com.sirolf2009.caesar.server.actor.jmx

import akka.actor.AbstractActor
import akka.actor.ActorRef
import com.sirolf2009.caesar.annotations.JMXBean
import com.sirolf2009.caesar.annotations.Match
import com.sirolf2009.caesar.server.model.Attribute
import com.sirolf2009.caesar.server.model.Attributes
import java.util.Set
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor

@JMXBean @FinalFieldsConstructor
class AttributesActor extends AbstractActor {

	val MBeanServerConnection connection
	val ObjectName objectName
	val Set<String> attributes

	@Match def void onAddAttribute(AddAttribute addAttribute) {
		attributes.add(addAttribute.attribute)
	}

	@Match def void onRemoveAttribute(RemoveAttribute removeAttribute) {
		attributes.remove(removeAttribute.attribute)
	}

	@Match def void onGetValue(GetValue getValue) {
		getValue.actor.tell(new Attributes(connection.getAttributes(objectName, attributes).asList.map [
			new Attribute(objectName, name, value)
		].toList()), self())
	}

	@Data static class AddAttribute {
		val String attribute
	}

	@Data static class RemoveAttribute {
		val String attribute
	}

	@Data static class GetValue {
		val ActorRef actor
	}

}

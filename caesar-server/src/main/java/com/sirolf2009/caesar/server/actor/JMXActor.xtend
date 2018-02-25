package com.sirolf2009.caesar.server.actor

import akka.actor.AbstractActor
import com.sirolf2009.caesar.annotations.JMXBean
import com.sirolf2009.caesar.annotations.Match
import com.sirolf2009.caesar.server.model.Attribute
import com.sirolf2009.caesar.server.model.MBean
import com.sirolf2009.caesar.server.model.NewValues
import com.sirolf2009.util.akka.ActorHelper
import java.io.IOException
import java.util.Collection
import java.util.List
import javax.management.MBeanAttributeInfo
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import javax.management.remote.JMXConnector
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL
import org.eclipse.xtend.lib.annotations.Data

@JMXBean
class JMXActor extends AbstractActor {

	extension val ActorHelper helper = new ActorHelper(this)
	extension val MBeanServerConnection connection

	new(JMXServiceURL url) throws IOException {
		this(JMXConnectorFactory.connect(url).MBeanServerConnection)
	}

	new(JMXConnector connector) throws IOException {
		this(connector.MBeanServerConnection)
	}

	new(MBeanServerConnection connection) {
		this.connection = connection
		registerAs("com.sirolf2009.caesar:type=JMXActor,path="+self().path.toStringWithoutAddress)
	}
	
	@Match def onGetBeans(GetBeans it) {
		sender().tell(new Beans(queryNames(null, null).map [bean|
			val info = getMBeanInfo(bean)
			val attributes = info.attributes.map[attr| new Attribute(bean, attr)]
			new MBean(bean, attributes, info.operations)
		].toList()))
	}

	@Match def onGetAttributes(GetAttributes it) {
		try {
			sender().tell(new NewValues(attributes.toMap([it], [getValue(it)])))
		} catch(Exception e) {
			error("Failed to retrieve attribute for " + it, e)
		}
	}
	
	override postStop() throws Exception {
		ObjectName.getInstance("com.sirolf2009.caesar:type=JMXActor,path="+self().path.toStringWithoutAddress).unregisterMBean()
	}
	
	def getValue(Attribute attribute) {
		return getValue(attribute.name, attribute.attributeInfo)
	}
	
	def getValue(ObjectName objectName, MBeanAttributeInfo attributeInfo) {
		return connection.getAttribute(objectName, attributeInfo.name)
	}

	static class GetBeans {
	}

	@Data static class Beans {
		val Collection<MBean> beans
	}
	@Data static class GetAttributes {
		val List<Attribute> attributes
	}

}

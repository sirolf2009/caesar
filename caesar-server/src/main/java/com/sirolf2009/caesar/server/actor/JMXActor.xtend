package com.sirolf2009.caesar.server.actor

import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.Cancellable
import com.sirolf2009.caesar.annotations.Expose
import com.sirolf2009.caesar.annotations.JMXBean
import com.sirolf2009.caesar.annotations.Match
import com.sirolf2009.caesar.annotations.Message
import com.sirolf2009.util.akka.ActorHelper
import java.io.IOException
import java.util.HashMap
import java.util.List
import java.util.Map
import java.util.concurrent.TimeUnit
import javax.management.MBeanAttributeInfo
import javax.management.MBeanOperationInfo
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import javax.management.remote.JMXConnector
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL
import org.eclipse.xtend.lib.annotations.Data
import scala.concurrent.duration.FiniteDuration
import java.util.Collection

@JMXBean
class JMXActor extends AbstractActor {

	extension val ActorHelper helper = new ActorHelper(this)
	extension val MBeanServerConnection connection
	val Map<ActorRef, Cancellable> subscriptions = new HashMap()
	var Map<String, MBean> beans

	new(JMXServiceURL url) throws IOException {
		this(JMXConnectorFactory.connect(url))
	}

	new(JMXConnector connector) throws IOException {
		this(connector.MBeanServerConnection)
	}

	new(MBeanServerConnection connection) {
		this.connection = connection
		scanObjects()
	}
	
	@Match def onGetBeans(GetBeans it) {
		sender().tell(new Beans(beans.values))
	}

	@Match def onSubscribe(Subscribe it) {
		if(!subscriptions.containsKey(sender())) {
			val delay = FiniteDuration.Zero
			val interval = FiniteDuration.create(updateInterval, TimeUnit.MILLISECONDS)
			val msg = new SendTo(sender(), name, attribute) as Object
			val cancellable = context().system.scheduler.schedule(delay, interval, self(), msg, context().system.dispatcher, self())
			subscriptions.put(sender(), cancellable)
		}
	}

	@Match def onUnsubscribe(Unsubscribe it) {
		subscriptions.remove(sender())
	}

	@Match def onSendTo(SendTo it) {
		try {
			val value = connection.getAttribute(beans.get(name).name, attribute)
			receiver.tell(new NewValue(name, attribute, value))
		} catch(Exception e) {
			error("Failed to retrieve attribute for " + it, e)
		}
	}

	@Match def onUpdate(Update update) {
		scanObjects()
	}
	
	@Expose override void rescanObjects() {
		self().tell(new Update(), self())
	}

	def scanObjects() {
		beans = queryNames(null, null).filter[toString.startsWith("com.sirolf2009")].map [
			val info = MBeanInfo
			new MBean(it, info.attributes, info.operations)
		].toMap[name.toString]
	}

	static class Update {
	}

	static class GetBeans {
	}

	@Message static class Subscribe {
		val String name
		val String attribute
		val long updateInterval
	}

	static class Unsubscribe {
	}
	
	@Data static class Beans {
		val Collection<MBean> beans
	}

	@Data static class NewValue {
		val String name
		val String attribute
		val Object value
	}

	@Data static class SendTo {
		ActorRef receiver
		val String name
		val String attribute
	}

	@Data static class MBean {
		val ObjectName name
		val List<MBeanAttributeInfo> attributeInfo
		val List<MBeanOperationInfo> operationInfo
	}

}

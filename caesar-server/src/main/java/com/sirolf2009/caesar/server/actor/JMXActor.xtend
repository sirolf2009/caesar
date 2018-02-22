package com.sirolf2009.caesar.server.actor

import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.Cancellable
import com.sirolf2009.caesar.annotations.Expose
import com.sirolf2009.caesar.annotations.JMXBean
import com.sirolf2009.caesar.annotations.Match
import com.sirolf2009.caesar.server.model.Attribute
import com.sirolf2009.caesar.server.model.MBean
import com.sirolf2009.caesar.server.model.NewValues
import com.sirolf2009.util.akka.ActorHelper
import java.io.IOException
import java.util.Collection
import java.util.HashMap
import java.util.List
import java.util.Map
import java.util.concurrent.TimeUnit
import javax.management.MBeanAttributeInfo
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import javax.management.remote.JMXConnector
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL
import org.eclipse.xtend.lib.annotations.Data
import scala.concurrent.duration.FiniteDuration

@JMXBean
class JMXActor extends AbstractActor {

	extension val ActorHelper helper = new ActorHelper(this)
	extension val MBeanServerConnection connection
	val Map<ActorRef, Cancellable> subscriptions = new HashMap()
	var Map<String, MBean> beans

	new(JMXServiceURL url) throws IOException {
		this(JMXConnectorFactory.connect(url).MBeanServerConnection)
	}

	new(JMXConnector connector) throws IOException {
		this(connector.MBeanServerConnection)
	}

	new(MBeanServerConnection connection) {
		this.connection = connection
		scanObjects()
		registerAs("com.sirolf2009.caesar:type=JMXActor")
	}
	
	@Match def onGetBeans(GetBeans it) {
		sender().tell(new Beans(beans.values))
	}

	@Match def onSubscribe(Subscribe it) {
		if(!subscriptions.containsKey(sender())) {
			val interval = FiniteDuration.create(updateInterval, TimeUnit.MILLISECONDS)
			val cancellable = context().system.scheduler.schedule(FiniteDuration.Zero, interval, self(), new SendTo(sender(), attributes), context().dispatcher, null)
			subscriptions.put(sender(), cancellable)
		}
	}

	@Match def onUnsubscribe(Unsubscribe it) {
		val subscription = subscriptions.get(sender())
		if(subscription !== null) {
			subscription.cancel
			subscriptions.remove(sender())
		}
	}

	@Match def onSendTo(SendTo it) {
		try {
			receiver.tell(new NewValues(attributes.toMap([it], [getValue(it)])))
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
	
	@Expose override int getSubscriberCount() {
		return subscriptions.size
	}

	def scanObjects() {
		beans = queryNames(null, null).map [bean|
			val info = getMBeanInfo(bean)
			val attributes = info.attributes.map[attr| new Attribute(bean, attr)]
			new MBean(bean, attributes, info.operations)
		].toMap[name.toString]
	}
	
	def getValue(Attribute attribute) {
		return getValue(attribute.name, attribute.attributeInfo)
	}
	
	def getValue(ObjectName objectName, MBeanAttributeInfo attributeInfo) {
		return connection.getAttribute(objectName, attributeInfo.name)
	}

	static class Update {
	}

	static class GetBeans {
	}

	@Data static class Subscribe {
		val List<Attribute> attributes
		val long updateInterval
	}

	static class Unsubscribe {
	}
	
	@Data static class Beans {
		val Collection<MBean> beans
	}

	@Data static class SendTo {
		val ActorRef receiver
		val List<Attribute> attributes
	}

}

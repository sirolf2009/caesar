package com.sirolf2009.caesar.server.actor

import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.Props
import com.sirolf2009.caesar.annotations.JMXBean
import com.sirolf2009.caesar.annotations.Match
import com.sirolf2009.caesar.server.actor.jmx.AttributesActor
import com.sirolf2009.caesar.server.actor.jmx.AttributesActor.GetValue
import com.sirolf2009.caesar.server.model.Attributes
import java.util.HashMap
import java.util.Map
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javafx.collections.ObservableList
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor
import scala.concurrent.duration.FiniteDuration

@JMXBean @FinalFieldsConstructor class TableActor extends AbstractActor {
	
	val AtomicInteger counter = new AtomicInteger()
	val Map<ObjectName, ActorRef> attributes = new HashMap()
	val MBeanServerConnection connection
	val ObservableList<Attributes> items
	var Cancellable updater
	
	@Match def void onStartUpdater(StartUpdater startUpdater) {
		if(updater === null) {
			updater = context().system.scheduler.schedule(FiniteDuration.Zero, FiniteDuration.apply(startUpdater.millisTimeout, TimeUnit.MILLISECONDS), self(), new Update(), context().dispatcher, self())
		}
	}
	
	@Match def void onStopUpdater(StopUpdater stopUpdater) {
		updater.cancel()
		updater = null
	}

	@Match def void onUpdate(Update update) {
		val bucket = context().actorOf(Props.create(DataBucketActor, [new DataBucketActor(self(), attributes.size())]), '''DataBucket-«counter.incrementAndGet»''')
		attributes.values.forEach[
			tell(new GetValue(bucket), self())
		]
	}
	
	@Match def void onAttributes(Attributes attributes) {
		items.add(attributes)
	}
	
	@Match def void onAddAttribute(AddAttribute attribute) {
		if(!attributes.containsKey(attribute.objectName)) {
			val actor = context().actorOf(Props.create(AttributesActor, [new AttributesActor(connection, attribute.objectName, #[attribute.attribute].toSet)]))
			attributes.put(attribute.objectName, actor)
		} else {
			attributes.get(attribute.objectName).tell(new AttributesActor.AddAttribute(attribute.attribute), self())
		}
	}
	
	@Data static class StartUpdater {
		long millisTimeout
	}
	@Data static class StopUpdater {
	}
	@Data static class Update {
	}
	@Data static class AddAttribute {
		val ObjectName objectName
		val String attribute
	}
	
}
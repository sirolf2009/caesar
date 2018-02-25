package com.sirolf2009.caesar.server.actor

import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.Props
import com.sirolf2009.caesar.annotations.JMXBean
import com.sirolf2009.caesar.annotations.Match
import com.sirolf2009.caesar.server.actor.JMXActor.GetAttributes
import com.sirolf2009.caesar.server.model.Attribute
import com.sirolf2009.caesar.server.model.NewValues
import java.util.ArrayList
import java.util.List
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer
import javax.management.MBeanServerConnection
import org.eclipse.xtend.lib.annotations.Data
import scala.concurrent.duration.FiniteDuration

@JMXBean class TableActor extends AbstractActor {
	
	val AtomicReference<List<Attribute>> attributes
	val String name
	val Consumer<NewValues> onNewValues
	val ActorRef jmxActor
	val Cancellable timer
	
	new(MBeanServerConnection connection, String name, Attribute attribute, Consumer<NewValues> onNewValues) {
		this.name = name
		this.onNewValues = onNewValues
		jmxActor = context().actorOf(Props.create(JMXActor, [new JMXActor(connection)]), "jmx")
		attributes = new AtomicReference(new ArrayList(#[attribute]))
		timer = context().system().scheduler().schedule(FiniteDuration.Zero, FiniteDuration.apply(1, TimeUnit.SECONDS), jmxActor, new GetAttributes(attributes.get()), context().dispatcher(), self())
		registerAs("com.sirolf2009.caesar:type=TableActor,table="+name+",path="+self().path.toStringWithoutAddress)
	}
	
	@Match def void onNewValues(NewValues it) {
		onNewValues.accept(it)
	}
	
	@Match def void onStop(Stop stop) {
		timer.cancel()
	}
	
	@Match def void addColumn(AddColumn addColum) {
		attributes.updateAndGet[
			add(addColum.attribute)
			it
		]
	}
	
	@Data static class AddColumn {
		val Attribute attribute
	}
	
	@Data static class Stop {
	}
	
}
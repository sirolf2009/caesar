package com.sirolf2009.caesar.server

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.Patterns
import com.sirolf2009.caesar.server.actor.JMXActor
import com.sirolf2009.caesar.server.actor.JMXActor.Beans
import com.sirolf2009.caesar.server.actor.JMXActor.GetBeans
import com.sirolf2009.caesar.server.actor.TableActor
import com.sirolf2009.caesar.server.model.Attribute
import com.sirolf2009.caesar.server.model.NewValues
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import javax.management.MBeanServerConnection
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.eclipse.xtend.lib.annotations.Accessors

@Accessors class JMXServer {

	val String name
	val MBeanServerConnection connection
	val ActorSystem system
	val ActorRef jmxActor

	new(String name, MBeanServerConnection connection) {
		this.name = name
		this.connection = connection
		system = ActorSystem.create(name)
		jmxActor = system.actorOf(Props.create(JMXActor, connection))
	}
	
	def getBeans() {
		return (jmxActor.ask(new GetBeans(), 1000) as Beans).beans
	}
	
	def createNewTable(String tableName, Attribute initialAttribute, Consumer<NewValues> onNewValues) {
		val tableActor = system.actorOf(Props.create(TableActor, [new TableActor(connection, tableName, initialAttribute, onNewValues)]), "table-"+tableName)
		return new CaesarTable(system, tableActor)
	}
	
	def private ask(ActorRef actor, Object msg, long timeout) {
		return Await.result(Patterns.ask(actor, msg, timeout), Duration.apply(timeout, TimeUnit.MILLISECONDS))
	}
	
	override toString() {
		return name
	}

}

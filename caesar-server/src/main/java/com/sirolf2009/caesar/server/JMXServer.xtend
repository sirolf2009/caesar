package com.sirolf2009.caesar.server

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Inbox
import akka.actor.Props
import akka.pattern.Patterns
import com.sirolf2009.caesar.server.actor.TableActor
import com.sirolf2009.caesar.server.actor.jmx.JMXConnectionActor.ObjectInstances
import com.sirolf2009.caesar.server.actor.jmx.JMXConnectionActor.QueryBeans
import com.sirolf2009.caesar.server.actor.jmx.JMXConnectorActor
import com.sirolf2009.caesar.server.model.Attributes
import java.util.HashMap
import java.util.Map
import java.util.concurrent.TimeUnit
import javafx.collections.ObservableList
import javax.management.remote.JMXConnector
import org.eclipse.xtend.lib.annotations.Accessors
import scala.concurrent.Await
import scala.concurrent.duration.Duration

@Accessors class JMXServer {

	val Map<String, CaesarTable> tables = new HashMap()
	val String name
	val ActorSystem system
	val ActorRef connectorActor

	new(String name, JMXConnector connector) {
		this.name = name
		system = ActorSystem.create(name)
		connectorActor = system.actorOf(Props.create(JMXConnectorActor, [new JMXConnectorActor(connector)]))
	}

	def getBeans() {
		val inbox = Inbox.create(system)
		return (connectorActor.ask(new QueryBeans(inbox.ref, null, null), 1000) as ObjectInstances).objectInstances
	}

	def createNewTable(String tableName, ObservableList<Attributes> attributes) {
		if(!tables.containsKey(name)) {
			val tableActor = system.actorOf(Props.create(TableActor, [new TableActor(attributes)]), "table-" + tableName)
			val table = new CaesarTable(system, tableActor)
			tables.put(tableName, table)
			return table
		} else {
			throw new IllegalArgumentException('''A table named «tableName» already exists''')
		}
	}

	def private ask(ActorRef actor, Object msg, long timeout) {
		return Await.result(Patterns.ask(actor, msg, timeout), Duration.apply(timeout, TimeUnit.MILLISECONDS))
	}

	override toString() {
		return name
	}

}

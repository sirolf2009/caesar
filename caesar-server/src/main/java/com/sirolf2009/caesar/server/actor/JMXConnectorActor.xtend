package com.sirolf2009.caesar.server.actor

import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.Props
import com.sirolf2009.caesar.annotations.JMXBean
import com.sirolf2009.caesar.annotations.Match
import com.sirolf2009.caesar.annotations.Message
import javax.management.remote.JMXConnector
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor

@JMXBean @FinalFieldsConstructor class JMXConnectorActor extends AbstractActor {

	val JMXConnector connector
	var ActorRef connectionActor

	@Match def void onOpenConnection(OpenConnection openConnection) {
		val connection = connector.MBeanServerConnection
		connectionActor = context().actorOf(Props.create(JMXConnectionActor, [new JMXConnectionActor(connection)]), "connection")
	}

	@Message static class OpenConnection {
	}

}

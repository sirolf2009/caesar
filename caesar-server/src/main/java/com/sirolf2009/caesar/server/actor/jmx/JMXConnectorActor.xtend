package com.sirolf2009.caesar.server.actor.jmx

import akka.actor.AbstractActor
import akka.actor.ActorRef
import com.sirolf2009.caesar.annotations.JMXBean
import com.sirolf2009.caesar.annotations.Match
import javax.management.remote.JMXConnector
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor
import akka.actor.Props

@JMXBean @FinalFieldsConstructor class JMXConnectorActor extends AbstractActor {
	
	val JMXConnector connector
	var ActorRef connection

	@Match def void onOpenConnection(OpenConnection openConnection) {
		connection = context().actorOf(Props.create(JMXConnectionActor, [new JMXConnectionActor(connector.MBeanServerConnection)]), "connection")
	}

	@Data static class OpenConnection {
	}	
	
}
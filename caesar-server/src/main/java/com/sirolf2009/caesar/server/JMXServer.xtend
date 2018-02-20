package com.sirolf2009.caesar.server

import akka.actor.ActorSystem
import javax.management.remote.JMXServiceURL
import akka.actor.Props
import com.sirolf2009.caesar.server.actor.JMXActor
import com.sirolf2009.caesar.server.actor.PrintActor
import com.sirolf2009.caesar.server.actor.JMXActor.Subscribe
import java.time.Duration

class JMXServer {
	
	def static void main(String[] args) {
		val url = new JMXServiceURL("service:jmx:rmi://localhost/jndi/rmi://localhost:" + 4567 + "/jmxrmi")
		val system = ActorSystem.create("CaesarJMXServer")
		val jmxActor = system.actorOf(Props.create(JMXActor, url))
		val printActor = system.actorOf(PrintActor.props)
		jmxActor.tell(new Subscribe("com.sirolf2009:type=Test", "Number", Duration.ofSeconds(1).toMillis()), printActor)
	}
	
}
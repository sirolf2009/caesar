package com.sirolf2009.caesar.server

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.Patterns
import com.sirolf2009.caesar.server.actor.JMXActor
import com.sirolf2009.caesar.server.actor.JMXActor.Beans
import com.sirolf2009.caesar.server.actor.JMXActor.GetBeans
import com.sirolf2009.caesar.server.actor.TableActor
import java.util.concurrent.TimeUnit
import javax.management.remote.JMXServiceURL
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import com.sirolf2009.caesar.server.actor.JMXActor.Subscribe
import com.sirolf2009.caesar.server.actor.TableActor.AddColumn
import tech.tablesaw.api.IntColumn
import akka.actor.ActorRef
import com.sirolf2009.util.jmx.JMXUtil

class JMXServer {

	def static void main(String[] args) {
		JMXUtil.startJMX(4568)
		val url = new JMXServiceURL("service:jmx:rmi://localhost/jndi/rmi://localhost:" + 4567 + "/jmxrmi")
		val system = ActorSystem.create("CaesarJMXServer")
		val jmxActor = system.actorOf(Props.create(JMXActor, url))
		val tableActor = system.actorOf(Props.create(TableActor, "test"))
		val beans = Await.result(Patterns.ask(jmxActor, new GetBeans(), 1000), Duration.apply(1000, TimeUnit.MILLISECONDS)) as Beans
		beans.beans.filter[name.toString.startsWith("com.sirolf2009")].forEach [
			attributes.forEach [
				val intColumn = new IntColumn(attributeInfo.name)
				tableActor.tell(new AddColumn(it, intColumn), ActorRef.noSender)
				tableActor.tell(intColumn.cumSum, ActorRef.noSender)
			]
			jmxActor.tell(new Subscribe(attributes, 1000), tableActor)
		]
//		jmxActor.tell(new GetBeans(), printActor)
		Thread.sleep(3000)
		jmxActor.tell(new GetBeans(), tableActor)
		Thread.sleep(10000)
		system.terminate
	}

}

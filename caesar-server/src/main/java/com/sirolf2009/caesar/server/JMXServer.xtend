package com.sirolf2009.caesar.server

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.Patterns
import com.sirolf2009.caesar.server.actor.JMXActor
import com.sirolf2009.caesar.server.actor.JMXActor.Beans
import com.sirolf2009.caesar.server.actor.JMXActor.GetBeans
import com.sirolf2009.caesar.server.actor.JMXActor.Subscribe
import com.sirolf2009.caesar.server.actor.TableActor
import com.sirolf2009.caesar.server.actor.TableActor.AddColumn
import com.sirolf2009.caesar.server.actor.TableActor.AddMappingColumn
import com.sirolf2009.util.jmx.JMXUtil
import java.util.concurrent.TimeUnit
import java.util.function.Supplier
import javax.management.MBeanServerConnection
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import tech.tablesaw.api.IntColumn
import tech.tablesaw.columns.Column
import com.sirolf2009.caesar.server.actor.JMXActor.Update

class JMXServer {

	val ActorSystem system
	val ActorRef jmxActor

	new(MBeanServerConnection connection) {
		system = ActorSystem.create("CaesarJMXServer")
		jmxActor = system.actorOf(Props.create(JMXActor, connection))
	}
	
	def rescan() {
		jmxActor.tell(new Update(), ActorRef.noSender)
	}

	def getBeans() {
		return (jmxActor.ask(new GetBeans(), 1000) as Beans).beans
	}
	
	def createNewTable(String tableName) {
		val tableActor = system.actorOf(Props.create(TableActor, tableName), "table-"+tableName)
		return new CaesarTable(jmxActor, tableActor)
	}
	
	def private ask(ActorRef actor, Object msg, long timeout) {
		return Await.result(Patterns.ask(actor, msg, timeout), Duration.apply(timeout, TimeUnit.MILLISECONDS))
	}

	def static void main(String[] args) {
		JMXUtil.startJMX(4568)
		val url = new JMXServiceURL("service:jmx:rmi://localhost/jndi/rmi://localhost:" + 4567 + "/jmxrmi")
		new JMXServer(JMXConnectorFactory.connect(url).MBeanServerConnection) => [
			beans.filter[name.toString.startsWith("com.sirolf2009")].forEach [
				attributes.forEach [
					val intColumn = new IntColumn(attributeInfo.name)
//					tableActor.tell(new AddColumn(it, intColumn), ActorRef.noSender)
//					val Supplier<Column> percentProvider = [intColumn.asPercent]
//					tableActor.tell(new AddMappingColumn(percentProvider), ActorRef.noSender)
				]
//				jmxActor.tell(new Subscribe(attributes, 1000), tableActor)
			]
		]
		val system = ActorSystem.create("CaesarJMXServer")
		val jmxActor = system.actorOf(Props.create(JMXActor, url))
//		val printActor = system.actorOf(PrintActor.props)
		val tableActor = system.actorOf(Props.create(TableActor, "test"))
		val beans = Await.result(Patterns.ask(jmxActor, new GetBeans(), 1000), Duration.apply(1000, TimeUnit.MILLISECONDS)) as Beans
		beans.beans.filter[name.toString.startsWith("com.sirolf2009")].forEach [
			attributes.forEach [
				val intColumn = new IntColumn(attributeInfo.name)
				tableActor.tell(new AddColumn(it, intColumn), ActorRef.noSender)
				val Supplier<Column> percentProvider = [intColumn.asPercent]
				tableActor.tell(new AddMappingColumn(percentProvider), ActorRef.noSender)
			]
			jmxActor.tell(new Subscribe(attributes, 1000), tableActor)
		]
	}

}

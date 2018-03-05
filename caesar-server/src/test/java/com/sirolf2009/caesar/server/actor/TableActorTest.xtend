package com.sirolf2009.caesar.server.actor

import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.javadsl.TestKit
import com.sirolf2009.util.jmx.JMXUtil
import javafx.collections.FXCollections
import javax.management.ObjectName
import javax.management.remote.JMXConnectorFactory
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import com.sirolf2009.caesar.server.actor.TableActor.AddAttribute
import javafx.collections.ListChangeListener
import javafx.collections.ListChangeListener.Change
import com.sirolf2009.caesar.server.actor.TableActor.StartUpdater

class TableActorTest {

	static ActorSystem system

	@Test
	def void test() {
		JMXUtil.startJMX(4567)
		val connector = JMXConnectorFactory.connect(JMXUtil.getURLForPort(4567))
		val connection = connector.MBeanServerConnection
		val objectName = new ObjectName("java.lang:type=ClassLoading")
		val attribute = "TotalLoadedClassCount"
		new TestKit(system) => [
			val collection = FXCollections.observableArrayList
			val actor = system.actorOf(Props.create(TableActor, [new TableActor(connection, collection)]))
			actor.tell(new AddAttribute(objectName, attribute), ref)
			collection.addListener(new ListChangeListener() {
				
				override onChanged(Change c) {
					println(c)
				}
				
			})
			actor.tell(new StartUpdater(100), ref)
			Thread.sleep(10000)
		]
	}

	@BeforeClass
	def static void setup() {
		system = ActorSystem.create()
	}

	@AfterClass
	def static void teardown() {
		TestKit.shutdownActorSystem(system)
		system = null
	}

}

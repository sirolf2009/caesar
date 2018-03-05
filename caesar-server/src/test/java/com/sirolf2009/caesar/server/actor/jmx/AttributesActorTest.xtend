package com.sirolf2009.caesar.server.actor.jmx

import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.javadsl.TestKit
import com.sirolf2009.caesar.server.actor.jmx.AttributesActor.GetValue
import com.sirolf2009.caesar.server.model.Attributes
import com.sirolf2009.util.jmx.JMXUtil
import javax.management.ObjectName
import javax.management.remote.JMXConnectorFactory
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.Assert
import com.sirolf2009.caesar.server.actor.jmx.AttributesActor.AddAttribute
import com.sirolf2009.caesar.server.actor.jmx.AttributesActor.RemoveAttribute

class AttributesActorTest {

	static ActorSystem system

	@Test
	def void test() {
		JMXUtil.startJMX(4567)
		val connector = JMXConnectorFactory.connect(JMXUtil.getURLForPort(4567))
		val connection = connector.MBeanServerConnection
		val objectName = new ObjectName("java.lang:type=ClassLoading")
		val attribute = "TotalLoadedClassCount"
		new TestKit(system) => [
			val actor = system.actorOf(Props.create(AttributesActor, [new AttributesActor(connection, objectName, #[attribute].toSet())]))

			actor.tell(new GetValue(ref), ref)
			{
				val attributes = expectMsgClass(Attributes)
				Assert.assertEquals(1, attributes.attributes.size())
				Assert.assertEquals("java.lang:type=ClassLoading", attributes.attributes.get(0).object.toString())
				Assert.assertEquals("TotalLoadedClassCount", attributes.attributes.get(0).name)
			}

			actor.tell(new AddAttribute("UnloadedClassCount"), ref)
			actor.tell(new GetValue(ref), ref)
			{
				val attributes = expectMsgClass(Attributes)
				Assert.assertEquals(2, attributes.attributes.size())
				Assert.assertEquals("java.lang:type=ClassLoading", attributes.attributes.get(1).object.toString())
				Assert.assertEquals("UnloadedClassCount", attributes.attributes.get(1).name)
			}
			
			actor.tell(new AddAttribute("DoesNotExist"), ref)
			actor.tell(new GetValue(ref), ref)
			{
				val attributes = expectMsgClass(Attributes)
				Assert.assertEquals(2, attributes.attributes.size())
			}
			
			actor.tell(new RemoveAttribute("TotalLoadedClassCount"), ref)
			actor.tell(new GetValue(ref), ref)
			{
				val attributes = expectMsgClass(Attributes)
				Assert.assertEquals(1, attributes.attributes.size())
				Assert.assertEquals("UnloadedClassCount", attributes.attributes.get(0).name)
			}
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

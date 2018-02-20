package com.sirolf2009.caesar.server.actor

import com.sirolf2009.caesar.annotations.JMXBean
import akka.actor.AbstractActor
import com.sirolf2009.caesar.annotations.Match
import akka.actor.Props

@JMXBean
class PrintActor extends AbstractActor {
	
	@Match def onObject(Object object) {
		println(object)
	}
	
	def static props() {
		return Props.create(PrintActor)
	}
	
}
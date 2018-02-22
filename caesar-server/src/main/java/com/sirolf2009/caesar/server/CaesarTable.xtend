package com.sirolf2009.caesar.server

import akka.actor.ActorRef
import com.sirolf2009.caesar.server.model.Attribute

class CaesarTable {
	
	val ActorRef jmxActor
	val ActorRef tableActor
	
	new(ActorRef jmxActor, ActorRef tableActor) {
		this.jmxActor = jmxActor
		this.tableActor = tableActor
	}
	
	def subcribeToAttribute(Attribute attribute) {
	}
	
}
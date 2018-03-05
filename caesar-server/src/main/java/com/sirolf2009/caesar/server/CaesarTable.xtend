package com.sirolf2009.caesar.server

import akka.actor.ActorRef
import akka.actor.ActorSystem
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javax.management.ObjectName

class CaesarTable  {
	
	val ActorSystem system
	val ActorRef tableActor
	val BooleanProperty isUpdating
	
	new(ActorSystem system, ActorRef tableActor) {
		this.system = system
		this.tableActor = tableActor
		isUpdating = new SimpleBooleanProperty(false)
	}
	
	def void add(ObjectName objectName, String attribute) {
	}
	
}
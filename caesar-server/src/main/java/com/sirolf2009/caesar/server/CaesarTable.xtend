package com.sirolf2009.caesar.server

import akka.actor.ActorRef
import akka.actor.ActorSystem
import com.sirolf2009.caesar.server.actor.TableActor.AddColumn
import com.sirolf2009.caesar.server.actor.TableActor.Stop
import com.sirolf2009.caesar.server.model.Attribute
import java.io.Closeable
import java.io.IOException

class CaesarTable implements Closeable {
	
	val ActorSystem system
	val ActorRef tableActor
	
	new(ActorSystem system, ActorRef tableActor) {
		this.system = system
		this.tableActor = tableActor
	}
	
	def void add(Attribute attribute) {
		tableActor.tell(new AddColumn(attribute), ActorRef.noSender)
	}
	
	override close() throws IOException {
		tableActor.tell(new Stop(), ActorRef.noSender)
		system.stop(tableActor)
	}
	
}
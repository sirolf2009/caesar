package com.sirolf2009.caesar.server.actor

import akka.actor.AbstractActor
import akka.actor.ActorRef
import com.sirolf2009.caesar.annotations.JMXBean
import com.sirolf2009.caesar.annotations.Match
import com.sirolf2009.caesar.server.model.Attributes
import java.util.ArrayList
import java.util.List
import java.util.stream.Collectors

@JMXBean class DataBucketActor extends AbstractActor {

	val ActorRef parent
	val int expectedSize
	val List<Attributes> attributesList

	new(ActorRef target, int expectedSize) {
		this.parent = target
		this.expectedSize = expectedSize
		attributesList = new ArrayList(expectedSize)
	}

	@Match def void onAttributes(Attributes attributes) {
		attributesList.add(attributes)
		if(attributesList.size == expectedSize) {
			parent.tell(new Attributes(attributesList.stream.flatMap[it.attributes.stream].collect(Collectors.toList())), self())
			context().stop(self())
		}
	}

}

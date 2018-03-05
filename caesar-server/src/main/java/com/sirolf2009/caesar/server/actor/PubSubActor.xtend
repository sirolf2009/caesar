package com.sirolf2009.caesar.server.actor

import akka.actor.AbstractActor
import akka.actor.ActorRef
import com.sirolf2009.caesar.annotations.JMXBean
import com.sirolf2009.caesar.annotations.Match
import java.util.HashSet
import java.util.Set
import org.eclipse.xtend.lib.annotations.Data

@JMXBean class PubSubActor extends AbstractActor {
	
	val Set<ActorRef> subscribers = new HashSet()
	
	@Match def void onSubscribe(Subscribe subscribe) {
		subscribers.add(subscribe.actor)
	}
	@Match def void onUnsubscribe(Unsubscribe unsubscribe) {
		subscribers.remove(unsubscribe.actor)
	}
	@Match def void onBroadcast(Broadcast broadcast) {
		subscribers.forEach[tell(broadcast.msg, self())]
	}
	
	@Data static class Subscribe {
		val ActorRef actor
	}
	@Data static class Unsubscribe {
		val ActorRef actor
	}
	@Data static class Broadcast {
		val Object msg
	}
	
}
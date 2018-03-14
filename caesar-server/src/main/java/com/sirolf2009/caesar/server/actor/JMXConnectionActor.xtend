package com.sirolf2009.caesar.server.actor

import com.sirolf2009.caesar.annotations.JMXBean
import javax.management.MBeanServerConnection
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor

@JMXBean @FinalFieldsConstructor class JMXConnectionActor {
	
	val MBeanServerConnection connection
	
}
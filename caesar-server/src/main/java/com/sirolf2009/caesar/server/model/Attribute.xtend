package com.sirolf2009.caesar.server.model

import javax.management.ObjectName
import org.eclipse.xtend.lib.annotations.Data

@Data class Attribute {
	val ObjectName object
	val String name
	val Object value
	
	override toString() {
		return '''«object»/«name»:«value»'''
	}
}

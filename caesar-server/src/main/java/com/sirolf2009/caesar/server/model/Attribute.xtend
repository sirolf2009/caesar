package com.sirolf2009.caesar.server.model

import javax.management.MBeanAttributeInfo
import javax.management.ObjectName
import org.eclipse.xtend.lib.annotations.Data

@Data class Attribute {
	val ObjectName name
	val MBeanAttributeInfo attributeInfo
	
	override toString() {
		return attributeInfo.name
	}
}

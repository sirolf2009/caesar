package com.sirolf2009.caesar.server.model

import java.util.List
import javax.management.MBeanOperationInfo
import javax.management.ObjectName
import org.eclipse.xtend.lib.annotations.Data

@Data class MBean {
	val ObjectName name
	val List<Attribute> attributes
	val List<MBeanOperationInfo> operationInfo
}

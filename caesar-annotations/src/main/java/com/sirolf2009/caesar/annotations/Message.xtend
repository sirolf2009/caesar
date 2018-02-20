package com.sirolf2009.caesar.annotations

import java.math.BigDecimal
import java.math.BigInteger
import java.util.Date
import java.util.HashMap
import java.util.Map
import javax.management.ObjectName
import javax.management.openmbean.CompositeDataSupport
import javax.management.openmbean.CompositeType
import javax.management.openmbean.OpenDataException
import javax.management.openmbean.OpenType
import org.eclipse.xtend.lib.macro.AbstractClassProcessor
import org.eclipse.xtend.lib.macro.Active
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.TypeReference

@Active(MessageProcessor)
annotation Message {
	static class MessageProcessor extends AbstractClassProcessor {

		override doTransform(MutableClassDeclaration annotatedClass, extension TransformationContext context) {
			annotatedClass.extendedClass = newTypeReference(CompositeDataSupport)

			annotatedClass.addMethod("createCompositeType") [
				val compositeType = newTypeReference(CompositeType)
				val fieldNames = annotatedClass.declaredFields.map[simpleName].map['''"«it»"'''].reduce[a, b|a + "," + b]
				val fieldTypes = annotatedClass.declaredFields.map['''javax.management.openmbean.SimpleType.«context.getSimpleType(type)»'''].reduce[a, b|a + "," + b]
				static = true
				returnType = compositeType
				body = '''
				try {
					return new «compositeType»("«annotatedClass.simpleName»", «annotatedClass.simpleName».class.getName(), 
					new String[] {«fieldNames»}, 
					new String[] {«fieldNames»},
					new «newTypeReference(OpenType)»[] { «fieldTypes» });
				} catch(«newTypeReference(Exception)» e) {
					throw new «newTypeReference(RuntimeException)»("Failed to construct CompositeType", e);
				}'''
			]

			annotatedClass.addMethod("createCompositeMap") [
				annotatedClass.declaredFields.forEach [ field |
					addParameter(field.simpleName, field.type)
				]
				val map = newTypeReference(Map, newTypeReference(String), anyType)
				val entries = annotatedClass.declaredFields.map['''map.put("«simpleName»", «simpleName»);'''].reduce[a, b|a + "\n" + b]
				static = true
				returnType = map
				body = '''
					«map» map = new «newTypeReference(HashMap, newTypeReference(String), anyType)»();
					«entries»
					return map;
				'''
			]

			annotatedClass.addConstructor [
				annotatedClass.declaredFields.forEach [ field |
					addParameter(field.simpleName, field.type)
				]
				val mapParams = annotatedClass.declaredFields.map['''«simpleName»'''].reduce[a, b|a + ", " + b]
				exceptions = #[newTypeReference(OpenDataException)]
				body = '''
				super(createCompositeType(), createCompositeMap(«mapParams»));'''
			]

			annotatedClass.declaredFields.forEach [ field |
				annotatedClass.addMethod("get" + field.simpleName.toFirstUpper) [
					returnType = field.type
					body = '''return («field.type») get("«field.simpleName»");'''
				]
			]

			annotatedClass.declaredFields.forEach[remove]
		}

		def static getSimpleType(extension TransformationContext context, TypeReference type) {
			if(type == newTypeReference(Date)) return "DATE"
			if(type == newTypeReference(String)) return "STRING"
			if(type == newTypeReference(BigDecimal)) return "BIGDECIMAL"
			if(type == newTypeReference(BigInteger)) return "BIGINTEGER"
			if(type == newTypeReference(ObjectName)) return "OBJECTNAME"
			if(type == newTypeReference(Integer) || type == newTypeReference(int)) return "INTEGER"
			if(type == newTypeReference(Double) || type == newTypeReference(double)) return "DOUBLE"
			if(type == newTypeReference(Float) || type == newTypeReference(float)) return "FLOAT"
			if(type == newTypeReference(Long) || type == newTypeReference(long)) return "LONG"
			if(type == newTypeReference(Short) || type == newTypeReference(short)) return "SHORT"
			if(type == newTypeReference(Byte) || type == newTypeReference(byte)) return "BYTE"
			if(type == newTypeReference(Boolean) || type == newTypeReference(boolean)) return "BOOLEAN"
			if(type == newTypeReference(Character) || type == newTypeReference(char)) return "CHARACTER"
		}

	}
}

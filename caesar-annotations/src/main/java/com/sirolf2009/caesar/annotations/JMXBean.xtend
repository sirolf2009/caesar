package com.sirolf2009.caesar.annotations

import javax.management.InstanceAlreadyExistsException
import javax.management.MBeanException
import javax.management.MXBean
import javax.management.MalformedObjectNameException
import javax.management.NotCompliantMBeanException
import org.eclipse.xtend.lib.macro.AbstractClassProcessor
import org.eclipse.xtend.lib.macro.Active
import org.eclipse.xtend.lib.macro.RegisterGlobalsContext
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.MethodDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableInterfaceDeclaration
import akka.actor.AbstractActor.Receive
import akka.japi.pf.ReceiveBuilder
import java.lang.management.ManagementFactory
import javax.management.ObjectName

@Active(JMXBeanProcessor)
annotation JMXBean {
	static class JMXBeanProcessor extends AbstractClassProcessor {

		override doRegisterGlobals(ClassDeclaration annotatedClass, RegisterGlobalsContext context) {
			context.registerInterface(annotatedClass.beanName)
		}

		override doTransform(MutableClassDeclaration annotatedClass, extension TransformationContext context) {
			val beanDeclaration = findInterface(annotatedClass.beanName)
			beanDeclaration.addAnnotation(newAnnotationReference(MXBean))
			annotatedClass.implementedInterfaces = annotatedClass.implementedInterfaces + #[beanDeclaration.newTypeReference()]

			context.exposeFields(annotatedClass, beanDeclaration)
			context.exposeMethods(annotatedClass, beanDeclaration)
			context.createReceiver(annotatedClass)
			context.addRegisterAs(annotatedClass)
		}
		
		def static createReceiver(extension TransformationContext context, MutableClassDeclaration annotatedClass) {
			val builder = annotatedClass.declaredMethods.filter [findAnnotation(newTypeReference(Match).type) !== null].filter [parameters.size == 1].map[
				val param = parameters.get(0)
				'''.match(«param.type».class, «param.simpleName» -> «simpleName»(«param.simpleName»))'''
			].reduce[a,b|a+b]
			annotatedClass.addMethod("createReceive") [
				returnType = newTypeReference(Receive)
				body = '''return new «newTypeReference(ReceiveBuilder)»()«builder».build();'''
			]
		}
		
		def static exposeFields(extension TransformationContext context, MutableClassDeclaration annotatedClass, MutableInterfaceDeclaration beanDeclaration) {
			annotatedClass.declaredFields.filter [findAnnotation(newTypeReference(Expose).type) !== null].forEach [
				context.expose(it, beanDeclaration, annotatedClass)
			]
		}
		
		def static exposeMethods(extension TransformationContext context, MutableClassDeclaration annotatedClass, MutableInterfaceDeclaration beanDeclaration) {
			annotatedClass.declaredMethods.filter [findAnnotation(newTypeReference(Expose).type) !== null].forEach [
				addDeclaration(it, beanDeclaration)
			]
		}

		def static expose(extension TransformationContext context, MutableFieldDeclaration field, MutableInterfaceDeclaration bean, MutableClassDeclaration actor) {
			field.createGetter(actor) => [
				addAnnotation(newAnnotationReference(Override))
				addDeclaration(bean)
			]
			field.createSetter(actor) => [
				addAnnotation(newAnnotationReference(Override))
				addDeclaration(bean)
			]
		}

		def static createGetter(MutableFieldDeclaration field, MutableClassDeclaration actor) {
			actor.addMethod("get" + field.simpleName.toFirstUpper) [
				returnType = field.type
				body = '''return «field.simpleName»;'''
			]
		}

		def static createSetter(MutableFieldDeclaration field, MutableClassDeclaration actor) {
			actor.addMethod("set" + field.simpleName.toFirstUpper) [
				addParameter(field.simpleName, field.type)
				body = '''this.«field.simpleName» = «field.simpleName»;'''
			]
		}

		def static addDeclaration(MethodDeclaration methodDeclaration, MutableInterfaceDeclaration beanDeclaration) {
			beanDeclaration.addMethod(methodDeclaration.simpleName) [
				exceptions = methodDeclaration.exceptions
				docComment = methodDeclaration.docComment
				returnType = methodDeclaration.returnType
				methodDeclaration.parameters.forEach [ param |
					addParameter(param.simpleName, param.type)
				]
			]
		}
		
		def static addRegisterAs(extension TransformationContext context, MutableClassDeclaration annotatedClass) {
			annotatedClass.addMethod("registerAs") [
				addParameter("name", newTypeReference(String))
				body = '''«newTypeReference(ManagementFactory)».getPlatformMBeanServer().registerMBean(this, «newTypeReference(ObjectName)».getInstance(name));'''
				exceptions = exceptions + #[newTypeReference(MBeanException), newTypeReference(InstanceAlreadyExistsException), newTypeReference(NotCompliantMBeanException), newTypeReference(MalformedObjectNameException)]
			]
		}

		def static getBeanName(ClassDeclaration clazz) {
			clazz.qualifiedName + "MXBean"
		}
	}

}

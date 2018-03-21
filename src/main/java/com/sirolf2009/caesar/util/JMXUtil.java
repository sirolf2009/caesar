package com.sirolf2009.caesar.util;

import javax.management.remote.JMXServiceURL;
import java.net.MalformedURLException;

public class JMXUtil {

	public static JMXServiceURL fromHostAndPort(String host, int port) throws MalformedURLException {
		return new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+host+":"+port+"/jmxrmi");
	}

}

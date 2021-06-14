package com.samy.study.git.Utilities;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

public class ServerDetector {

	public static ServerDetector getInstance() {
		if (_instance == null) {
			_instance = new ServerDetector();
			_instance._init();
		}
		return _instance;
	}

	public static String getServerId() {
		return getInstance()._serverId;
	}

	public static void init(String serverId) {
		ServerDetector serverDetector = new ServerDetector();
		serverDetector._serverId = serverId;
		if (serverId.equals("geronimo")) {
			serverDetector._geronimo = true;
		} else if (serverId.equals("glassfish")) {
			serverDetector._glassfish = true;
		} else if (serverId.equals("jboss")) {
			serverDetector._jBoss = true;
		} else if (serverId.equals("jetty")) {
			serverDetector._jetty = true;
		} else if (serverId.equals("jonas")) {
			serverDetector._jonas = true;
		} else if (serverId.equals("oc4j")) {
			serverDetector._oc4j = true;
		} else if (serverId.equals("resin")) {
			serverDetector._resin = true;
		} else if (serverId.equals("tomcat")) {
			serverDetector._tomcat = true;
		} else if (serverId.equals("weblogic")) {
			serverDetector._webLogic = true;
		} else if (serverId.equals("websphere")) {
			serverDetector._webSphere = true;
		} else {
			serverDetector._init();
		}
		_instance = serverDetector;
	}

	public static boolean isGeronimo() {
		return getInstance()._geronimo;
	}

	public static boolean isGlassfish() {
		return getInstance()._glassfish;
	}

	public static boolean isJBoss() {
		return getInstance()._jBoss;
	}

	public static boolean isJBoss5() {
		return getInstance()._jBoss5;
	}

	public static boolean isJBoss7() {
		return getInstance()._jBoss7;
	}

	public static boolean isJetty() {
		return getInstance()._jetty;
	}

	public static boolean isJOnAS() {
		return getInstance()._jonas;
	}

	public static boolean isOC4J() {
		return getInstance()._oc4j;
	}

	public static boolean isResin() {
		return getInstance()._resin;
	}

	public static boolean isSupportsComet() {
		return _SUPPORTS_COMET;
	}

	public static boolean isSupportsHotDeploy() {
		return getInstance()._supportsHotDeploy;
	}

	public static boolean isTomcat() {
		return getInstance()._tomcat;
	}

	public static boolean isWebLogic() {
		return getInstance()._webLogic;
	}

	public static boolean isWebSphere() {
		return getInstance()._webSphere;
	}

	public static void setSupportsHotDeploy(boolean supportsHotDeploy) {
		getInstance()._supportsHotDeploy = supportsHotDeploy;
		if (supportsHotDeploy) {
			System.out.println("Server supports hot deploy");
		} else {
			System.out.println("Server does not support hot deploy. Remote.");
		}
	}

	private boolean _detect(String className) {
		try {
			ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
			systemClassLoader.loadClass(className);
			return true;
		} catch (ClassNotFoundException cnfe) {
			Class<?> clazz = getClass();
			if (clazz.getResource(className) != null) {
				return true;
			} else {
				return false;
			}
		}
	}

	private boolean _hasSystemProperty(String key) {
		String value = System.getProperty(key);
		if (value != null) {
			return true;
		} else {
			return false;
		}
	}

	private void _init() {
		if (_isGeronimo()) {
			_serverId = "geronimo";
			_geronimo = true;
		} else if (_isGlassfish()) {
			_serverId = "glassfish";
			_glassfish = true;
		} else if (_isJBoss()) {
			_serverId = "jboss";
			_jBoss = true;
			if (_isJBoss5()) {
				_jBoss5 = true;
			} else {
				_jBoss7 = true;
			}
		} else if (_isJOnAS()) {
			_serverId = "jonas";
			_jonas = true;
		} else if (_isOC4J()) {
			_serverId = "oc4j";
			_oc4j = true;
		} else if (_isResin()) {
			_serverId = "resin";
			_resin = true;
		} else if (_isWebLogic()) {
			_serverId = "weblogic";
			_webLogic = true;
		} else if (_isWebSphere()) {
			_serverId = "websphere";
			_webSphere = true;
		}
		if (_serverId == null) {
			if (_isJetty()) {
				_serverId = "jetty";
				_jetty = true;
			} else if (_isTomcat()) {
				_serverId = "tomcat";
				_tomcat = true;
			}
		}
		if (System.getProperty("external-properties") == null) {

			if (_serverId != null) {
				System.out.println("Detected server " + _serverId);
			} else {
				System.out.println("No server detected");
			}

		}
		/*
		 * if (_serverId == null) { throw new
		 * RuntimeException("Server is not supported"); }
		 */
	}

	private boolean _isGeronimo() {
		return _hasSystemProperty("org.apache.geronimo.home.dir");
	}

	private boolean _isGlassfish() {
		return _hasSystemProperty("com.sun.aas.instanceRoot");
	}

	private boolean _isJBoss() {
		return _hasSystemProperty("jboss.home.dir");
	}

	private boolean _isJBoss5() {
		try {
			for (MBeanServer mBeanServer : MBeanServerFactory
					.findMBeanServer(null)) {
				String defaultDomain = mBeanServer.getDefaultDomain();
				if (defaultDomain.equals("jboss")) {
					ObjectName objectName = new ObjectName(
							"jboss.system:type=Server");
					String version = (String) mBeanServer.getAttribute(
							objectName, "VersionNumber");
					if (version.startsWith("5")) {
						return true;
					}
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean _isJetty() {
		return _hasSystemProperty("jetty.home");
	}

	private boolean _isJOnAS() {
		return _hasSystemProperty("jonas.base");
	}

	private boolean _isOC4J() {
		return _detect("oracle.oc4j.util.ClassUtils");
	}

	private boolean _isResin() {
		return _hasSystemProperty("resin.home");
	}

	private boolean _isTomcat() {
		return _hasSystemProperty("catalina.base");
	}

	private boolean _isWebLogic() {
		return _detect("/weblogic/Server.class");
	}

	private boolean _isWebSphere() {
		return _detect("/com/ibm/websphere/product/VersionInfo.class");
	}

	private static final boolean _SUPPORTS_COMET = false;
	private static ServerDetector _instance;
	private boolean _geronimo;
	private boolean _glassfish;
	private boolean _jBoss;
	private boolean _jBoss5;
	private boolean _jBoss7;
	private boolean _jetty;
	private boolean _jonas;
	private boolean _oc4j;
	private boolean _resin;
	private String _serverId;
	private boolean _supportsHotDeploy;
	private boolean _tomcat;
	private boolean _webLogic;
	private boolean _webSphere;
}

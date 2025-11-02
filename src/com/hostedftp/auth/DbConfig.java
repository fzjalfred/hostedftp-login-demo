package com.hostedftp.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

public final class DbConfig {
	private static final Properties P = new Properties();
	static {
		try (InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("db.properties")) {
			if (in == null) throw new IllegalStateException("db.properties not on classpath");
			P.load(in);
		} catch (IOException e) { throw new RuntimeException(e); }
	}
	public static String url()  { return P.getProperty("mysql.url"); }
	public static String user() { return P.getProperty("mysql.user"); }
	public static String pass() { return P.getProperty("mysql.pass"); }
}


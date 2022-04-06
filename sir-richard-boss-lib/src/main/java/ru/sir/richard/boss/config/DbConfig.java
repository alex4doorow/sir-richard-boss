package ru.sir.richard.boss.config;

public class DbConfig {
	
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_SR_PRODUCTION_URL = "jdbc:mysql://127.0.0.1:3306/p326995_mysirric?user=***&password=***=true&characterEncoding=UTF-8&useSSL=false";
	public static final String DB_PM_PRODUCTION_URL = "jdbc:mysql://127.0.0.1:3306/p326995_pm?user=***&password=***&useUnicode=true&characterEncoding=UTF-8&useSSL=false";

	public static final String DB_JNDI = "java:comp/env/jdbc/sirRichardBoss";

}

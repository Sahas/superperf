package com.turvo.perf.grafana.client;

public class GrafanaConfiguration {
	
	private String host;
	private int port;
	private String user;
	private String password;
	private String dashboardUid;
	private String datasource;
	
	public String getHost() {
		return host;
	}
	public String getDashboardUid() {
		return dashboardUid;
	}
	public void setDashboardUid(String dashboardUid) {
		this.dashboardUid = dashboardUid;
	}
	public String getDatasource() {
		return datasource;
	}
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public GrafanaConfiguration host(String host) {
		this.setHost(host);
		return this;
	}
	
	public GrafanaConfiguration port(int port) {
		this.setPort(port);
		return this;
	}
	
	public GrafanaConfiguration user(String user) {
		this.setUser(user);
		return this;
	}
	
	public GrafanaConfiguration password(String password) {
		this.setPassword(password);
		return this;
	}
	
	public GrafanaConfiguration dashboardUid(String dashboardUid) {
		this.dashboardUid = dashboardUid;
		return this;
	}
	
	public GrafanaConfiguration datasource(String datasource) {
		this.datasource = datasource;
		return this;
	}
	
}

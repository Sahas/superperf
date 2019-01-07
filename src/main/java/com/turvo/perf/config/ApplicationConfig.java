package com.turvo.perf.config;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.offbytwo.jenkins.JenkinsServer;
import com.turvo.perf.grafana.client.GrafanaConfiguration;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.userauth.keyprovider.PKCS8KeyFile;

@Configuration
public class ApplicationConfig {

	@Bean
	public JenkinsServer jenkinsServer(@Value("${jenkins.url}") String url, @Value("${jenkins.userId}") String userName,
			@Value("${jenkins.password}") String password, @Value("${jenkins.apiToken}") String apiToken)
			throws URISyntaxException {

		return new JenkinsServer(new URI(url), userName, password);

	}
	
	
	@Bean
	public SSHClient sshClient(@Value("${jenkins.host}") String host,@Value("${jenkins.host.user}") String user, @Value("${jenkins.host.pem.path}") String pemFilePath) throws IOException {
		SSHClient client = new SSHClient();
		client.addHostKeyVerifier(new PromiscuousVerifier());
		client.connect(host);
		PKCS8KeyFile keyFile = new PKCS8KeyFile();
		keyFile.init(new File(pemFilePath));
//		client.authPublickey("ubuntu",keyFile);
//		KeyProvider key = client.loadKeys(pemFilePath);
		client.authPublickey(user, keyFile);
		client.useCompression();
		return client;
	}
	
	@Bean
	public GrafanaConfiguration grafanaConfiguration(@Value("${grafana.host}") String host, @Value("${grafana.port}") int port, @Value("${grafana.user}") String user, @Value("${grafana.password}") String password,
			@Value("${grafana.dashboard.uid}") String dashboardUid, @Value("${grafana.dashboard.datasource.db}") String datasource) {
		return new GrafanaConfiguration().host(host).port(port).user(user).password(password).dashboardUid(dashboardUid).datasource(datasource);
	}
}

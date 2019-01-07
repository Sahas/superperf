package com.turvo.test.perf.jenkins;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


import org.junit.Test;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.PKCS8KeyFile;

public class JenkinsEnvSetupTest {
	
	@Test
	public void connectSSH() throws IOException {
		SSHClient client = new SSHClient();
		client.addHostKeyVerifier(new PromiscuousVerifier());
		client.connect("ec2-34-220-137-248.us-west-2.compute.amazonaws.com");
		PKCS8KeyFile keyFile = new PKCS8KeyFile();
		keyFile.init(new File("/Users/sahas.n/.ssh/redash_bi.pem"));
//		client.authPublickey("ubuntu",keyFile);
//		KeyProvider key = client.loadKeys(pemFilePath);
		client.authPublickey("ubuntu", keyFile);
		client.useCompression();
		
		Session session = client.startSession();
		Command cmd = session.exec("mkdir -m777 -p /home/ubuntu/random/users/user/1");
		System.out.println("---------");
		System.out.println("----" + IOUtils.readFully(cmd.getErrorStream()));
		cmd.join(5, TimeUnit.SECONDS);
		client.disconnect();

	}
}

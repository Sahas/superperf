package com.turvo.perf.core;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.xfer.FileSystemFile;
import net.schmizz.sshj.xfer.LocalSourceFile;

/**
 * Uploads the multipart file to jenkins server via SCP. 
 * Refactor later - PathDetails in separate class for more clarity
 * @author sahas.n
 *
 */
//@Component
public class ScpStorageService implements StorageService {
	
	private SSHClient sshClient;
	
	private String baseFolder;
	
	private static final Logger LOGGER = LogManager.getLogger(ScpStorageService.class);

	public SSHClient getSshClient() {
		return sshClient;
	}
	
	public String getBaseFolder() {
		return baseFolder;
	}
	
	//@Value("${storage.folder}")
	public void setBaseFolder(String baseFolder) {
		this.baseFolder = baseFolder;
	}

	//@Autowired
	public void setSshClient(SSHClient sshClient) {
		this.sshClient = sshClient;
	}

	@Override
	public void saveFile(File file, Map<String, String> pathDetails)
			throws IllegalStateException, IOException {
		// TODO Auto-generated method stub
		if(file!=null && file.exists()) {
			LocalSourceFile sourceFile = new FileSystemFile(file);
			String relFolder = pathDetails.get("relFolder");
			String remotePath = this.baseFolder+pathDetails.get("relPath");
			createRelFolderOnRemoteMachine(relFolder);
			LOGGER.info("Uploading file to path :" + remotePath + " in remote machine");
			this.sshClient.newSCPFileTransfer().upload(sourceFile, remotePath);
			chmod(remotePath);
		}
	}
	
	
	public void createRelFolderOnRemoteMachine(String relFolder) throws ConnectionException, TransportException {
		Session session = this.sshClient.startSession();
		Command cmd = session.exec("mkdir -m777 -p " + this.baseFolder + relFolder);
		cmd.join(5, TimeUnit.SECONDS);
		session.close();
	}
	
	public void chmod(String path) throws ConnectionException, TransportException {
		Session session = this.sshClient.startSession();
		session.exec("chmod 777 " + path);
		session.close();
	}
	/**
	 * TODO: Change the filename to unique/random number so that no conflict exists.
	 */
//	@Override
//	public void saveFile(MultipartFile file, Map<String,String> pathDetails) throws IllegalStateException, IOException {
//		File tempFile = new File(file.getOriginalFilename());
//		file.transferTo(tempFile);
//		LocalSourceFile sourceFile = new FileSystemFile(tempFile);
//		String remotePath = this.baseFolder+pathDetails.get("relPath");
//		LOGGER.info("Saving file to path :" + remotePath);
//		this.sshClient.newSCPFileTransfer().upload(sourceFile, remotePath);
//	}
	
	

}

package com.tuvo.perf.core;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.xfer.FileSystemFile;
import net.schmizz.sshj.xfer.LocalSourceFile;

/**
 * Uploads the multipart file to jenkins server via SCP. 
 * Refactor later - PathDetails in separate class for more clarity
 * @author sahas.n
 *
 */
@Component
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
	
	@Value("${jenkins.scp.base.folder}")
	public void setBaseFolder(String baseFolder) {
		this.baseFolder = baseFolder;
	}

	@Autowired
	public void setSshClient(SSHClient sshClient) {
		this.sshClient = sshClient;
	}
	
	/**
	 * TODO: Change the filename to unique/random number so that no conflict exists.
	 */
	@Override
	public void saveFile(MultipartFile file, Map<String,String> pathDetails) throws IllegalStateException, IOException {
		File tempFile = new File(file.getOriginalFilename());
		file.transferTo(tempFile);
		LocalSourceFile sourceFile = new FileSystemFile(tempFile);
		String remotePath = this.baseFolder+pathDetails.get("relPath");
		LOGGER.info("Saving file to path :" + remotePath);
		this.sshClient.newSCPFileTransfer().upload(sourceFile, remotePath);
	}
	
	

}

package com.turvo.perf.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.jmeter.save.SaveService;
import org.apache.jorphan.collections.HashTree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.turvo.perf.jmeter.domain.JTestPlan;

import lombok.Data;

/**
 * Calls the required Jmx Genrator and saves the file in local/remote machine
 * @author sahas.n
 *
 */
@Component("integrationService")
//@Component
@Data
public class JmxFileUploadIntegrationService {
	
	private static final Logger LOGGER = LogManager.getLogger(JmxFileUploadIntegrationService.class);
	
	@Autowired
	private StorageService storageService;
	
	@Autowired
	private JmxGenerator jmxGenerator;
	
	public void generateAndStoreJmxFile(JTestPlan jTestPlan) throws FileNotFoundException, IOException {
		LOGGER.info("Invoking JMXGen to get hashtree");
		HashTree jmxHashTree = jmxGenerator.generateJmxFromJson(jTestPlan);
		Map<String,String> pathDetails = new HashMap<>();
		String storageFolder= RandomStringUtils.randomAlphanumeric(8, 12);
		String storageFileName = storageFolder + ".jmx";
		//String storageFileName = jTestPlan.getName() + ".jmx";
		pathDetails.put("relFolder", "/users/user1/" + storageFolder);
		pathDetails.put("relPath", "/users/user1/" + storageFolder + System.getProperty("file.separator") + storageFileName);
		File file = new File(storageFileName);
		jmxGenerator.writeJmxIntoFile(jmxHashTree, file);
		LOGGER.info("Uploading " + storageFileName);
		storageService.saveFile(file, pathDetails);
		file.delete();
	}
	
	
}

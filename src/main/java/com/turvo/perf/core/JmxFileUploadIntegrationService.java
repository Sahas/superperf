package com.turvo.perf.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.mongodb.DBObject;
import com.turvo.perf.jenkins.domain.BuildDetails;
import com.turvo.perf.jenkins.domain.JobDetails;
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
	
	@Autowired
	private JenkinsJobService jenkinsJobService;
	
	@Autowired
	private MongoTemplate mongo;
	
	@Value("${storage.base.folder}")
	private String baseFolder;
	
	
	@Value("${jenkins.jobname}")
	private String jobName;
	
	public Map<String,String> generateAndStoreJmxFile(JTestPlan jTestPlan) throws FileNotFoundException, IOException {
		LOGGER.info("Invoking JMXGen to get hashtree");
		HashTree jmxHashTree = jmxGenerator.generateJmxFromJson(jTestPlan);
		Map<String,String> pathDetails = new HashMap<>();
		String storageFolder= RandomStringUtils.randomAlphanumeric(8, 12);
		String storageFileName = storageFolder + ".jmx";
		//String storageFileName = jTestPlan.getName() + ".jmx";
		pathDetails.put("folderPath", baseFolder + "/users/user1/" + storageFolder);
		pathDetails.put("fileName", storageFileName);
		pathDetails.put("fullPath", baseFolder + "/users/user1/" + storageFolder + System.getProperty("file.separator") + storageFileName);
		File file = new File(storageFileName);
		jmxGenerator.writeJmxIntoFile(jmxHashTree, file);
		LOGGER.info("Uploading " + storageFileName);
		storageService.saveFile(file, pathDetails);
		//file.delete();
		return pathDetails;
	}
	
	public JobDetails storeJobDetailsInDatastore(String name, JTestPlan testPlan, Map<String,String> pathDetails) {
		JobDetails job = new JobDetails();
		String uniqueJobId = RandomStringUtils.randomAlphanumeric(8, 12);
		job.jobId(uniqueJobId).name(name).testPlan(testPlan).scriptName(pathDetails.get("fileName")).scriptLocation(pathDetails.get("folderPath"));
		mongo.insert(job);
		return job;
	}
	
	public void triggerBuild(JobDetails job, Map<String, String> params) throws IOException {
		LOGGER.info("Triggering build for job :" + job.getName());
		BuildDetails build = new BuildDetails();
		int buildNumber = jenkinsJobService.triggerJob(jobName, params);
		LOGGER.info("Triggered build for job :" + job.getName() + " with id: " + buildNumber);
		build.setBuildId(RandomStringUtils.randomAlphanumeric(8, 12));
		build.setJenkinsBuildId(buildNumber + "");
		build.setParameters(params);
		if(job.getBuilds() == null) {
			job.setBuilds(new ArrayList<BuildDetails>());
		}
		job.getBuilds().add(build); 
	}
	
	public List<DBObject> getAllJobDetails(){
		return mongo.findAll(DBObject.class, "jobs");
	}
	
	public DBObject getJobDetails(String jobId){
		Query searchQuery = new Query();
		searchQuery.addCriteria(new Criteria("jobId").is(jobId));
		return mongo.findOne(searchQuery, DBObject.class, "jobs");
	}
	
	public void storeJob(JobDetails jobDetails) {
		mongo.save(jobDetails);
	}
	
}

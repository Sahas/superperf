package com.turvo.perf.controller;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turvo.perf.core.JmxFileUploadIntegrationService;
import com.turvo.perf.jenkins.domain.JobDetails;
import com.turvo.perf.jmeter.domain.JTestPlan;
import com.turvo.perf.jmeter.domain.TriggerJobPayload;

/**
 * 
 * @author sahas.n
 *
 * What should this do? 
 * 1. UI - Get the jmx file, Get all the required parameters, Save the jmx file
 * 2.  Isolate the script env for each jmx
 * 3. Trigger the jenkins job
 * 4. Get periodic status of the job
 * 5. Stop or retrigger the job
 * 
 */
@Controller
//@RequestMapping("/jenkins")
public class JenkinsTriggerController {
	
	@Resource(name="integrationService")
	//@Autowired
	private JmxFileUploadIntegrationService integrationService;
	
	private static final Logger LOGGER = LogManager.getLogger(JenkinsTriggerController.class);
	
	private static final ObjectMapper mapper =
		      new ObjectMapper()
		          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
		          .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
	
	/**
	 * Creates a Jmeter script env folder and stores the JMX file uploaded. 
	 * The folder is created to isolate each script from any interferences
	 *  
	 * 
	 * @return 
	 */
	@PostMapping("/saveJmxFile")
	public @ResponseBody JobDetails saveJMXFile(@RequestParam("jmxFile") MultipartFile file){
		return null;
	}
	
	@PostMapping("/triggerJob")
	public @ResponseBody JobDetails triggerJob(@RequestParam("jobId") String jobId) {
		
		return null;
	}
	
	@GetMapping("/jobs")
	public @ResponseBody List<JobDetails> getJobs(){
		return integrationService.getAllJobDetails();
	}
	
	@GetMapping("/jobs/{jobId}")
	public @ResponseBody JobDetails getJobDetails(@PathVariable("jobId") String jobId){
		return integrationService.getJobDetails(jobId);
	}
	
	@PostMapping("/convertAndTrigger")
	public @ResponseBody JobDetails generateJmxAndTriggerBuild(@RequestBody String testPlanAndParamsPayload) throws FileNotFoundException, IOException {
		//Map<String,Object> fullDetailsMap = mapper.readValue(testPlanAndParamsPayload, new TypeReference<HashMap<String,Object>>(){});
		TriggerJobPayload triggerJobPayload = mapper.readValue(testPlanAndParamsPayload, new TypeReference<TriggerJobPayload>(){});
		Map<String,String> scriptDetails = integrationService.generateAndStoreJmxFile(triggerJobPayload.getTestPlan());
		JobDetails job = integrationService.storeJobDetailsInDatastore(triggerJobPayload.getTestPlan().getName(), 
				triggerJobPayload.getTestPlan(), scriptDetails);
		triggerJobPayload.getTriggerParameters().put("scriptFolderPath", scriptDetails.get("folderPath"));
		triggerJobPayload.getTriggerParameters().put("scriptName", scriptDetails.get("fileName"));
		integrationService.triggerBuild(job, triggerJobPayload.getTriggerParameters());
		integrationService.storeJob(job);
		return job;
	}
	
	@PostMapping("/jobs/{jobId}/trigger")
	public @ResponseBody JobDetails triggerBuild(@RequestBody String buildParamsPayload) throws JsonParseException, JsonMappingException, IOException {
		Map<String,String> triggerJobPayload = mapper.readValue(buildParamsPayload, new TypeReference<HashMap<String,String>>(){});
		JobDetails jobDetails = integrationService.getJobDetails(triggerJobPayload.get("jobId"));
		integrationService.triggerBuild(jobDetails, triggerJobPayload);
		integrationService.storeJob(jobDetails);
		return jobDetails;
	}
	
	
}

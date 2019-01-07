package com.turvo.perf.controller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.turvo.perf.jenkins.domain.JobDetails;

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
@RequestMapping("/jenkins")
public class JenkinsTriggerController {
	
	private static final Logger LOGGER = LogManager.getLogger(JenkinsTriggerController.class);
	
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
	
}

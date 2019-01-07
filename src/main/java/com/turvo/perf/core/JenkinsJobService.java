package com.turvo.perf.core;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.HttpResponseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueReference;

/**
 *  Purpose:
 *  1. Trigger the job
 *  2. Stop the job
 *  3. Later : Job Queue. can trigger multiple jobs till queue is full.
 * @author sahas.n
 *
 */
@Component
public class JenkinsJobService {
	
	private JenkinsServer jenkinsServer;
	
	private static final Logger LOGGER = LogManager.getLogger(ScpStorageService.class);

	public JenkinsServer getJenkinsServer() {
		return jenkinsServer;
	}

	@Autowired
	public void setJenkinsServer(JenkinsServer jenkinsServer) {
		this.jenkinsServer = jenkinsServer;
	}
	
	private Map<String,Job> getJobs() throws IOException{
		return jenkinsServer.getJobs();
	}
	
	private Job getJob(String name) throws IOException {
		return getJobs().get(name);
	}
	
	public boolean doesJobExist(String jobName) throws IOException {
		return getJobs().containsKey(jobName);
	}
	
	/**
	 * Triggers the parametrized Jenkins job
	 * Need to deal with concurrency later - Build Number
	 * @param jobName
	 * @param params
	 * @return currBuildNumber
	 * @throws IOException
	 */
	public int triggerJob(String jobName, Map<String,String> params) throws IOException {
		LOGGER.info("Triggering job : " + jobName + " with Params : " +  params);
		JobWithDetails job = getJob(jobName).details();
		QueueReference queueRef = job.build(params, true);
		LOGGER.info("Reference URL: " + queueRef.getQueueItemUrlPart());
		return job.getNextBuildNumber();
	}
	
	/**
	 * Need to improve - Maintain a jobqueue
	 * @param jobName
	 * @return
	 * @throws IOException 
	 * @throws HttpResponseException 
	 */
	public boolean stopJob(String jobName, int buildNumber) throws HttpResponseException, IOException {
		LOGGER.info("Stopping the  job : " + jobName + " build: " + buildNumber);
		JobWithDetails job = getJob(jobName).details();
		if(job.getBuildByNumber(buildNumber) !=null) {
			BuildWithDetails build = job.getBuildByNumber(buildNumber).details();
			build.Stop(true);
		}
		return true;	
	}
}

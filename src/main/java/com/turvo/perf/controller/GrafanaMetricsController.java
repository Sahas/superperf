package com.turvo.perf.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.turvo.perf.grafana.client.GrafanaClient;

import lombok.Data;

/**
 * 
 * @author sahas.n
 *
 *
 */
@Data
@Controller
@RequestMapping("/visualisation")
public class GrafanaMetricsController {
	
	@Autowired
	@Qualifier("grafanaClient")
	private GrafanaClient grafana;
	
	private static final String DEFAULT_PERCENTILE_VALUE = "90";
	private static final String DEFAULT_SCALE_VALUE = "30";
	
	
	@GetMapping("/applications")
	public @ResponseBody Map<String,Object> getApplicationNamesOfDashBoard() throws IOException{
		return grafana.getApplicationNamesInDashboard();
	}
	
	@GetMapping("/runsTriggered")
	public @ResponseBody Map<String,Object> getRunsTriggered() throws IOException{
		return grafana.getAllRunNames();
	}
	
	@GetMapping("/{application}/transactions")
	public @ResponseBody Map<String,Object> getTransactionsOfApplication(@PathVariable("application") String application) throws IOException{
		return grafana.getTransactionsOfApplication(application);
	}
	
	//Reponse time
	
	@GetMapping("/{application}/{runName}/all/responseTime")
	public @ResponseBody Map<String,Object> getAvgResponseTimesForAllTransactions(@PathVariable("application") String applicationName,
			@PathVariable("runName") String runName, @RequestParam("pct") String percentile) throws IOException{
		percentile = getOrDefault(percentile, DEFAULT_PERCENTILE_VALUE);  
		return grafana.getAvgPercentileResponseTimeOfAllTransactions(applicationName, runName, percentile);
	}
	
	@GetMapping("/{application}/{runName}/{transaction}/responseTime")
	public @ResponseBody Map<String,Object> getAvgResponseTimesForTransaction(@PathVariable("application") String applicationName,
			@PathVariable("runName") String runName, @RequestParam("pct") String percentile, @PathVariable("transaction") String txnName) throws IOException{
		percentile = getOrDefault(percentile, DEFAULT_PERCENTILE_VALUE);  
		return grafana.getAvgPercentileResponseTimeOfTransaction(applicationName, runName, txnName, percentile);
	}
	
	@GetMapping("/{application}/{runName}/all/responseTimeSeries")
	public @ResponseBody Map<String,Object> getResponseTimeSeriesForAllTransactions(@PathVariable("application") String applicationName,
			@PathVariable("runName") String runName, @RequestParam("pct") String percentile, @RequestParam("scale") String scale) throws IOException{
		percentile = getOrDefault(percentile, DEFAULT_PERCENTILE_VALUE);
		scale = getOrDefault(scale, DEFAULT_SCALE_VALUE);
		return grafana.getPercentileTimeSeriesOfAllTransactionsOfApplication(applicationName, runName, percentile, scale);
	}
	
	@GetMapping("/{application}/{runName}/{transaction}/responseTimeSeries")
	public @ResponseBody Map<String,Object> getResponseTimeSeriesForTransaction(@PathVariable("application") String applicationName,
			@PathVariable("runName") String runName, @RequestParam("pct") String percentile, @PathVariable("transaction") String txnName,
			@RequestParam("scale") String scale) throws IOException{
		percentile = getOrDefault(percentile, DEFAULT_PERCENTILE_VALUE);  
		scale = getOrDefault(scale, DEFAULT_SCALE_VALUE);
		return grafana.getPercentileTimeSeriesOfTransactionOfApplication(applicationName, runName, txnName, percentile, scale);
	}
	
	// Throughput
	
	@GetMapping("/{application}/{runName}/all/tpm")
	public @ResponseBody Map<String,Object> getTPMForAllTransactions(@PathVariable("application") String applicationName,
			@PathVariable("runName") String runName) throws IOException{ 
		return grafana.getTPMOfAllTransactions(applicationName, runName);
	}
	
	@GetMapping("/{application}/{runName}/{transaction}/tpm")
	public @ResponseBody Map<String,Object> getTPMForTransaction(@PathVariable("application") String applicationName,
			@PathVariable("runName") String runName, @PathVariable("transaction") String txnName) throws IOException{
		return grafana.getTPMOfTransaction(applicationName, runName, txnName);
	}
	
	@GetMapping("/{application}/{runName}/all/throughputTimeSeries")
	public @ResponseBody Map<String,Object> getThroughputTimeSeriesForAllTransactions(@PathVariable("application") String applicationName,
			@PathVariable("runName") String runName, @RequestParam("scale") String scale) throws IOException{
		scale = getOrDefault(scale, DEFAULT_SCALE_VALUE);
		return grafana.getTPMTimeSeriesOfAllTransactions(applicationName, runName, scale);
	}
	
	@GetMapping("/{application}/{runName}/{transaction}/throughputTimeSeries")
	public @ResponseBody Map<String,Object> getThroughputTimeSeriesForTransaction(@PathVariable("application") String applicationName,
			@PathVariable("runName") String runName, @PathVariable("transaction") String txnName,
			@RequestParam("scale") String scale) throws IOException{
		scale = getOrDefault(scale, DEFAULT_SCALE_VALUE);
		return grafana.getTPMTimeSeriesOfTransaction(applicationName, runName, txnName, scale);
	}
	
	@GetMapping("/{application}/{runName}/all/failedRequestTimeSeries")
	public @ResponseBody Map<String,Object> getFailedRequestTimeSeriesForAllTransactions(@PathVariable("application") String applicationName,
			@PathVariable("runName") String runName, @RequestParam("scale") String scale) throws IOException{
		scale = getOrDefault(scale, DEFAULT_SCALE_VALUE);
		return grafana.getFailedRequestTimeSeriesOfAllTransactions(applicationName, runName, scale);
	}
	
	@GetMapping("/{application}/{runName}/{transaction}/failedRequestTimeSeries")
	public @ResponseBody Map<String,Object> getFailedRequestTimeSeriesForTransaction(@PathVariable("application") String applicationName,
			@PathVariable("runName") String runName, @PathVariable("transaction") String txnName,
			@RequestParam("scale") String scale) throws IOException{
		scale = getOrDefault(scale, DEFAULT_SCALE_VALUE);
		return grafana.getFailedRequestTimeSeriesOfTransaction(applicationName, runName, txnName, scale);
	}
	
	// Total Requests
	@GetMapping("/{application}/{runName}/all/requestCount")
	public @ResponseBody Map<String,Object> getRequestCountForAllTransactions(@PathVariable("application") String applicationName,
			@PathVariable("runName") String runName) throws IOException{ 
		return grafana.getTotalRequestCountOfRun(applicationName, runName);
	}
	
	@GetMapping("/{application}/{runName}/{transaction}/requestCount")
	public @ResponseBody Map<String,Object> getRequestCountForTransaction(@PathVariable("application") String applicationName,
			@PathVariable("runName") String runName, @PathVariable("transaction") String txnName) throws IOException{
		return grafana.getTotalRequestCountOfTransaction(applicationName, runName, txnName);
	}
	
	@GetMapping("/{application}/{runName}/all/failedCount")
	public @ResponseBody Map<String,Object> getFailedCountForAllTransactions(@PathVariable("application") String applicationName,
			@PathVariable("runName") String runName) throws IOException{ 
		return grafana.getFailedRequestCountOfRun(applicationName, runName);
	}
	
	@GetMapping("/{application}/{runName}/{transaction}/failedCount")
	public @ResponseBody Map<String,Object> getFailedCountForTransaction(@PathVariable("application") String applicationName,
			@PathVariable("runName") String runName, @PathVariable("transaction") String txnName) throws IOException{
		return grafana.getFailedRequestCountOfTransaction(applicationName, runName, txnName);
	}
	
	
	//Error Details
	@GetMapping("/{application}/{runName}/all/errorDetails")
	public @ResponseBody Map<String,Object> getErrorDetailsOfAllTransactions(@PathVariable("application") String applicationName,
			@PathVariable("runName") String runName) throws IOException{ 
		return grafana.getErrorDetailsOfAllTransactions(applicationName, runName);
	}
	
	@GetMapping("/{application}/{runName}/{transaction}/errorDetails")
	public @ResponseBody Map<String,Object> getErrorDetailsForTransaction(@PathVariable("application") String applicationName,
			@PathVariable("runName") String runName, @PathVariable("transaction") String txnName) throws IOException{
		return grafana.getErrorDetailsOfTransaction(applicationName, runName, txnName);
	}
	
	@GetMapping("/{application}/{runName}/getMeEverything")
	public @ResponseBody Map<String,Object> getAllDataForRun(@PathVariable("application") String applicationName,
			@PathVariable("runName") String runName) throws IOException{ 
		Map<String,Object> everythingMap = new HashMap<>();
		everythingMap.put("requestCount", grafana.getTotalRequestCountOfRun(applicationName, runName));
		everythingMap.put("failedCount", grafana.getFailedRequestCountOfRun(applicationName, runName));
		everythingMap.put("tpm", grafana.getTPMOfAllTransactions(applicationName, runName));
		everythingMap.put("90pctResponseTime", grafana.getAvgPercentileResponseTimeOfAllTransactions(applicationName, runName, "90"));
		//everythingMap.put("95pctResponseTime", grafana.getAvgPercentileResponseTimeOfAllTransactions(applicationName, runName, "95"));
		return everythingMap;
	}
	private String getOrDefault(String value, String defaultVal) {
		if(StringUtils.isEmpty(value) || !StringUtils.isNumeric(value)) {
			return defaultVal;
		}
		return value;
	}
	
	
}

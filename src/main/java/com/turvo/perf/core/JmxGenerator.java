package com.turvo.perf.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.protocol.http.util.HTTPArgument;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.backend.BackendListener;
import org.apache.jorphan.collections.HashTree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.blazemeter.jmeter.control.VirtualUserController;
import com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup;
import com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroupGui;
import com.turvo.perf.jmeter.domain.JSampler;
import com.turvo.perf.jmeter.domain.JTestPlan;

import lombok.Data;

@Component
@Data
public class JmxGenerator {
	
	private static final Logger LOGGER = LogManager.getLogger(JmxGenerator.class);
	
	@Value("${jmeter.path}")
	private String jmeterHome;
	
	@Value("${influx.url}")
	private String influxUrl;
	
	@Value("${influx.measurement}")
	private String measurement;
	
	@PostConstruct
	public void init() throws IllegalStateException{
		File home = new File(jmeterHome);
		if(!home.exists()) {
			throw new IllegalStateException("Jmeter Home Invalid");
		}
		File jmeterProperties = new File(home.getPath() + System.getProperty("file.separator") + "bin" + System.getProperty("file.separator") + "jmeter.properties");
        //JMeter initialization (properties, log levels, locale, etc)
        JMeterUtils.setJMeterHome(home.getPath());
        JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
        JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
        JMeterUtils.initLocale();
	}
	
//	public HashTree generateJmxFromJson(JTestPlan jTestPlan, String jmeterHome, String influxUrl, String measurement) throws FileNotFoundException, IOException {
	public HashTree generateJmxFromJson(JTestPlan jTestPlan) {	
		StandardJMeterEngine jmeter = new StandardJMeterEngine();
        HashTree testPlanTree = new HashTree();
        BackendListener influxListener = getBackendListenerElement(influxUrl,measurement);
        
        List<JSampler> jSamplers = new ArrayList<>();
        List<HTTPSamplerProxy> samplers = new ArrayList<>();
        jTestPlan.getTraffic().forEach((sampler) -> jSamplers.add(sampler));
        jSamplers.forEach(jSampler -> {
        	HTTPSamplerProxy proxy = new HTTPSamplerProxy();
        	URI uri;
			try {
				uri = new URI(jSampler.getUrl());
				proxy.setDomain(uri.getHost());
	        	proxy.setProtocol(uri.getScheme());
	        	proxy.setPort(uri.getPort());
	        	proxy.setPath(uri.getPath());
	        	// Issue with blazemeter - formadata vs Query Params
	        	if(jSampler.getBody() != null && StringUtils.isNotEmpty(jSampler.getBody().toString())) {
	        		proxy.setPostBodyRaw(true);
	        		if(StringUtils.isNotBlank(uri.getQuery())) {
	        			proxy.setPath(uri.getPath() + "?" + uri.getRawQuery());
	        		}
	        		String body = jSampler.getBody().isArray()? jSampler.getBody().get(0).textValue() :jSampler.getBody().textValue(); 
	        		HTTPArgument requestBody = new HTTPArgument("body", body);
	        		requestBody.setAlwaysEncoded(false);
	        		proxy.getArguments().addArgument(requestBody);
	        	} else {
	        		Arguments queryParamArgs = proxy.getArguments();
		        	
		        	URLEncodedUtils.parse(uri, Charset.forName("UTF-8")).forEach( queryParam -> {
		        		HTTPArgument queryParamArg = new HTTPArgument(queryParam.getName(), queryParam.getValue(), "=");
		        		queryParamArg.setAlwaysEncoded(true);
		        		queryParamArg.setUseEquals(true);
		        		queryParamArg.setProperty("name", queryParam.getName());
		        		queryParamArgs.addArgument(queryParamArg);
		        	});
	        	}
	        	proxy.setMethod(jSampler.getMethod());
	        	proxy.setName(jSampler.getLabel());
	        	proxy.setFollowRedirects(true);
	        	proxy.setAutoRedirects(false);
	        	proxy.setUseKeepAlive(true);
	        	
	        	proxy.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
	        	proxy.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
	        	
	        	samplers.add(proxy);
			} catch (URISyntaxException e) {
				LOGGER.error("Incorrect URI syntax while generating script : " + e.getMessage());
			}
        	
        });
        
        ConcurrencyThreadGroup threadGroup = new ConcurrencyThreadGroup();
        threadGroup.setName("First Thread Group");
        threadGroup.setProperty(ThreadGroup.ON_SAMPLE_ERROR, ThreadGroup.ON_SAMPLE_ERROR_CONTINUE);
        threadGroup.setProperty("TargetLevel", "1");
        threadGroup.setProperty("RampUp", "1");
        threadGroup.setProperty("Steps", "1");
        threadGroup.setProperty("Hold", "1");
        threadGroup.setProperty("Unit", "M");
        threadGroup.setProperty(TestElement.TEST_CLASS, ConcurrencyThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ConcurrencyThreadGroupGui.class.getName());
        
        TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

        // Construct Test Plan from previously initialized elements
        testPlanTree.add(testPlan);
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(samplers);
        threadGroupHashTree.add(influxListener);
        //SaveService.saveTree(testPlanTree, new FileOutputStream(jmeterHome + System.getProperty("file.separator")  + "example2.jmx"));
        return testPlanTree;
	}
	
	public void writeJmxIntoFile(HashTree testPlanTree, File file) throws FileNotFoundException, IOException {
		
		try( FileOutputStream output = new FileOutputStream(file)){
			SaveService.saveTree(testPlanTree, output);
		}
	}
	
	public BackendListener getBackendListenerElement(String influxUrl, String measurement) {
		BackendListener listener = new BackendListener();
		listener.getArguments().addArgument(new Argument("influxdbMetricsSender","org.apache.jmeter.visualizers.backend.influxdb.HttpMetricsSender", "="));
		listener.getArguments().addArgument(new Argument("influxdbUrl",influxUrl, "="));
		listener.getArguments().addArgument(new Argument("application","${__P(applicationName)}", "="));
		listener.getArguments().addArgument(new Argument("measurement",measurement, "="));
		listener.getArguments().addArgument(new Argument("summaryOnly","false", "="));
		listener.getArguments().addArgument(new Argument("samplersRegex",".*", "="));
		listener.getArguments().addArgument(new Argument("percentiles","50;90;95;99", "="));
		listener.getArguments().addArgument(new Argument("TAG_currentRunName","${__P(currentPerfRunName)}", "="));
		listener.setProperty(TestElement.GUI_CLASS, "BackendListenerGui");
		listener.setProperty(TestElement.TEST_CLASS, "BackendListener");
		listener.setProperty(TestElement.NAME, "Backend Listener");
		listener.setProperty("enabled", true);
		
		return listener;
	}
}

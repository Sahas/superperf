package com.turvo.perf.jmeter.explore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.jmeter.threads.ThreadGroup;
import org.apache.commons.io.FileUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.protocol.http.util.HTTPArgument;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.junit.Before;
import org.junit.Test;

import com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turvo.perf.core.JmxGenerator;
import com.turvo.perf.jmeter.domain.JTestPlan;

public class HttpSamplerTest {
	
	File jmeterHome = null;
	String slash = System.getProperty("file.separator");
	
	@Before
	public void setup() {
		this.jmeterHome = new File("/Users/sahas.n/Downloads/apache-jmeter-5.0");
        System.out.println("Jmeter home is : " + System.getProperty("jmeter.home"));

        if (jmeterHome.exists()) {
            File jmeterProperties = new File(jmeterHome.getPath() + slash + "bin" + slash + "jmeter.properties");
            //JMeter initialization (properties, log levels, locale, etc)
            JMeterUtils.setJMeterHome(jmeterHome.getPath());
            JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
            JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
            JMeterUtils.initLocale();
        }

	}
	
	@Test
	public void verifyJmxGeneratedProperlyForSampler() throws FileNotFoundException, IOException {
                //JMeter Engine
                StandardJMeterEngine jmeter = new StandardJMeterEngine();

                // JMeter Test Plan, basically JOrphan HashTree
                HashTree testPlanTree = new HashTree();

                // First HTTP Sampler - open example.com
                HTTPSamplerProxy examplecomSampler = new HTTPSamplerProxy();
                examplecomSampler.setDomain("example.com");
                examplecomSampler.setPort(80);
                examplecomSampler.setPath("/");
                examplecomSampler.setMethod("GET");
                examplecomSampler.setName("Open example.com");
                examplecomSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
                examplecomSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());


                // Second HTTP Sampler - open blazemeter.com
                HTTPSamplerProxy blazemetercomSampler = new HTTPSamplerProxy();
                blazemetercomSampler.setDomain("blazemeter.com");
                blazemetercomSampler.setPort(80);
                blazemetercomSampler.setPath("/");
                blazemetercomSampler.setMethod("GET");
                blazemetercomSampler.setName("Open blazemeter.com");
                blazemetercomSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
                blazemetercomSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
                HTTPArgument requestBody = new HTTPArgument("body", "{\"input\":\"100\"}", "=");
                requestBody.setAlwaysEncoded(false);
                blazemetercomSampler.getArguments().addArgument(requestBody);
                blazemetercomSampler.setPostBodyRaw(true);


                // Loop Controller
                LoopController loopController = new LoopController();
                loopController.setLoops(1);
                loopController.setFirst(true);
                loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
                loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
                loopController.initialize();

                // Thread Group
                ThreadGroup threadGroup = new ThreadGroup();
                threadGroup.setName("Example Thread Group");
                threadGroup.setNumThreads(1);
                threadGroup.setRampUp(1);
                threadGroup.setSamplerController(loopController);
                threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
                threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

                // Test Plan
                TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
                testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
                testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
                testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

                // Construct Test Plan from previously initialized elements
                testPlanTree.add(testPlan);
                HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
                threadGroupHashTree.add(blazemetercomSampler);
                threadGroupHashTree.add(examplecomSampler);

                // save generated test plan to JMeter's .jmx file format
                SaveService.saveTree(testPlanTree, new FileOutputStream(jmeterHome + slash + "example.jmx"));

                //add Summarizer output to get test progress in stdout like:
                // summary =      2 in   1.3s =    1.5/s Avg:   631 Min:   290 Max:   973 Err:     0 (0.00%)
//                Summariser summer = null;
//                String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
//                if (summariserName.length() > 0) {
//                    summer = new Summariser(summariserName);
//                }
//
//
//                // Store execution results into a .jtl file
//                String logFile = jmeterHome + slash + "example.jtl";
//                ResultCollector logger = new ResultCollector(summer);
//                logger.setFilename(logFile);
//                testPlanTree.add(testPlanTree.getArray()[0], logger);
//
//                // Run Test Plan
//                jmeter.configure(testPlanTree);
//                jmeter.run();
//
//                System.out.println("Test completed. See " + jmeterHome + slash + "example.jtl file for results");
                System.out.println("JMeter .jmx script is available at " + jmeterHome + slash + "example.jmx");
                System.exit(0);

	}
	
	@Test
	public void createSamplerWithPreAndPostProcessors() {
		StandardJMeterEngine jmeter = new StandardJMeterEngine();
		
		HashTree testPlanTree = new HashTree();
		HTTPSamplerProxy shipmentStatusUpdateSampler = new HTTPSamplerProxy();
		shipmentStatusUpdateSampler.setDomain("stage-app-charlie.turvo.com");
		shipmentStatusUpdateSampler.setPort(8080);
		shipmentStatusUpdateSampler.setPath("/shipments/status/${shipmentId}?fullResponse=true");
		shipmentStatusUpdateSampler.setMethod("PUT");
		HTTPArgument requestBody = new HTTPArgument("body","{\"notes\":\"\",\"description\":\"Ready for billing\",\"code\":{\"id\":100168,\"key\":\"2108\",\"value\":\"Ready for billing\"},\"attributes\":{\"documents\":[]},\"timezone\":\"America/Los_Angeles\",\"tags\":[],\"componentKey\":11033}", "=");
		requestBody.setAlwaysEncoded(false);
		shipmentStatusUpdateSampler.getArguments().addArgument(requestBody);
		
		LoopController loopController = new LoopController();
        loopController.setLoops(1);
        loopController.setFirst(true);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.initialize();
		
        ConcurrencyThreadGroup threadGroup = new ConcurrencyThreadGroup();
//        threadGroup.MAIN_CONTROLLER
//        threadGroup.setName("Pre and Post Example Thread Group");
        threadGroup.setNumThreads(1);
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        
        
        
		//SaveService.saveTree(tree, new FileOutputStream(jmeterHome + slash + "exampleWithPrePost.jmx"));
	}
	
	@Test
	public void testSuperPerf() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		String superPerfJson = FileUtils.readFileToString(new File("/Users/sahas.n/Downloads/Create-shipment-JMeter.json"), Charset.forName("UTF-8"));
		JTestPlan jTestPlan = mapper.readValue(superPerfJson, JTestPlan.class);
		JmxGenerator scriptGen = new JmxGenerator();
		//scriptGen.init("/Users/sahas.n/Downloads/apache-jmeter-5.0");
		//scriptGen.generateJmxFromJson(jTestPlan, "/Users/sahas.n/Downloads/apache-jmeter-5.0", "influx.url", "jmeter");
	}
}

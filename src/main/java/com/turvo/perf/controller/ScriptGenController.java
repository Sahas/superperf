package com.turvo.perf.controller;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.turvo.perf.core.JmxFileUploadIntegrationService;
import com.turvo.perf.jmeter.domain.JTestPlan;

import lombok.Data;

@Controller
@RequestMapping("/converter")
@Data
public class ScriptGenController {
	
	@Resource(name="integrationService")
	//@Autowired
	private JmxFileUploadIntegrationService integrationService;
	
	private static final Logger LOGGER = LogManager.getLogger(ScriptGenController.class);
	
	@PostMapping("/json-to-jmeter")
	public void createAndUploadJmxFile(HttpServletRequest request, HttpServletResponse response,
			@RequestBody JTestPlan jTestPlan) throws FileNotFoundException, IOException {
		
		integrationService.generateAndStoreJmxFile(jTestPlan);
		
	}
}

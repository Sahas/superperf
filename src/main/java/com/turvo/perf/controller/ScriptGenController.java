package com.turvo.perf.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/converter")
public class ScriptGenController {
	
	private static final Logger LOGGER = LogManager.getLogger(ScriptGenController.class);
	
	@PostMapping("/json-to-jmeter")
	public void createAndSendJmxFile(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String json) {
		
		
	}
}

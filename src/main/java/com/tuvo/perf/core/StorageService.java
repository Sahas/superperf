package com.tuvo.perf.core;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

/**
 * Store the jmx file and create an isolated env. Generate JobDetails
 * 
 * @author sahas.n
 *
 */
@FunctionalInterface
public interface StorageService {

	void saveFile(MultipartFile file, Map<String,String> pathDetails) throws IllegalStateException, IOException;
	 
}

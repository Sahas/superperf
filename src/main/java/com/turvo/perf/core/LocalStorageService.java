package com.turvo.perf.core;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import lombok.Data;

@Data
public class LocalStorageService implements StorageService {
	
	private String baseFolder;

	@Override
	public void saveFile(File file, Map<String, String> pathDetails) throws IllegalStateException, IOException {
		// TODO Auto-generated method stub

	}

}

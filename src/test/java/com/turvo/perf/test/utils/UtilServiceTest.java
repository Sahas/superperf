package com.turvo.perf.test.utils;

import java.net.URI;
import java.net.URISyntaxException;

public class UtilServiceTest {
	
	public static void main(String[] args) throws URISyntaxException {
		String url = "https://stage-app.turvo.com/api/shipments/list?filter=%7B%22pageSize%22%3A8%2C%22start%22%3A0%2C%22criteria%22%3A%5B%7B%22key%22%3A%22status.category%22%2C%22function%22%3A%22eq%22%2C%22value%22%3A%22bill%22%7D%2C%7B%22key%22%3A%22status.code.id%22%2C%22function%22%3A%22nin%22%2C%22values%22%3A%5B100173%5D%7D%5D%2C%22sortBy%22%3A%22lastUpdatedOn%22%2C%22sortDirection%22%3A%22desc%22%7D&extendedAttributes=true&card=bill";
		URI uri = new URI(url);
		System.out.println(uri.getHost());
		System.out.println(uri.getPath());
		System.out.println(uri.getScheme());
		System.out.println(uri.getQuery());
	}
}

package com.turvo.perf.grafana.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turvo.perf.grafana.domain.QueryResults;
import com.turvo.perf.grafana.domain.Series;
import com.turvo.perf.utils.UtilService;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Enhancements : Per specific user. Integration with Jenkins. Use of local DB to map the builds and tags unique per user.
 * @author sahas.n
 *
 */


/**
 * List of APIs required
 * ----------------------
 * 1. Get Application names - Done
 * 2. Get All run names  - Done
 * 3. Get transaction names - Done -> No way to relate these now. Should add later  -> Pending
 * 4. Get percentile(90,95,avg) results for transaction of a particular name - Done
 * 5. Get Throughput of transaction/overall -> Scale as well as avg - Done
 * 6. Get success and failures of transaction/overall - Done
 * 7. Error Details of Transaction/ all transactions - Done
 */


@Component
public class GrafanaClient {
	
	private static final Logger LOGGER = LogManager.getLogger(GrafanaClient.class);
	
	private GrafanaConfiguration config;
	
	private GrafanaService grafanaService;
	
	private static final ObjectMapper mapper =
		      new ObjectMapper()
		          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
		          .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
	
	public GrafanaConfiguration getConfig() {
		return config;
	}
	
	public void setConfig(GrafanaConfiguration grafanaConfiguration) {
		this.config = grafanaConfiguration;
	}

	public GrafanaService getGrafanaService() {
		return grafanaService;
	}

	public void setGrafanaService(GrafanaService grafanaService) {
		this.grafanaService = grafanaService;
	}

	@Autowired
	public GrafanaClient(GrafanaConfiguration config) {
		this(config, new OkHttpClient());
	}
	
	public GrafanaClient(GrafanaConfiguration config, OkHttpClient client){
		this.config = config;
		client = client.newBuilder().cookieJar(new CookieJar() {
			private List<Cookie> cookies;
			
			@Override
			public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
				if(url.encodedPath().endsWith("login")) {
					this.cookies = new ArrayList<>(cookies);
				}
			}

			@Override
			public List<Cookie> loadForRequest(HttpUrl url) {
				if(!url.encodedPath().endsWith("login") && cookies != null) {
					return this.cookies;
				}
				return Collections.emptyList();
			}
			
		}).build();
		HttpUrl url = new HttpUrl.Builder().scheme("http").host(config.getHost()).port(config.getPort()).build();
//		Interceptor headersInterceptor = (chain) ->{
//			Request request = chain.request();
//			LOGGER.info("Intercepting the request: " + request.url().toString());
//			if(!request.url().encodedPath().equalsIgnoreCase("login")) {
//				request = request.newBuilder().addHeader("Cookie", value)
//			}
//			return chain.proceed(request);
//		};
		Retrofit retrofit = new Retrofit.Builder().baseUrl(url).client(client).addConverterFactory(JacksonConverterFactory.create(mapper)).build();
		this.grafanaService = retrofit.create(GrafanaService.class);
	}
	
	@PostConstruct
	//@Scheduled(fixedRate = 1*60*60)
	public void login() throws IOException {
		LOGGER.info("Logging into grafana");
		Map<String,String> loginParams = new HashMap<>();
		loginParams.put("user", this.config.getUser());
		loginParams.put("email", "");
		loginParams.put("password", this.config.getPassword());
		LOGGER.info("Logging into grafana with user: " + this.config.getUser());
		this.grafanaService.login(loginParams, "1").execute();
	}
	
	/**
	 * Enhancements : Make it cacheable later
	 * @return
	 * @throws IOException 
	 */
	public Map<String, Object> getApplicationNamesInDashboard() throws IOException{
		LOGGER.info("Getting application names of the dashboard");
		
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), GrafanaQueries.GET_APPLICATION_NAMES_QUERY).execute();
		QueryResults results = response.body();
		return getValuesFromQueryResponse(results, 0, "applicationNames");
		
	}
	
	public Map<String,Object> getTransactionsOfApplication(String application) throws IOException{
		LOGGER.info("Getting txn names of the application: " + application);
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("application", application);
		String query = StringSubstitutor.replace(GrafanaQueries.GET_TRANSACTION_NAMES_QUERY, paramMap);
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), query).execute();
		// Change this later
		return getValuesFromQueryResponse(response.body(), 0, "transactions");
	}
	
	public Map<String,Object> getAllRunNames() throws IOException{
		LOGGER.info("Getting all run names: ");
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), GrafanaQueries.GET_PERF_RUN_NAMES_QUERY).execute();
		// Change this later
		return getValuesFromQueryResponse(response.body(), 0, "runNames");
	}
	
	public Map<String,Object> getPercentileTimeSeriesOfTransactionOfApplication(String applicationName, String runName, String txnName, String percentile, String scale) throws IOException{
		LOGGER.info("Getting 95pct of the transaction: " + txnName + " for run :" + runName );
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("applicationName", applicationName);
		paramMap.put("pct", percentile);
		paramMap.put("txnName", txnName);
		paramMap.put("runName", runName);
		paramMap.put("scale", scale);
		String query = StringSubstitutor.replace(GrafanaQueries.GET_PCT_TIMESCALE_TXN_RESPONSE_TIME_QUERY, paramMap);
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), query).execute();
		return getValuesFromQueryResponse(response.body(), 0, txnName);
	}
	
	public Map<String,Object> getPercentileTimeSeriesOfAllTransactionsOfApplication(String applicationName, String runName, String percentile, String scale) throws IOException{
		LOGGER.info("Getting pct time series of all the transactions for run :" + runName );
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("applicationName", applicationName);
		paramMap.put("pct", percentile);
		paramMap.put("runName", runName);
		paramMap.put("scale", scale);
		String query = StringSubstitutor.replace(GrafanaQueries.GET_PCT_TIMESCALE_ALL_TXNS_RESPONSE_TIME_QUERY, paramMap);
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), query).execute();
		return getValuesFromQueryResponse(response.body(), 0, "transaction");
	}
	
	public Map<String,Object> getAvgPercentileResponseTimeOfAllTransactions(String applicationName, String runName, String percentile) throws IOException{
		LOGGER.info("Getting avg pct of all the transactions for run :" + runName );
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("pct", percentile);
		paramMap.put("applicationName", applicationName);
		paramMap.put("runName", runName);
		String query = StringSubstitutor.replace(GrafanaQueries.GET_PCT_AVG_ALL_TXNS_RESPONSE_TIME_QUERY, paramMap);
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), query).execute();
		return getValuesFromQueryResponse(response.body(), 0, "transaction");
	}
	
	public Map<String,Object> getAvgPercentileResponseTimeOfTransaction(String applicationName, String runName, String txnName, String percentile) throws IOException{
		LOGGER.info("Getting avg pct of " +  "transaction: " + txnName + ", for run :" + runName );
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("pct", percentile);
		paramMap.put("applicationName", applicationName);
		paramMap.put("runName", runName);
		paramMap.put("txnName", txnName);
		String query = StringSubstitutor.replace(GrafanaQueries.GET_PCT_AVG_TXN_RESPONSE_TIME_QUERY, paramMap);
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), query).execute();
		return getValuesFromQueryResponse(response.body(), 0, txnName);
	}
	
	public Map<String,Object> getTPMOfTransaction(String applicationName, String runName, String txnName) throws IOException{
		LOGGER.info("Getting throughput of transaction: " + txnName + ", for run :" + runName );
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("applicationName", applicationName);
		paramMap.put("runName", runName);
		paramMap.put("txnName", txnName);
		String query = StringSubstitutor.replace(GrafanaQueries.AVG_REQ_THROUGHPUT_OF_TXN_PER_MIN, paramMap);
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), query).execute();
		return getValuesFromQueryResponse(response.body(), 0, txnName);
	}
	
	public Map<String,Object> getTPMTimeSeriesOfTransaction(String applicationName, String runName, String txnName, String scale) throws IOException{
		LOGGER.info("Getting throughput of transaction: " + txnName + ", for run :" + runName );
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("applicationName", applicationName);
		paramMap.put("runName", runName);
		paramMap.put("txnName", txnName);
		paramMap.put("scale", scale);
		String query = StringSubstitutor.replace(GrafanaQueries.REQUEST_THROUGHPUT_TIMESERIES_OF_TXN_DURING_RUN, paramMap);
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), query).execute();
		return getValuesFromQueryResponse(response.body(), 0, txnName);
	}
	
	public Map<String,Object> getTPMOfAllTransactions(String applicationName, String runName) throws IOException{
		LOGGER.info("Getting throughput of all transactions: for run :" + runName );
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("applicationName", applicationName);
		paramMap.put("runName", runName);
		String query = StringSubstitutor.replace(GrafanaQueries.AVG_REQ_THROUGHPUT_OF_ALL_TXNS_PER_MIN, paramMap);
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), query).execute();
		return getValuesFromQueryResponse(response.body(), 0, "transaction");
	}
	
	public Map<String,Object> getTPMTimeSeriesOfAllTransactions(String applicationName, String runName, String scale) throws IOException{
		LOGGER.info("Getting throughput of all transactions: for run :" + runName );
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("applicationName", applicationName);
		paramMap.put("runName", runName);
		paramMap.put("scale", scale);
		String query = StringSubstitutor.replace(GrafanaQueries.REQUEST_THROUGHPUT_TIMESERIES_DURING_RUN, paramMap);
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), query).execute();
		return getValuesFromQueryResponse(response.body(), 0, "transaction");
	}
	
	public Map<String,Object> getTotalRequestCountOfRun(String applicationName, String runName) throws IOException{
		LOGGER.info("Getting total requests for run :" + runName );
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("applicationName", applicationName);
		paramMap.put("runName", runName);
		String query = StringSubstitutor.replace(GrafanaQueries.TOTAL_REQUESTS_DURING_RUN, paramMap);
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), query).execute();
		return getValuesFromQueryResponse(response.body(), 0, "requestsCount");
	}
	
	public Map<String,Object> getTotalRequestCountOfTransaction(String applicationName, String runName, String txnName) throws IOException{
		LOGGER.info("Getting total requests for run :" + runName + " for transaction : " + txnName);
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("applicationName", applicationName);
		paramMap.put("runName", runName);
		paramMap.put("txnName", txnName);
		String query = StringSubstitutor.replace(GrafanaQueries.TOTAL_REQUESTS_OF_TXN_DURING_RUN, paramMap);
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), query).execute();
		return getValuesFromQueryResponse(response.body(), 0, txnName);
	}
	
	public Map<String,Object> getFailedRequestCountOfRun(String applicationName, String runName) throws IOException{
		LOGGER.info("Getting failed request count for run :" + runName );
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("applicationName", applicationName);
		paramMap.put("runName", runName);
		String query = StringSubstitutor.replace(GrafanaQueries.FAILED_REQUESTS_DURING_RUN, paramMap);
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), query).execute();
		return getValuesFromQueryResponse(response.body(), 0, "failedCount");
	}
	
	public Map<String,Object> getFailedRequestCountOfTransaction(String applicationName, String runName, String txnName) throws IOException{
		LOGGER.info("Getting failed request count for run :" + runName );
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("applicationName", applicationName);
		paramMap.put("runName", runName);
		paramMap.put("txnName", txnName);
		String query = StringSubstitutor.replace(GrafanaQueries.FAILED_REQUESTS_OF_TXN_DURING_RUN, paramMap);
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), query).execute();
		return getValuesFromQueryResponse(response.body(), 0, "failedCount");
	}
	
	public Map<String,Object> getFailedRequestTimeSeriesOfAllTransactions(String applicationName, String runName, String scale) throws IOException{
		LOGGER.info("Getting failed request timeseries for run :" + runName);
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("applicationName", applicationName);
		paramMap.put("runName", runName);
		paramMap.put("scale", scale);
		String query = StringSubstitutor.replace(GrafanaQueries.FAILED_REQUESTS_TIMESERIES_DURING_RUN, paramMap);
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), query).execute();
		return getValuesFromQueryResponse(response.body(), 0, "transaction");
	}
	
	public Map<String,Object> getFailedRequestTimeSeriesOfTransaction(String applicationName, String runName, String txnName, String scale) throws IOException{
		LOGGER.info("Getting failed request timeseries for run :" + runName + " for transaction : " + txnName);
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("applicationName", applicationName);
		paramMap.put("runName", runName);
		paramMap.put("txnName", txnName);
		paramMap.put("scale", scale);
		String query = StringSubstitutor.replace(GrafanaQueries.FAILED_REQUESTS_TIMESERIES_OF_TXN_DURING_RUN, paramMap);
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), query).execute();
		return getValuesFromQueryResponse(response.body(), 0, txnName);
	}
	
	public Map<String,Object> getErrorDetailsOfAllTransactions(String applicationName, String runName) throws IOException{
		LOGGER.info("Getting error details for run :" + runName);
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("applicationName", applicationName);
		paramMap.put("runName", runName);
		String query = StringSubstitutor.replace(GrafanaQueries.ERROR_INFO_ALL_TXNS_DURING_RUN, paramMap);
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), query).execute();
		return getValuesFromQueryResponse(response.body(), 0, "transaction");
	}
	
	public Map<String,Object> getErrorDetailsOfTransaction(String applicationName, String runName, String txnName) throws IOException{
		LOGGER.info("Getting error details for run :" + runName + " for transaction : " + txnName);
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("applicationName", applicationName);
		paramMap.put("runName", runName);
		paramMap.put("txnName", txnName);
		String query = StringSubstitutor.replace(GrafanaQueries.ERROR_INFO_OF_TXN_DURING_RUN, paramMap);
		Response<QueryResults> response = this.grafanaService.getResultForQuery(this.config.getDatasource(), query).execute();
		return getValuesFromQueryResponse(response.body(), 0, txnName);
	}
	
	private Map<String, Object> getValuesFromQueryResponse(QueryResults results, int statement, String tagName){
		
		// Structure: TagName : {"columns": ["col1","col2"], "values":[["val1, "val2""],[],[]]}
		
		List<Series> series = results.getStatementResults().get(statement).getSeries();
		Map<String,Object> valueMap = new HashMap<>();
		
		for(Series element : series) {
			String tag = !MapUtils.isEmpty(element.getTagNames()) && element.getTagNames().containsKey(tagName)? element.getTagNames().get(tagName) : tagName;
			valueMap.put(tag, new HashMap<String,Object>());
			((Map<String,Object>)valueMap.get(tag)).put("columns", element.getColumns());
			((Map<String,Object>)valueMap.get(tag)).put("values", element.getValues());
		}
//		Map<Integer,String> colsMap = new HashMap<>();
//		for(int idx=0; idx< series.getColumns().size(); idx++) {
//			if(colNames.contains(series.getColumns().get(idx))){
//				colsMap.put(idx, series.getColumns().get(idx));
//			}
//		}
//		
//		colsMap.forEach((key, value) -> valueMap.put(value, new ArrayList<>()));
//		String[][] values = series.getValues();
//		for(int i=0; i< values.length; i++) {
//			String[] row = values[i];
//			colsMap.forEach((colIdx, colName) -> {
//				List<String> colValues = (List<String>)valueMap.get(colName);
//				colValues.add(row[colIdx]);
//			});
//		}
		return valueMap;
	}
	
	
	
//	public static void main(String[] args) {
//		GrafanaConfiguration config = new GrafanaConfiguration().host("ec2-54-149-220-134.us-west-2.compute.amazonaws.com").port(3000).user("admin").password("admin").datasource("turvo_performance");
//		GrafanaClient client = new GrafanaClient(config);
//		try {
//			client.login();
////			System.out.println(client.getAllRunNames());
////			System.out.println(client.getApplicationNamesInDashboard());
////			System.out.println(client.getTransactionsOfApplication("TurvoDemo"));
//			//System.out.println(UtilService.getParameters(client.getPercentileTimeSeriesOfAllTransactionsOfApplication("95", "Perf_Stage_Charlie_TurvoPerf_CR2280_AfterIndexes_NOV2_R2", "50")));
////			System.out.println(UtilService.getParameters(client.getPercentileTimeSeriesOfTransactionOfApplication("95", "Create Carrier Payment", "Perf_Stage_Charlie_TurvoPerf_CR2280_AfterIndexes_NOV2_R2", "50")));
////			System.out.println(UtilService.getParameters(client.getAvgPercentileResponseTimeOfTransaction("95", "TurvoDemo","Perf_Stage_Charlie_TurvoPerf_CR2280_AfterIndexes_NOV2_R2", "Create Carrier Payment")));
////			System.out.println(UtilService.getParameters(client.getAvgPercentileResponseTimeOfAllTransactions("95", "TurvoDemo","Perf_Stage_Charlie_TurvoPerf_CR2280_AfterIndexes_NOV2_R2")));
//					//("Create Carrier Payment", "Perf_Stage_Charlie_TurvoPerf_CR2280_AfterIndexes_NOV2_R2", "50"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
	
	
	
}

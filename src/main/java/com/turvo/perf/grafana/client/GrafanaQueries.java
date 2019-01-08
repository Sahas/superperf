package com.turvo.perf.grafana.client;

public final class GrafanaQueries {
	
	private GrafanaQueries() {}
	
	public static String GET_APPLICATION_NAMES_QUERY = "SHOW TAG VALUES FROM \"jmeter\" WITH KEY = \"application\"";
	public static String GET_TRANSACTION_NAMES_QUERY = "SHOW TAG VALUES FROM \"jmeter\" WITH KEY = \"transaction\" WHERE \"application\" =~ /^${application}$/ AND \"transaction\" != 'all'  AND \"transaction\" !~ /[Ii]nternal/ AND \"transaction\" !~ /[Dd]ummy/";
	public static String GET_PERF_RUN_NAMES_QUERY = "SHOW TAG VALUES FROM \"jmeter\" WITH KEY = \"currentRunName\"";
	
	public static String GET_PCT_TIMESCALE_TXN_RESPONSE_TIME_QUERY = "SELECT last(\"pct${pct}.0\") FROM \"jmeter\" WHERE (\"transaction\" =~ /^${txnName}$/ AND \"statut\" = 'ok' AND \"currentRunName\"=~/^${runName}$/) GROUP BY time(${scale}s) fill(null)";
	public static String GET_PCT_TIMESCALE_ALL_TXNS_RESPONSE_TIME_QUERY = "SELECT last(\"pct${pct}.0\") FROM \"jmeter\" WHERE (\"statut\" = 'ok' AND \"currentRunName\"=~/^${runName}$/ AND \"transaction\" !~ /[Ii]nternal/ AND \"transaction\" !~ /[Dd]ummy/ AND \"transaction\" !~ /[Aa]ll/ AND \"transaction\" != 'ClearCSVFiles' AND \"transaction\"!='LoadProperties') GROUP BY time(${scale}s) fill(null)";
	public static String GET_PCT_AVG_ALL_TXNS_RESPONSE_TIME_QUERY_USETIMELATER_1 = "SELECT last(\"pct90.0\") FROM \"$measurement_name\" WHERE (\"transaction\" =~ /^$transaction$/ AND \"statut\" = 'ok' AND \"currentRunName\"=~/^$RunTags$/) AND $timeFilter GROUP BY time($__interval) fill(null)";
	public static String GET_PCT_AVG_ALL_TXNS_RESPONSE_TIME_QUERY_USETIMELATER = "SELECT  mean(\"pct${pct}.0\") as \"${pct}pct\"  FROM \"jmeter\"  WHERE (\"application\" =~ /^${applicationName}$/ AND \"currentRunName\" =~ /^${runName}$/ AND \"transaction\" !~ /[Ii]nternal/ AND \"transaction\" !~ /[Dd]ummy/ AND \"transaction\" !~ /[Aa]ll/ AND \"transaction\" != 'ClearCSVFiles' AND \"transaction\"!='LoadProperties') AND time >= 1546236429684ms and time <= 1546239098305ms GROUP BY transaction";
	public static String GET_PCT_AVG_ALL_TXNS_RESPONSE_TIME_QUERY = "SELECT  mean(\"pct${pct}.0\") as \"${pct}pct\"  FROM \"jmeter\"  WHERE (\"application\" =~ /^${applicationName}$/ AND \"currentRunName\" =~ /^${runName}$/ AND \"transaction\" !~ /[Ii]nternal/ AND \"transaction\" !~ /[Dd]ummy/ AND \"transaction\" !~ /[Aa]ll/ AND \"transaction\" != 'ClearCSVFiles' AND \"transaction\"!='LoadProperties') GROUP BY transaction";
	public static String GET_PCT_AVG_ALL_TXNS_RESPONSE_TIME_QUERY_WITHOUTAPP = "SELECT  mean(\"pct${pct}.0\") as \"${pct}pct\"  FROM \"jmeter\"  WHERE (\"currentRunName\" =~ /^${runName}$/ AND \"transaction\" !~ /[Ii]nternal/ AND \"transaction\" !~ /[Dd]ummy/ AND \"transaction\" !~ /[Aa]ll/ AND \"transaction\" != 'ClearCSVFiles' AND \"transaction\"!='LoadProperties') GROUP BY transaction";
	public static String GET_PCT_AVG_TXN_RESPONSE_TIME_QUERY = "SELECT  mean(\"pct${pct}.0\") as \"${pct}pct\"  FROM \"jmeter\"  WHERE (\"application\" =~ /^${applicationName}$/ AND \"currentRunName\" =~ /^${runName}$/ AND \"transaction\" =~ /^${txnName}$/ )";
	public static String GET_PCT_AVG_TXN_RESPONSE_TIME_QUERY_WITHOUTAPP = "SELECT  mean(\"pct${pct}.0\") as \"${pct}pct\"  FROM \"jmeter\"  WHERE (\"currentRunName\" =~ /^${runName}$/ AND \"transaction\" =~ /^${txnName}$/ )";
	
	public static String TOTAL_REQUEST_DURING_RUN_ORIG = "SELECT sum(\"count\")  FROM \"$measurement_name\" WHERE (\"application\" =~ /^$application$/ AND \"transaction\" = 'all' AND \"currentRunName\"=~/^$RunTags$/) AND $timeFilter GROUP BY time($__interval) fill(null)";
	//Later add timefilter
	public static String TOTAL_REQUESTS_DURING_RUN = "SELECT sum(\"count\") as count FROM \"jmeter\" WHERE (\"application\" =~ /^${applicationName}$/ AND \"transaction\" = 'all' AND \"currentRunName\"=~/^${runName}$/) fill(null)";
	public static String TOTAL_REQUESTS_DURING_RUN_WITHOUTAPP = "SELECT sum(\"count\") as count FROM \"jmeter\" WHERE (\"transaction\" = 'all' AND \"currentRunName\"=~/^${runName}$/) fill(null)";
	
	public static String TOTAL_REQUESTS_OF_TXN_DURING_RUN_ORIG = "SELECT sum(\"count\") FROM \"$measurement_name\" WHERE (\"application\" =~ /^$application$/ AND \"transaction\" =~ /^$transaction$/ AND \"statut\" = 'all' AND \"currentRunName\"=~/^$RunTags$/) AND $timeFilter GROUP BY time($__interval) fill(null)";
	//Later add timefilter
	public static String TOTAL_REQUESTS_OF_TXN_DURING_RUN = "SELECT sum(\"count\") as count FROM \"$measurement_name\" WHERE (\"application\" =~ /^${applicationName}$/ AND \"transaction\" =~ /^${txnName}$/ AND \"statut\" = 'all' AND \"currentRunName\"=~/^${runName}$/) fill(null)";
	public static String TOTAL_REQUESTS_OF_TXN_DURING_RUN_WITHOUTAPP = "SELECT sum(\"count\") as count FROM \"$measurement_name\" WHERE (\"transaction\" =~ /^${txnName}$/ AND \"statut\" = 'all' AND \"currentRunName\"=~/^${runName}$/) fill(null)";
	
	public static String FAILED_REQUESTS_DURING_RUN_ORIG_1 = "SELECT sum(\"countError\") FROM \"$measurement_name\" WHERE (\"transaction\" = 'all' AND \"application\" =~ /^$application$/ AND \"currentRunName\"=~/^$RunTags$/) AND $timeFilter GROUP BY time($__interval) fill(null)";
	public static String FAILED_REQUESTS_DURING_RUN_ORIG = "SELECT sum(\"countError\") FROM \"jmeter\" WHERE (\"transaction\" = 'all' AND \"application\" =~ /^Orders$/ AND \"currentRunName\"=~/^Pref_Orders_Stage_Bravo_ONLY_ORDERS_31DEC_R1$/) AND time >= 1546236429684ms and time <= 1546239098305ms GROUP BY time(30s) fill(null)";
	public static String FAILED_REQUESTS_DURING_RUN = "SELECT sum(\"countError\") as count FROM \"jmeter\" WHERE (\"transaction\" = 'all' AND \"application\" =~ /^${applicationName}$/ AND \"currentRunName\"=~/^${runName}$/) fill(null)";
	public static String FAILED_REQUESTS_DURING_RUN_WITHOUTAPP = "SELECT sum(\"countError\") as count FROM \"jmeter\" WHERE (\"transaction\" = 'all' AND \"currentRunName\"=~/^${runName}$/) fill(null)";
	public static String FAILED_REQUESTS_OF_TXN_DURING_RUN = "SELECT sum(\"countError\") as count FROM \"jmeter\" WHERE (\"application\" =~ /^${applicationName}$/ AND \"currentRunName\"=~/^${runName}$/ AND \"transaction\" =~ /^${txnName}$/) fill(null)";
	public static String FAILED_REQUESTS_OF_TXN_DURING_RUN_WITHOUTAPP = "SELECT sum(\"countError\") as count FROM \"jmeter\" WHERE (\"currentRunName\"=~/^${runName}$/ AND \"transaction\" =~ /^${txnName}$/) fill(null)";
	
	public static String AVG_REQ_THROUGHPUT_OF_ALL_TXNS_PER_MIN_ORIG_1 = "SELECT mean(\"count\")*60/ $send_interval FROM \"$measurement_name\"  WHERE (\"application\" =~ /^$application$/ AND \"transaction\" !~ /[Ii]nternal/ AND \"transaction\" !~ /[Dd]ummy/ AND \"currentRunName\"=~/^$RunTags$/) AND $timeFilter GROUP BY transaction";
	public static String AVG_REQ_THROUGHPUT_OF_ALL_TXNS_PER_MIN = "SELECT mean(\"count\")*12 as tpm FROM \"jmeter\"  WHERE (\"application\" =~ /^${applicationName}$/ AND \"transaction\" !~ /[Ii]nternal/ AND \"transaction\" !~ /[Dd]ummy/ AND \"currentRunName\"=~/^${runName}$/) GROUP BY transaction";
	public static String AVG_REQ_THROUGHPUT_OF_ALL_TXNS_PER_MIN_WITHOUTAPP = "SELECT mean(\"count\")*12 as tpm FROM \"jmeter\"  WHERE (\"transaction\" !~ /[Ii]nternal/ AND \"transaction\" !~ /[Dd]ummy/ AND \"currentRunName\"=~/^${runName}$/) GROUP BY transaction";
	public static String AVG_REQ_THROUGHPUT_OF_TXN_PER_MIN = "SELECT mean(\"count\")*12 as tpm FROM \"jmeter\"  WHERE (\"application\" =~ /^${applicationName}$/ AND \"currentRunName\"=~/^${runName}$/) AND \"transaction\" =~ /^${txnName}$/";
	public static String AVG_REQ_THROUGHPUT_OF_TXN_PER_MIN_WITHOUTAPP = "SELECT mean(\"count\")*12 as tpm FROM \"jmeter\"  WHERE (\"currentRunName\"=~/^${runName}$/) AND \"transaction\" =~ /^${txnName}$/";
	
//	public static String REQUEST_THROUGHPUT_OF_TXN_PER_MIN_DURING_RUN_ORIG = "SELECT sum(\"count\") FROM \"jmeter\" WHERE (\"application\" =~ /^Orders$/ AND \"transaction\" =~ /^Create Order UI$/ AND \"statut\" = 'all' AND \"currentRunName\"=~/^Pref_Orders_Stage_Bravo_ONLY_ORDERS_31DEC_R1$/) AND time >= 1546236429684ms and time <= 1546239098305ms GROUP BY time(30s) fill(null)";
//	public static String REQUEST_THROUGHPUT_OF_TXN_PER_MIN_DURING_RUN_ORIG_1 = "SELECT sum(\"count\") FROM \"$measurement_name\" WHERE (\"application\" =~ /^$application$/ AND \"transaction\" =~ /^$transaction$/ AND \"statut\" = 'all' AND \"currentRunName\"=~/^$RunTags$/) AND $timeFilter GROUP BY time($__interval) fill(null)";
//	public static String REQUEST_THROUGHPUT_OF_TXN_PER_MIN_DURING_RUN = "SELECT sum(\"count\") FROM \"jmeter\" WHERE (\"application\" =~ /^${applicationName}$/ AND \"transaction\" =~ /^${txnName}$/ AND \"statut\" = 'all' AND \"currentRunName\"=~/^$RunTags$/) fill(null)";
	
	public static String REQUEST_THROUGHPUT_TIMESERIES_OF_TXN_DURING_RUN_ORIG = "SELECT last(\"count\") / 5 FROM \"jmeter\" WHERE (\"transaction\" =~ /^Create Order UI$/ AND \"statut\" = 'ok' AND \"currentRunName\"=~/^Pref_Orders_Stage_Bravo_ONLY_ORDERS_31DEC_R1$/) AND time >= 1546236429684ms and time <= 1546239098305ms GROUP BY time(5s)";
	public static String REQUEST_THROUGHPUT_TIMESERIES_OF_TXN_DURING_RUN_ORIG_1 = "SELECT last(\"count\") / $send_interval FROM \"$measurement_name\" WHERE (\"transaction\" =~ /^$transaction$/ AND \"statut\" = 'ok' AND \"currentRunName\"=~/^$RunTags$/) AND $timeFilter GROUP BY time($__interval)";
	public static String REQUEST_THROUGHPUT_TIMESERIES_OF_TXN_DURING_RUN = "SELECT last(\"count\") / 5 FROM \"jmeter\" WHERE (\"transaction\" =~ /^${txnName}$/ AND \"statut\" = 'ok' AND \"currentRunName\"=~/^${runName}$/) GROUP BY time(${scale}s)";
	
	public static String REQUEST_THROUGHPUT_TIMESERIES_DURING_RUN_ORIG_1 = "SELECT mean(\"count\") / $send_interval FROM \"$measurement_name\" WHERE (\"transaction\" = 'all' AND \"application\" =~ /^$application$/ AND \"currentRunName\"=~/^$RunTags$/) AND $timeFilter GROUP BY time($__interval) fill(null)";
	public static String REQUEST_THROUGHPUT_TIMESERIES_DURING_RUN = "SELECT mean(\"count\") / 5 FROM \"jmeter\" WHERE (\"transaction\" = 'all' AND \"application\" =~ /^${applicationName}$/ AND \"currentRunName\"=~/^${runName}$/) GROUP BY time(${scale}s) fill(null)";
	public static String REQUEST_THROUGHPUT_TIMESERIES_DURING_RUN_WITHOUT_APP = "SELECT mean(\"count\") / 5 FROM \"jmeter\" WHERE (\"transaction\" = 'all' AND \"currentRunName\"=~/^${runName}$/) GROUP BY time(${scale}s) fill(null)";
	
	public static String FAILED_REQUESTS_TIMESERIES_DURING_RUN_ORIG_1 = "SELECT sum(\"countError\") FROM \"$measurement_name\" WHERE (\"transaction\" = 'all' AND \"application\" =~ /^$application$/ AND \"currentRunName\"=~/^$RunTags$/) AND $timeFilter GROUP BY time($__interval) fill(null)";
	public static String FAILED_REQUESTS_TIMESERIES_DURING_RUN_ORIG = "SELECT sum(\"countError\") FROM \"jmeter\" WHERE (\"transaction\" = 'all' AND \"application\" =~ /^Orders$/ AND \"currentRunName\"=~/^Pref_Orders_Stage_Bravo_ONLY_ORDERS_31DEC_R1$/) AND time >= 1546236429684ms and time <= 1546239098305ms GROUP BY time(5s) fill(null)";
	public static String FAILED_REQUESTS_TIMESERIES_DURING_RUN = "SELECT sum(\"countError\") FROM \"jmeter\" WHERE (\"transaction\" = 'all' AND \"application\" =~ /^${applicationName}$/ AND \"currentRunName\"=~/^${runName}$/) GROUP BY time(${scale}s) fill(null)";
	public static String FAILED_REQUESTS_TIMESERIES_DURING_RUN_WITHOUTAPP = "SELECT sum(\"countError\") FROM \"jmeter\" WHERE (\"transaction\" = 'all' AND \"currentRunName\"=~/^${runName}$/) GROUP BY time(${scale}s) fill(null)";
	public static String FAILED_REQUESTS_TIMESERIES_OF_TXN_DURING_RUN = "SELECT sum(\"countError\") FROM \"jmeter\" WHERE (\"transaction\" =~ /^${txnName}$/ AND \"application\" =~ /^${applicationName}$/ AND \"currentRunName\"=~/^${runName}$/) GROUP BY time(${scale}s) fill(null)";
	public static String FAILED_REQUESTS_TIMESERIES_OF_TXN_DURING_RUN_WITHOUTAPP = "SELECT sum(\"countError\") FROM \"jmeter\" WHERE (\"transaction\" =~ /^${txnName}$/ AND \"currentRunName\"=~/^${runName}$/) GROUP BY time(${scale}s) fill(null)";
	
	public static String ERROR_INFO_ALL_TXNS_DURING_RUN_ORIG = "SELECT sum(\"count\") FROM \"jmeter\" WHERE (\"application\" =~ /^TurvoDemo$/ AND \"responseCode\" !~ /^$/ AND \"currentRunName\"=~/^Perf_Stage_Charlie_TurvoPerf_CR2280_AfterIndexes_NOV2_R2$/) AND time >= 1541158848755ms and time <= 1541163311611ms GROUP BY \"transaction\",\"responseCode\",\"responseMessage\"";
	public static String ERROR_INFO_ALL_TXNS_DURING_RUN_ORIG_1 = "SELECT sum(\"count\") FROM \"$measurement_name\" WHERE (\"application\" =~ /^$application$/ AND \"responseCode\" !~ /^$/ AND \"currentRunName\"=~/^$RunTags$/) AND $timeFilter GROUP BY \"transaction\",\"responseCode\",\"responseMessage\"";
	public static String ERROR_INFO_ALL_TXNS_DURING_RUN = "SELECT sum(\"count\") FROM \"jmeter\" WHERE (\"application\" =~ /^${applicationName}$/ AND \"responseCode\" !~ /^$/ AND \"currentRunName\"=~/^${runName}$/) GROUP BY \"transaction\",\"responseCode\",\"responseMessage\"";
	public static String ERROR_INFO_ALL_TXNS_DURING_RUN_WITHOUTAPP = "SELECT sum(\"count\") FROM \"jmeter\" WHERE (\"responseCode\" !~ /^$/ AND \"currentRunName\"=~/^${runName}$/) GROUP BY \"transaction\",\"responseCode\",\"responseMessage\"";
	public static String ERROR_INFO_OF_TXN_DURING_RUN = "SELECT sum(\"count\") FROM \"jmeter\" WHERE (\"application\" =~ /^${applicationName}$/ AND \"responseCode\" !~ /^$/ AND \"currentRunName\"=~/^${runName}$/ AND \"transaction\" =~ /^${txnName}$/) GROUP BY \"responseCode\",\"responseMessage\"";
	public static String ERROR_INFO_OF_TXN_DURING_RUN_WITHOUTAPP = "SELECT sum(\"count\") FROM \"jmeter\" WHERE (\"responseCode\" !~ /^$/ AND \"currentRunName\"=~/^${runName}$/ AND \"transaction\" =~ /^${txnName}$/) GROUP BY \"responseCode\",\"responseMessage\"";
	

}

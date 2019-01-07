package com.turvo.perf.grafana.client;

import java.util.Map;

import com.turvo.perf.grafana.domain.GrafanaDashboard;
import com.turvo.perf.grafana.domain.QueryResults;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GrafanaService {
	
	String GRAFANA_DASHBOARDS = "api/dashboards/uid/{uid}";
	String GRAFANA_NOTIFICATIONS = "api/alert-notifications/";
	String GRAFANA_ALERTS = "api/alerts/";
	String GRAFANA_SEARCH = "api/search/";
	String GRAFANA_LOGIN = "login";
	String GRAFANA_QUERY ="api/datasources/proxy/1/query";
	
	/**
	 * Later cache this result
	 */
	@POST(GRAFANA_LOGIN)
	Call<ResponseBody> login(@Body Map<String,String> loginParams, @Query("orgId") String orgId);
	
	@GET(GRAFANA_DASHBOARDS)
	Call<GrafanaDashboard> getDashboard(@Path("uid") String dashboardUid);
	
	@GET(GRAFANA_QUERY)
	Call<QueryResults> getResultForQuery(@Query("db") String db, @Query("q") String query);
}

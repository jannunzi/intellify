package com.hmhco.lrs.intellify.service;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Test {

	private String authorizationToken = "2085a4b4c51907c1590f5fa955f2654cdf9fccc22b043481a112d485a919ab066d4c064c28d814d019df6e35734e20f5d21c5360c0e666af0038a9edb8f85465";
	private String OBSERVER_URL     = "http://hmhprod2.intellifylearning.com/intellisearch/xxx-test-hmh-observinator-event-eventdata-5613436f0cf2bffa8000e246/_search";
	private String OBSERVER_PAYLOAD = "{\"query\":{\"filtered\":{\"query\":{\"bool\":{\"should\":[{\"query_string\":{\"query\":\"*\"}}]}},"
			+ "\"filter\":{\"bool\":{\"must\":[{\"range\":{\"timestamp\":{\"from\":\"1507780800000\",\"to\":\"1508472000000\"}}}]}}}},"
			+ "\"highlight\":{\"fields\":{},\"fragment_size\":2147483647,\"pre_tags\":[\"@start-highlight@\"],\"post_tags\":[\"@end-highlight@\"]},"
			+ "\"size\":100000,\"sort\":[{\"timestampISO\":{\"order\":\"asc\"}}]}";
	private String R180U_CURRENT_COMPUTE_STREAM_URL     = "https://hmhprod2.intellifylearning.com/intellisearch/xxx-test-r180u-synthetic-a5-b3-aggregate-with-student-info/_search";
	private String R180U_CURRENT_COMPUTE_STREAM_PAYLOAD = "{\"query\":{\"filtered\":{\"filter\":{\"bool\":{\"must\":[{\"range\":{\"timestamp\":{\"from\":\"1507780800000\",\"to\":\"1508472000000\"}}},{\"term\":{\"triggerType\":\"R180U_Performance\"}}]}}}},\"highlight\":{\"fields\":{},\"fragment_size\":2147483647,\"pre_tags\":[\"@start-highlight@\"],\"post_tags\":[\"@end-highlight@\"]},\"size\":0,\"aggs\":{\"R180U_COMPUTE_PERF_events_per_day\":{\"date_histogram\":{\"field\":\"timestamp\",\"interval\":\"day\"}}},\"sort\":[{\"timestampISO\":{\"order\":\"asc\"}}]}";
	private String rawUrl  = R180U_CURRENT_COMPUTE_STREAM_URL;
	private String payload = R180U_CURRENT_COMPUTE_STREAM_PAYLOAD;

	private static String INTELLIFY_AUTHORIZATION_TOKEN_URL = "https://hmhprod2.intellifylearning.com/user/apiToken";
	private static String INTELLIFY_AUTHORIZATION_PAYLOAD = "{\"username\": \"mryan\", \"password\": \"scholastic\"}";

	public String getAPIAuthorizationToken() throws Throwable {
		if(authorizationToken != null) {
			return authorizationToken;
		}
		String apiTokenResponse = sendPostRequest(INTELLIFY_AUTHORIZATION_TOKEN_URL, INTELLIFY_AUTHORIZATION_PAYLOAD, null);
		JSONObject apiTokenJson = new JSONObject(apiTokenResponse);
		return apiTokenJson.getString("apiToken");
	}

	public static String sendPostRequest(String requestUrl, String payload, String token) {
		StringBuffer jsonString = new StringBuffer();
		try {
	        URL url = new URL(requestUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	        connection.setDoInput(true);
	        connection.setDoOutput(true);
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Accept", "application/json");
	        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
	        if(token != null) {
		        connection.setRequestProperty("Authorization", "Bearer " + token);
	        }
	        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
	        writer.write(payload);
	        writer.close();
	        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String line;
	        while ((line = br.readLine()) != null) {
	                jsonString.append(line);
	        }
	        br.close();
	        connection.disconnect();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return jsonString.toString();
	}

	public String i_get_Raw_Stream_Data_for_OB(String token) throws Throwable {
		String rawStreamPayload = new String(payload);
		rawStreamPayload = rawStreamPayload.replaceAll("START_TIME", "1508990400000");
		rawStreamPayload = rawStreamPayload.replaceAll("END_TIME",   "1508904000000");
		return sendPostRequest(rawUrl, rawStreamPayload, token);
	}

	public static void main(String[] args) throws Throwable
	{
		// TODO Auto-generated method stub
		Test test = new Test();
		String token = test.getAPIAuthorizationToken();
		System.out.println(token);
		String raw = test.i_get_Raw_Stream_Data_for_OB(token);
		System.out.println(raw);
	}

}

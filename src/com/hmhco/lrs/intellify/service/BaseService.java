package com.hmhco.lrs.intellify.service;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class BaseService {

	private String USERNAME = "mryan";
	protected static String PASSWORD = "scholastic";
	private String INTELLIFY_API_TOKEN_PAYLOAD = "{\"username\": \"mryan\", \"password\": \"scholastic\"}";
	private String INTELLIFY_API_TOKEN_URL     = "https://hmh2.intellifylearning.com/user/apiToken";
	protected static String PAYLOAD = "{from: FROM, to: TO}";
	private String apiToken = "56fe8da06e10aad5bb88e85a608c1ead5003ae8c2a743185bdeaab9886abd735ed5832269322ad5cc0e6178f078f2ef8bccb8476dc0b89dc13f6eb7dd613f966";

	private long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

	protected String url = null;
	protected String payload = PAYLOAD;
	protected long from;
	protected long to;

	public BaseService() {
		long from = this.getLastNightAtMidnightTimeInMilliseconds();
		long to = this.minusOneDayInMilliseconds(from);
		this.setTimeIntervalInMillis(from, to);
	}

	public BaseService(long from, long to) {
		this.setTimeIntervalInMillis(from, to);
	}

	public BaseService(String url, String payload, long from, long to) {
		super();
		this.payload = payload;
		this.url = url;
		this.setTimeIntervalInMillis(from, to);
	}

	public BaseService(String url, String payload, Date from, Date to) {
		super();
		this.payload = payload;
		this.url = url;
		this.from = from.getTime();
		this.to = to.getTime();
		this.setTimeIntervalInMillis(this.from, this.to);
	}

	public BaseService(String url, String payload, String from, String to) {
		super();
		this.payload = payload;
		this.url = url;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date fromDate = new Date();
		Date toDate = new Date();
		try {
			fromDate = sdf.parse(from);
			toDate = sdf.parse(to);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		this.from = fromDate.getTime();
		this.to = toDate.getTime();
		this.setTimeIntervalInMillis(this.from, this.to);
	}

	public long dateToMillis(Date date) {
		return -1;
	}

	public void setTimeIntervalInMillis(long fromInMillis, long toInMillis) {
		this.from = fromInMillis;
		this.to = toInMillis;
		this.payload = this.payload
				.replaceAll("FROM", from + "")
				.replaceAll("TO", to + "");
	}

	public void setFrom(long from) {
		this.from = from;
	}

	public void setTo(long to) {
		this.to = to;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long minusOneDayInMilliseconds(long timeInMillis) {
		return timeInMillis - ONE_DAY_IN_MILLIS;
	}

	public long getLastNightAtMidnightTimeInMilliseconds() {
		GregorianCalendar now = new GregorianCalendar();
		System.out.println(now.getTime());
		now.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		// reset hour, minutes, seconds and millis
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		System.out.println(now.getTime());

		// lastMidnight
		Calendar lastMidnight = (Calendar) now.clone();
		lastMidnight.add(Calendar.DAY_OF_MONTH, -1);
		System.out.println(lastMidnight.getTime());

		return lastMidnight.getTimeInMillis();
	}

	public String getApiToken() {
		if(apiToken != null) {
			return apiToken;
		}
		String apiTokenResponse = sendPostRequest(INTELLIFY_API_TOKEN_URL, INTELLIFY_API_TOKEN_PAYLOAD, null);
		JSONObject apiTokenJson = new JSONObject(apiTokenResponse);
		apiToken = apiTokenJson.getString("apiToken");
		return apiToken;
	}

	public String sendPostRequest() {
		return sendPostRequest(this.url, this.payload, this.apiToken);
	}

	public String sendPostRequest(String requestUrl, String payload, String authenticationToken) {
		StringBuffer jsonString = new StringBuffer();
		try {
	        URL url = new URL(requestUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	        connection.setDoInput(true);
	        connection.setDoOutput(true);
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Accept", "*/*");
	        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
	        if(authenticationToken != null) {
	        		connection.setRequestProperty("Authorization", "Bearer " + authenticationToken);
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
	    		System.out.println(authenticationToken);
	        e.printStackTrace();
	    }
	    return jsonString.toString();
	}

	public static void main(String[] args)
	{
		BaseService bs = new BaseService();
		String token = bs.getApiToken();
		System.out.println(token);
		long yesterdayAtMidnight = bs.getLastNightAtMidnightTimeInMilliseconds();
		long twoMidnightsAgoInMillis = bs.minusOneDayInMilliseconds(yesterdayAtMidnight);
		long difference = yesterdayAtMidnight - twoMidnightsAgoInMillis;
		System.out.println(yesterdayAtMidnight);
		System.out.println(twoMidnightsAgoInMillis);
		System.out.println(difference);
	}

}

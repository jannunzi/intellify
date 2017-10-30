package com.hmhco.lrs.intellify.service;

import com.hmhco.lrs.intellify.model.R180uPerformancePerDayEvent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class R180uComputeStreamService extends BaseService {

	private static String URL = "https://hmh2.intellifylearning.com/intellisearch/xxx-test-r180u-synthetic-a5-b3-aggregate-with-student-info/_search";

	static {
//		PAYLOAD = "{\"query\":{ \"filtered\":{ \"filter\":{ \"bool\":{ \"must\":[ { \"range\":{ \"timestamp\":{ \"from\":\"1508990400000\", \"to\":\"1508904000000\" } } }, { \"term\": { \"triggerType\": \"R180U_Performance\" } } ] } } } }, \"highlight\":{ \"fields\":{ }, \"fragment_size\":2147483647, \"pre_tags\":["
//				+ " \"@start-highlight@\" ], \"post_tags\":[ \"@end-highlight@\" ] }, \"size\":0, \"aggs\": { \"R180U_COMPUTE_PERF_events_per_day\" : { \"date_histogram\" : { \"field\" : \"timestamp\", \"interval\" : \"day\" } } }, \"sort\":[ { \"timestampISO\":{ \"order\":\"asc\" } } ] }";
		PAYLOAD = "{\"query\":{ \"filtered\":{ \"filter\":{ \"bool\":{ \"must\":[ { \"range\":{ \"timestamp\":{ \"from\":\"1507780800000\", \"to\":\"1508472000000\" } } }, { \"term\": { \"triggerType\": \"R180U_Performance\" } } ] } } } }, \"highlight\":{ \"fields\":{ }, \"fragment_size\":2147483647, \"pre_tags\":["
				+ " \"@start-highlight@\" ], \"post_tags\":[ \"@end-highlight@\" ] }, \"size\":0, \"aggs\": { \"R180U_COMPUTE_PERF_events_per_day\" : { \"date_histogram\" : { \"field\" : \"timestamp\", \"interval\" : \"day\" } } }, \"sort\":[ { \"timestampISO\":{ \"order\":\"asc\" } } ] }";
	}
	public R180uComputeStreamService() {
		super();
	}

	public R180uComputeStreamService(long from, long to) {
		super(from, to);
	}

	public String getPayload() {
		return this.payload;
	}

	public String requestRawJsonData() {
		String authorizationToken = this.getApiToken();
		System.out.println(authorizationToken);
		return this.sendPostRequest(this.url, this.payload, authorizationToken);
	}

	public ArrayList<R180uPerformancePerDayEvent> parseR180uPerformancePerDayEventsJsonArray(String jsonEvents) {
		ArrayList<R180uPerformancePerDayEvent> events = new ArrayList<R180uPerformancePerDayEvent>();

		JSONObject jsonObj = new JSONObject(jsonEvents);
		JSONObject aggregations = jsonObj.getJSONObject("aggregations");
		JSONObject R180U_COMPUTE_PERF_events_per_day = aggregations.getJSONObject("R180U_COMPUTE_PERF_events_per_day");
		JSONArray buckets = R180U_COMPUTE_PERF_events_per_day.getJSONArray("buckets");
		int eventCount = buckets.length();
		for(int i = 0; i < eventCount; i++) {
			JSONObject eventJson = buckets.getJSONObject(i);
			R180uPerformancePerDayEvent event = new R180uPerformancePerDayEvent(eventJson);
			events.add(event);
		}

		return events;
	}

	public static ArrayList<R180uPerformancePerDayEvent> compareEvents(ArrayList<R180uPerformancePerDayEvent> list1, ArrayList<R180uPerformancePerDayEvent> list2) {
		ArrayList<R180uPerformancePerDayEvent> missingEvents = new ArrayList<R180uPerformancePerDayEvent>();
		for(R180uPerformancePerDayEvent event1 : list1) {
			boolean found = false;
			for(R180uPerformancePerDayEvent event2 : list2) {
				if(event1.equals(event2)) {
					found = true;
					break;
				}
			}
			if(!found) {
				missingEvents.add(event1);
			}
		}
		return missingEvents;
	}

	public static void main(String[] args)
	{
		String currentR180uComputeStream = "https://hmh2.intellifylearning.com/intellisearch/xxx-test-r180u-synthetic-a5-b3-aggregate-with-student-info/_search";
		String newR180uComputeStream     = "http://hmh2.intellifylearning.com/intellisearch/data-r180u-performance-v19/_search";

		R180uComputeStreamService r180uComputeStreamService = new R180uComputeStreamService();
		r180uComputeStreamService.setUrl(currentR180uComputeStream);
//		String payload = r180.getPayload();
		String currentEventsJson = r180uComputeStreamService.requestRawJsonData();
		System.out.println(currentEventsJson);
		ArrayList<R180uPerformancePerDayEvent> currentEvents = r180uComputeStreamService
				.parseR180uPerformancePerDayEventsJsonArray(currentEventsJson);
		System.out.println(currentEvents);

		r180uComputeStreamService.setUrl(newR180uComputeStream);
		String newEventsJson = r180uComputeStreamService.requestRawJsonData();
		System.out.println(newEventsJson);
		ArrayList<R180uPerformancePerDayEvent> newEvents = r180uComputeStreamService
				.parseR180uPerformancePerDayEventsJsonArray(newEventsJson);
		System.out.println(newEvents);

		System.out.println("Missing Events:");
		ArrayList<R180uPerformancePerDayEvent> missingEvents =
				R180uComputeStreamService.compareEvents(currentEvents, newEvents);
		System.out.println(missingEvents);
	}

}

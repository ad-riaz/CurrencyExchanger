package service;

import java.util.DoubleSummaryStatistics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonParser {
	private static final GsonBuilder gsonBuilder = new GsonBuilder();
	
	public static String toJson(Object o) {
		return gsonBuilder.create().toJson(o);
	}
}

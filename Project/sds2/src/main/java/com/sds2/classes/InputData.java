package com.sds2.classes;

import org.json.JSONObject;

public class InputData {
	private String city;
	private String initialDate;
	private String finalDate;

	public InputData(String json) {
		JSONObject obj = new JSONObject(json);
		this.city = obj.getString("city");
		this.initialDate = obj.getString("start_date");
		this.finalDate = obj.getString("end_date");
	}

	public String getCity() {
		return city;
	}

	public String getInitialDate() {
		return initialDate;
	}

	public String getFinalDate() {
		return finalDate;
	}

}

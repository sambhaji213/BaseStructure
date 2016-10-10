package com.basestructure.data;

import com.basestructure.provider.MethodEnum;

import org.json.JSONObject;

import java.util.List;

public class SyncableObject {

	public String url = "";
	public MethodEnum restCommand;
	List<JSONObject> jsonValue;
	public ParserTypeEnum parserType;

	public SyncableObject(MethodEnum restApi, List<JSONObject> json, String uri, ParserTypeEnum type) {
		this.restCommand = restApi;
		this.jsonValue = json;
		this.url = uri;
		this.parserType = type;
	}

	public SyncableObject(MethodEnum restApi, List<JSONObject> json, String uri) {
		this.restCommand = restApi;
		this.jsonValue = json;
		this.url = uri;
		this.parserType = ParserTypeEnum.None;
	}

	public void setParserType(ParserTypeEnum type) {
		this.parserType = type;
	}

	public ParserTypeEnum getParserType() {
		return parserType;
	}

	public String getUrl() {
		return url;
	}

	public List<JSONObject> getJsonObject() {
		return jsonValue;
	}

	public MethodEnum getRestCommand() {
		return restCommand;
	}
}

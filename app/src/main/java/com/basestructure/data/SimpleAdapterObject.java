package com.basestructure.data;

import org.apache.commons.lang.StringUtils;

public class SimpleAdapterObject  {
	private String id;
	private String code;
	private String name;
	private Boolean selected;

	public SimpleAdapterObject(String id, String name){
		this.id=id;
		this.name=name;
		this.selected=false;
	}

	public SimpleAdapterObject(String id, String name, Boolean selected){
		this.id=id;
		this.name=name;
		this.selected = selected;
	}

	public SimpleAdapterObject(String id, String code, String name){
		this.id = id;
		this.code = code;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getId() {
		return id;
	}

	public int getIntId() {
		if(StringUtils.isNotBlank(id))
			return Integer.valueOf(id);
		return -1;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}


}

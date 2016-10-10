package com.basestructure.data;

public enum ParserTypeEnum {
	userProfile(0),
    userLinks(1),
    userLanguage(2),
    None(3);

	private int parserType;

	ParserTypeEnum(int i) {
		parserType = i;
	}

	public int valueOf(int status){ return parserType; }
}


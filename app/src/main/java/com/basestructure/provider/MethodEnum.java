package com.basestructure.provider;

public enum MethodEnum {
    POST(0), DELETE(1),PUT(2),NEW(3),GET(4),PATCH(5), NONE(6);

    private int statusCode;

    MethodEnum(int i) {
        statusCode = i;
    }

    public int valueOf(int status){ return statusCode; }

    public static MethodEnum fromValue(String value, MethodEnum defaultValue) {
        try {
            return Enum.valueOf(MethodEnum.class, value);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public static MethodEnum fromValue(String value) {
        return fromValue( value, NONE);
    }
}


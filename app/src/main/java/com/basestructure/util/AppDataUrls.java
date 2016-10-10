package com.basestructure.util;

public class AppDataUrls {

    public static final String BASE_URL = "http://52.28.23.152/app/";

    public static final String API_REQUEST = BASE_URL + "api/rest/";

    public static String getLoginUrl(){
        //http://52.28.83.229:8585/userAddresses
        return BASE_URL+"userAddresses/";
    }

    public static String getLogOutUrl(){
        //http://52.28.23.152/app/api/rest/customers
        return BASE_URL+"userAddresses/";
    }

    public static String getProducts(int page, int limit) {
        //http://52.28.23.152/app/api/rest/products?page=1&size=10?oauth_token=c1ecd37c1d180787537b26e5f7782aa4
        return String.format("%sproducts?page=%d&limit=%d", API_REQUEST, page, limit);
    }

    public static String getCustomers(int page, int limit) {
        //http://52.28.23.152/app/api/rest/customers?page=1&size=10?oauth_token=c1ecd37c1d180787537b26e5f7782aa4
        return String.format("%scustomers?page=%d&limit=%d", API_REQUEST, page, limit);
    }
}




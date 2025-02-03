package com.concord.concordapi.shared.util;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;


@Component
public class UtilsMethods {

    public static HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
    public static String generateUniqueCode(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}

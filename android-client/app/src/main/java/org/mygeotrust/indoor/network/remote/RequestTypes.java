package org.mygeotrust.indoor.network.remote;

/**
 * Created by mis on 2/7/2016.
 *
 * =====
 * Usage
 * =====
 * RequestTypes type = RequestTypes.POST;
 * System.out.println(type.getRequestTypeValue());
 */
public enum RequestTypes {
    POST ("POST"),
    GET ("GET"),
    PUT ("PUT"),
    DELETE ("DELETE");

    private final String requestType;

    RequestTypes(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestTypeValue(){
        return this.requestType;
    }
}

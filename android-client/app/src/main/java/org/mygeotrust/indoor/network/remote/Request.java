package org.mygeotrust.indoor.network.remote;

import java.util.HashMap;

/**
 * Created by mis on 2/11/2016.
 */
public class Request {
    private static final String TAG = Request.class.getSimpleName();

    private RequestTypes _requestType;
    private String _remoteHost;
    private int _portNumber;
    private String _apiName;
    private HashMap<String, String> _params;

    /*
    Constructor
     */
    public Request(String remoteHost, int portNumber, RequestTypes requestType) {
        this._remoteHost = remoteHost;
        this._portNumber = portNumber;
        this._requestType = requestType;
        _apiName = "";
        _params = new HashMap<>();
    }

    /*
    Getters and Setters
     */
    public RequestTypes getRequestType() {
        return _requestType;
    }

    public void setRequestType(RequestTypes requestType) {
        this._requestType = requestType;
    }

    public String getRemoteHost() {
        return _remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this._remoteHost = remoteHost;
    }

    public int getPortNumber() {
        return _portNumber;
    }

    public void setPortNumber(int portNumber) {
        this._portNumber = portNumber;
    }

    public String getApiName() {
        return _apiName;
    }

    public void setApiName(String apiName) {
        this._apiName = apiName;
    }

    public HashMap<String, String> getParams() {
        return _params;
    }

    public void setParams(HashMap<String, String> params) {
        _params = params;
    }

    /*
    Helper Methods
     */
    public String getRequestURL(){
        StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append("http://" + getRemoteHost() + ":" + getPortNumber());

        if(!_apiName.isEmpty())
            urlBuilder.append(_apiName);

        return urlBuilder.toString();
    }
}

package org.mygeotrust.indoor.network.remote;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mesba on 2/9/2016.
 */


public class RemoteConnection {
    // constant variables
    private static final String TAG = RemoteConnection.class.getSimpleName();
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;

    /*
    Class Methods
    */
    public static Response getURLResponse(Request request) {
        String _responseRes = "";
        int _responseCode = 0;
        Map<String, List<String>> _headerMap = new HashMap<>();
        Response _response = new Response();

        try {
            URL obj = null;
            if(request.getParams().size() > 0){
                String urlString = request.getRequestURL() + "?" + getQuery(request.getParams());
                System.out.println("Url String: " + urlString);
                obj = new URL(urlString);
            }
            else {
                System.out.println("Url String: " + request.getRequestURL());
                obj = new URL(request.getRequestURL());
            }

            HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
            httpURLConnection.setRequestMethod(request.getRequestType().getRequestTypeValue());



            //for POST method we need to write the parameters to the connection after we have opened the connection
            if (request.getRequestType() == RequestTypes.POST) {
                configureURLConnection(httpURLConnection);
                writeParameters(httpURLConnection, request.getParams());
                httpURLConnection.connect();
            }

            _responseCode = httpURLConnection.getResponseCode();
            System.out.println("Response Code: " + _responseCode);

            _headerMap = httpURLConnection.getHeaderFields();
            System.out.println("Header Map Size: " + _headerMap.size());

            _responseRes = readHttpInputStreamToString(httpURLConnection);
            System.out.println("Response Result: " + _responseRes);

        } catch (IOException e) {
            System.out.println("Error Message :" + e.getMessage());
            _responseRes = e.getMessage();
            e.printStackTrace();
        }

        _response.setResponseCode(_responseCode);
        _response.setHeaderMap(_headerMap);
        _response.setResponseResult(_responseRes);

        return _response;
    }

    /*
    private helper methods
     */

    /**
     * In POST method we need to write the parameters to the connection after we have opened the connection
     *
     * @param connection
     * @param params
     */
    private static void writeParameters(URLConnection connection, HashMap<String, String> params){
        try {
            OutputStream os = connection.getOutputStream();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param connection object; note: before calling this function,
     *  ensure that the connection is already be open,
     *  and any writes to the connection's output stream should have already been completed.
     * @return String containing the body of the connection response or
     *  null if the input stream could not be read correctly
     */
    private static String readHttpInputStreamToString(URLConnection connection) throws IOException {
        String result = "";
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        InputStream is = null;

        try {
            is = new BufferedInputStream(connection.getInputStream());
        }
        catch (Exception e) {
            System.out.println(TAG + ": Error reading InputStream");

            if(connection instanceof HttpURLConnection){
                is = new BufferedInputStream(((HttpURLConnection)connection).getErrorStream());
            }
        }
        finally {
            br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();

            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    System.out.println(TAG + ": Error closing InputStream");
                }
            }
        }

        return result;
    }

    /**
     *
     *
     * @param connection
     */
    private static void configureURLConnection(URLConnection connection){
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        //todo need to remove later
        connection.setRequestProperty("api_key", "fgi_all");
        connection.setRequestProperty("Content-Type", "application/json");

        try {
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write("{}");
            writer.close();
        }
        catch (IOException ioEx){
            System.err.println(TAG + ": " + ioEx.getMessage());
        }
    }

    /**
     *
     *
     * @param map
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String getQuery(HashMap<String, String> map) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry: map.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        System.out.println(TAG + " Final URL: " + result.toString());

        return result.toString();
    }
}

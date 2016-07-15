package org.mygeotrust.indoor.network.async;

import java.util.List;
import java.util.Map;

/**
 * Created by mis on 2/11/2016.
 */
public interface IAsyncResponse {
    void onResultsFailed(String response, int statusCode, Map<String, List<String>> header);
    void onResultsSucceeded(String response, int statusCode, Map<String, List<String>> header);
}

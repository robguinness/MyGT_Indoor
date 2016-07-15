package org.mygeotrust.indoor.network.async;

import android.os.AsyncTask;

import org.mygeotrust.indoor.network.remote.RemoteConnection;
import org.mygeotrust.indoor.network.remote.Request;
import org.mygeotrust.indoor.network.remote.Response;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mis on 2/11/2016.
 */
public class AsyncRequestTask extends AsyncTask<Request, Void, Response> {
    private static final String TAG = AsyncRequestTask.class.getSimpleName();

    private IAsyncResponse _listener;
    public AsyncRequestTask() {
    }

    public void executeTask(Request request, IAsyncResponse listener){
        _listener = listener;
        this.execute(request);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Response doInBackground(Request... params) {
        Response response = RemoteConnection.getURLResponse(params[0]);
        return response;
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        if(response != null){
            switch (response.getResponseCode()){
                case 200:
                    _listener.onResultsSucceeded(response.getResponseResult(), response.getResponseCode(), response.getHeaderMap());
                    break;
                case 204:
                    _listener.onResultsSucceeded(response.getResponseResult(), response.getResponseCode(), response.getHeaderMap());
                    break;
                case 401:
                    _listener.onResultsFailed("HTTP_UNAUTHORIZED", response.getResponseCode(), response.getHeaderMap());
                    break;
                case 500:
                    _listener.onResultsFailed("HTTP_SERVER_ERROR", response.getResponseCode(), response.getHeaderMap());
                    break;
                default:
                    _listener.onResultsFailed(response.getResponseResult(), response.getResponseCode(), response.getHeaderMap());
                    break;
            }

        }
        else
            _listener.onResultsFailed("Response Not Found!!", 404, new HashMap<String, List<String>>());
    }
}

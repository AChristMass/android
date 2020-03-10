package fr.igm.robotmissions.objects.utils;

import android.util.Log;

import java.util.List;
import java.util.Map;

import io.swagger.client.ApiCallback;
import io.swagger.client.ApiException;

public abstract class SimpleApiCallback<T> implements ApiCallback<T> {

    @Override
    public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
        Log.e("api call error", statusCode+":"+e.getMessage()+e.getResponseBody());
    }

    @Override
    public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

    }

    @Override
    public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

    }
}

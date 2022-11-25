package com.geostat.census_2024.architecture.future;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CallbackFuture extends CompletableFuture<Response> implements Callback {
    public void onResponse(@NonNull Call call, @NonNull Response response) {
        super.complete(response);
    }
    public void onFailure(@NonNull Call call, @NonNull IOException e){
        super.completeExceptionally(e);
    }
}

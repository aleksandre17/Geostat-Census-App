package com.geostat.census_2024.ui.fileManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.geostat.census_2024.R;
import com.geostat.census_2024.ui.map.MapActivity;
import com.geostat.census_2024.utility.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2.Status;
import com.tonyodev.fetch2core.DownloadBlock;
import com.tonyodev.fetch2core.Extras;
import com.tonyodev.fetch2core.FetchObserver;
import com.tonyodev.fetch2core.Func;
import com.tonyodev.fetch2core.MutableExtras;
import com.tonyodev.fetch2core.Reason;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class FileActivity extends AppCompatActivity implements FetchObserver<Download> {

    private static final int STORAGE_PERMISSION_CODE = 100;
    //http://fdi.geostat.ge/nadzaladevi.gpkg
    public static final String[] sampleUrls = new String[]{"http://fdi.geostat.ge/nadzaladevi.gpkg"};

    private View mainView;
    private TextView progressTextView;
    private TextView titleTextView;
    private TextView etaTextView;
    private TextView downloadSpeedTextView;
    private Request request;
    private Fetch fetch;

    private String starter = "FALSE";

    private final FetchListener fetchListener = new FetchListener() {
        @Override
        public void onAdded(@NonNull Download download) {

        }

        @Override
        public void onQueued(@NonNull Download download, boolean b) {

        }

        @Override
        public void onWaitingNetwork(@NonNull Download download) {

        }

        @Override
        public void onCompleted(@NonNull Download download) {
            starter = "TRUE";
//            Intent up = new Intent(); setResult(Activity.RESULT_OK, up); finish();

            Intent intent = new Intent(FileActivity.this, MapActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onError(@NonNull Download download, @NonNull Error error, @Nullable Throwable throwable) {
            Log.d("TAG", "onError: ");
        }

        @Override
        public void onDownloadBlockUpdated(@NonNull Download download, @NonNull DownloadBlock downloadBlock, int i) {

        }

        @Override
        public void onStarted(@NonNull Download download, @NonNull List<? extends DownloadBlock> list, int i) {
//            Log.d("TAG", "onStarted: ");
        }

        @Override
        public void onProgress(@NonNull Download download, long l, long l1) {

        }

        @Override
        public void onPaused(@NonNull Download download) {
//            Log.d("TAG", "onPaused: ");
        }

        @Override
        public void onResumed(@NonNull Download download) {
//            Log.d("TAG", "onResumed: ");
        }

        @Override
        public void onCancelled(@NonNull Download download) {
//            Log.d("TAG", "onCancelled: ");
        }

        @Override
        public void onRemoved(@NonNull Download download) {
//            Log.d("TAG", "onRemoved: ");
        }

        @Override
        public void onDeleted(@NonNull Download download) {
//            Log.d("TAG", "onDeleted: ");
        }
    };

    @NonNull
    public static String getSaveDir(Context context) {
        return context.getExternalFilesDir(null).getPath();
    }

    @NonNull
    static String getNameFromUrl(final String url) {
        return Uri.parse(url).getLastPathSegment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this).setDownloadConcurrentLimit(3).build();

        mainView = findViewById(R.id.activity_single_download);
        progressTextView = findViewById(R.id.progressTextView);
        titleTextView = findViewById(R.id.titleTextView);
        etaTextView = findViewById(R.id.etaTextView);
        downloadSpeedTextView = findViewById(R.id.downloadSpeedTextView);
        fetch = Fetch.Impl.getInstance(fetchConfiguration);
        checkStoragePermission();

        fetch.addListener(fetchListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (request != null) {
            // fetch.removeListener(fetchListener);
            fetch.attachFetchObserversForDownload(request.getId(), this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (request != null) {
            fetch.removeListener(fetchListener);
            fetch.removeFetchObserversForDownload(request.getId(), this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (starter.equals("FALSE")) {
            File file = new File(getExternalFilesDir(null).getPath() + "/nadzaladevi.gpkg");
            if (file.exists()) {
                file.delete();
            }
        }
        fetch.close();
    }

    @Override
    public void onChanged(Download data, @NotNull Reason reason) {
        updateViews(data, reason);
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            enqueueDownload();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enqueueDownload();
        } else {
            Snackbar.make(mainView, R.string.permission_not_enabled, Snackbar.LENGTH_LONG).show();
        }
    }

    private void enqueueDownload() {
        final String url = sampleUrls[0];
        final String filePath = getSaveDir(this) + "/" + getNameFromUrl(url);
        request = new Request(url, filePath);
        request.setExtras(getExtrasForRequest(request));

        fetch.attachFetchObserversForDownload(request.getId(), this)
                .enqueue(request, new Func<Request>() {
                    @Override
                    public void call(@NotNull Request result) {
                        request = result;
                    }
                }, new Func<Error>() {
                    @Override
                    public void call(@NotNull Error result) {
                        Toast.makeText(FileActivity.this, "SingleDownloadActivity Error: %1$s" + result.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Extras getExtrasForRequest(Request request) {
        final MutableExtras extras = new MutableExtras();
        return extras;
    }

    private void updateViews(@NotNull Download download, Reason reason) {
        if (request.getId() == download.getId()) {
            if (reason == Reason.DOWNLOAD_QUEUED || reason == Reason.DOWNLOAD_COMPLETED) {
                setTitleView(download.getFile());
            }
            setProgressView(download.getStatus(), download.getProgress());
            etaTextView.setText(Utils.getETAString(this, download.getEtaInMilliSeconds()));
            downloadSpeedTextView.setText(Utils.getDownloadSpeedString(this, download.getDownloadedBytesPerSecond()));
            if (download.getError() != Error.NONE) {
                showDownloadErrorSnackBar(download.getError());
            }
        }
    }

    private void setTitleView(@NonNull final String fileName) {
        final Uri uri = Uri.parse(fileName);
        titleTextView.setText(uri.getLastPathSegment());
    }

    private void setProgressView(@NonNull final Status status, final int progress) {
        switch (status) {
            case QUEUED: {
                progressTextView.setText(R.string.queued);
                break;
            }
            case ADDED: {
                progressTextView.setText(R.string.added);
                break;
            }
            case DOWNLOADING:
            case COMPLETED: {
                if (progress == -1) {
                    progressTextView.setText(R.string.downloading);
                } else {
                    final String progressString = getResources().getString(R.string.percent_progress, progress);
                    progressTextView.setText(progressString);
                }
                break;
            }
            default: {
                progressTextView.setText(R.string.status_unknown);
                break;
            }
        }
    }

    private void showDownloadErrorSnackBar(@NotNull Error error) {
        final Snackbar snackbar = Snackbar.make(mainView, "Download Failed: ErrorCode: " + error, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.retry, v -> {
            fetch.retry(request.getId());
            snackbar.dismiss();
        });
        snackbar.show();
    }
}
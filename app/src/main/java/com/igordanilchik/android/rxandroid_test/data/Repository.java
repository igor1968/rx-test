package com.igordanilchik.android.rxandroid_test.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.igordanilchik.android.rxandroid_test.BuildConfig;
import com.igordanilchik.android.rxandroid_test.api.CacheProviders;
import com.igordanilchik.android.rxandroid_test.api.ClientApi;
import com.igordanilchik.android.rxandroid_test.model.Catalogue;

import java.io.File;
import java.io.IOException;

import io.rx_cache.EvictProvider;
import io.rx_cache.internal.RxCache;
import io.victoralbertos.jolyglot.JacksonSpeaker;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import rx.Observable;


public class Repository {
    private static final String LOG_TAG = Repository.class.getSimpleName();

    @NonNull
    private final CacheProviders providers;
    @NonNull
    private final ClientApi restApi;

    private static Repository INSTANCE;

    public static Repository getRepository(@NonNull File cacheDir) {
        if (INSTANCE == null) {
            INSTANCE = new Repository(cacheDir);
        }
        return INSTANCE;
    }

    private Repository(@NonNull File cacheDir) {
        providers = new RxCache.Builder()
                .persistence(cacheDir, new JacksonSpeaker())
                .using(CacheProviders.class);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            httpClient.addInterceptor(logging);
        }

        httpClient.addNetworkInterceptor(chain -> {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
        });

        restApi = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .baseUrl(ClientApi.API_BASE_URL)
                .client(httpClient.build())
                .build()
                .create(ClientApi.class);
    }

    @RxLogObservable
    public Observable<Catalogue> getCatalogue(final boolean update) {
        return providers.getCatalogueEvictProvider(getRemoteCatalogue(), new EvictProvider(update));
    }

    private Observable<Catalogue> getRemoteCatalogue() {
        return restApi.loadCatalogue(ClientApi.API_KEY);
    }

    private static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;

        ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override public long contentLength() {
            return responseBody.contentLength();
        }

        @Override public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    if (responseBody.contentLength() > 0) {
                        progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    } else {
                        progressListener.update(totalBytesRead, bytesRead == -1);
                    }
                    return bytesRead;
                }
            };
        }
    }

    interface ProgressListener {
        //Known content length
        void update(long bytesRead, long contentLength, boolean done);
        //Unknown content length
        void update(long bytesRead, boolean done);
    }

    private final ProgressListener progressListener = new ProgressListener() {
        @Override
        public void update(long bytesRead, long contentLength, boolean done) {
            update(bytesRead, done);
            Log.d(LOG_TAG, "Content length: " + contentLength);
            Log.d(LOG_TAG, String.format("%d%% done\n", (100 * bytesRead) / contentLength));
        }

        @Override
        public void update(long bytesRead, boolean done) {
            Log.d(LOG_TAG, "Bytes read: " + bytesRead);
            Log.d(LOG_TAG, "Done: " + done);
        }
    };
}
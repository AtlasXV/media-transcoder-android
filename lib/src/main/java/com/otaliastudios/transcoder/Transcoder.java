/*
 * Copyright (C) 2014 Yuya Tanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.otaliastudios.transcoder;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.otaliastudios.transcoder.ext.DefaultTranscodeOptionFactory;
import com.otaliastudios.transcoder.ext.TranscodeCallable;
import com.otaliastudios.transcoder.ext.TranscodeOptionFactory;
import com.otaliastudios.transcoder.internal.transcode.ThreadCountStrategy;
import com.otaliastudios.transcoder.internal.utils.ThreadPool;
import com.otaliastudios.transcoder.sink.DataSink;
import com.otaliastudios.transcoder.validator.Validator;

import java.io.FileDescriptor;
import java.util.concurrent.Future;

public class Transcoder {
    public static ThreadCountStrategy threadCountStrategy;

    /**
     * Constant for {@link TranscoderListener#onTranscodeCompleted(int)}.
     * Transcoding was executed successfully.
     */
    public static final int SUCCESS_TRANSCODED = 0;

    /**
     * Constant for {@link TranscoderListener#onTranscodeCompleted(int)}:
     * transcoding was not executed because it was considered
     * not necessary by the {@link Validator}.
     */
    public static final int SUCCESS_NOT_NEEDED = 1;

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public static Transcoder getInstance() {
        return new Transcoder();
    }

    private Transcoder() { /* private */ }

    /**
     * Starts building transcoder options.
     * Requires a non null absolute path to the output file.
     *
     * @param outPath path to output file
     * @return an options builder
     */
    @NonNull
    public static TranscoderOptions.Builder into(@NonNull String outPath) {
        return new TranscoderOptions.Builder(outPath);
    }

    /**
     * Starts building transcoder options.
     * Requires a non null fileDescriptor to the output file or stream
     *
     * @param fileDescriptor descriptor of the output file or stream
     * @return an options builder
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    public static TranscoderOptions.Builder into(@NonNull FileDescriptor fileDescriptor) {
        return new TranscoderOptions.Builder(fileDescriptor);
    }

    /**
     * Starts building transcoder options.
     * Requires a non null sink.
     *
     * @param dataSink the output sink
     * @return an options builder
     */
    @NonNull
    public static TranscoderOptions.Builder into(@NonNull DataSink dataSink) {
        return new TranscoderOptions.Builder(dataSink);
    }

    public Future<Void> transcode(@NonNull final TranscoderOptions options) {
        return transcode(new DefaultTranscodeOptionFactory(options), options.getListener());
    }

    /**
     * Transcodes video file asynchronously.
     *
     * @param factory The transcoder options factory.
     * @return a Future that completes when transcoding is completed
     */
    @NonNull
    public Future<Void> transcode(@NonNull final TranscodeOptionFactory factory, final TranscoderListener listener) {
        return ThreadPool.submit(new TranscodeCallable(factory, listener), true);
    }

    public void cancel(boolean mayInterruptIfRunning) {
        ThreadPool.cancel(mayInterruptIfRunning);
    }
}

package com.danstoakes.fileexplorer.utility;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FileRunner
{
    private final Executor executor;

    private Callback callback;

    public FileRunner()
    {
        executor = Executors.newSingleThreadExecutor();
    }

    public void initialise ()
    {
        executor.execute(() -> {
            callback.onBegin();
            callback.run();
            callback.onComplete();
        });
    }

    public void setCallback (Callback callback)
    {
        this.callback = callback;
    }

    public interface Callback
    {
        void onBegin();
        void run();
        void onComplete();
    }
}
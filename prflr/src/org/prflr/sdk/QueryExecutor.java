package org.prflr.sdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Simple query executor.
 * @author cab404
 */
class QueryExecutor implements Executor {
    private QueryThread query;
    private boolean cancelled = false;

    public QueryExecutor() {
        query = new QueryThread();
    }

    @Override public void execute(Runnable runnable) {
        if (query == null || !query.isAlive()) {
            query = new QueryThread(runnable);
            query.start();
        } else {
            query.add(runnable);
        }
    }

    public void cancel() {
        cancelled = true;
    }

    private class QueryThread extends Thread {
        private List<Runnable> query;
        final Object query_lock = new Object();

        void add(Runnable run) {
            synchronized (query_lock) {
                query.add(run);
            }
        }

        private QueryThread(Runnable... initial) {
            query = new ArrayList<>(Arrays.asList(initial));
        }

        @Override public void run() {
            super.run();
            while (!query.isEmpty() && !cancelled) {
                Runnable toRun;
                synchronized (query_lock) {
                    toRun = query.remove(0);
                }
                toRun.run();
            }
        }


    }

}

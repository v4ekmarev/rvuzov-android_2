package org.prflr.sdk;

import android.util.Log;

import java.io.IOException;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class PRFLRSender {

    private static final String TAG = "PRFLRSender";
    private static PRFLRSender instance;
    private static Integer overflowCount = 100;

    private QueryExecutor executor;

    private String source = null;
    private String key = null;
    private String port = null;

    /**
     * How many timers can be running at once.
     */
    /**
     * PRFLR.org's IP
     */
    private InetAddress ip;
    /**
     * Our pocket GMS.
     */
    private DatagramSocket socket;
    /**
     * Timer storage. Should add Timer class in some point of future.
     */
    private Map<String, Long> timers;

    public PRFLRSender() {
        timers = new ConcurrentHashMap<>();
        executor = new QueryExecutor();
    }

    /**
     * Connects to prflr.org and starts communication.
     */
    synchronized static void init(final String source, final String apiKey) {
        if (initialized()) Log.w(TAG, "Already initialized!");
        instance = new PRFLRSender();

        instance.executor.execute(new Runnable() {
            @Override public void run() {
                String host;
                if (apiKey == null)
                    Log.e(TAG, "ApiKey is null");
                if (source == null)
                    Log.e(TAG, "Source is null");
                try {
                    instance.source = cut(source, 32);
                    String[] parts = apiKey.split("@");
                    instance.key  = parts[0];
                    parts = parts[1].split(":");
                    host = parts[0];
                    instance.port = parts[1];
                    instance.ip = InetAddress.getByName(host);
                    instance.socket = new DatagramSocket();
                } catch (Exception e) {
                    Log.e(TAG, "Initialization error", e);
                    instance.executor.cancel();
                    instance = null;
                    return;
                }
            }
        });
    }

    static boolean initialized() {
        return instance != null;
    }

    static PRFLRSender getInstance() {
        if (instance != null)
            return instance;
        else {
            Log.e(TAG, "PRFLR is not initialized yet! Call init(), please.");
            return null;
        }
    }

    static void setOverflowCount(int overflowCount) {
        PRFLRSender.overflowCount = overflowCount;
    }

    /**
     * Starts timer with given name.
     * (delta-time, info you supplied to end(), thread name, etc.) to prflr.org
     */
    public void begin(String timerName) {

        //overflow control
        if (timers.size() > overflowCount) {
            Log.w(TAG, "Too many timers, deleted all of them. Please, check for missing end() calls." +
                    "If that's not a problem, try increasing overflowCount via PRFLR.setOverflowCount().");
            timers.clear();
        }

        timers.put(Long.toString(Thread.currentThread().getId()) + timerName, System.nanoTime());
    }

    public void end(String timerName) {
        end(timerName, null);
    }

    /**
     * Sends all useful data
     * (delta-time, info you supplied to end(), thread name, etc.) to prflr.org
     * Should be invoked in thread with the same with thread where begin() was invoked name.
     * That's one of downsides of this realisations. You can't remove timer from another thread.
     */
    public void end(final String timerName, final String info) {
        final String thread = Long.toString(Thread.currentThread().getId());

        Long startTime = timers.remove(thread + timerName);

        if (startTime == null) {
            Log.w(TAG, "Can't find timer with name '" + timerName + "'");
            return;
        }

        Long now = System.nanoTime();
        Long precision = (long) Math.pow(10, 3);

        final Double diffTime = (double) Math.round((double) (now - startTime) / 1000000 * precision) / precision;

        // send to exclusive thread
        executor.execute(new Runnable() {
            @Override public void run() {
                send(timerName, diffTime, thread, info);
            }
        });
    }

    private void send(String timerName, Double time, String thread, String info) {
        try {
            byte[] raw_data = (
                    cut(thread + "." + PRFLR.UID, 32) + "|"
                            + source + "|"
                            + cut(timerName, 48) + "|"
                            + Double.toString(time) + "|"
                            + cut(info, 32) + "|"
                            + key

            ).getBytes("UTF-8");

            int intPort = Integer.valueOf(port);

            socket.send(new DatagramPacket(raw_data, raw_data.length, ip, intPort));

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private static String cut(String s, Integer maxLength) {
        if (s == null)
            return "";
        if (s.length() < maxLength)
            return s;
        else
            return s.substring(0, maxLength);
    }
}

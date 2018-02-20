package com.raspisaniyevuzov.app.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.raspisaniyevuzov.app.Flags;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogUtil {
    private static final String LOG_PATH = "/RVuzov/logs/log.txt";
    private static final int MAX_FILE_SIZE = 20 * 1024 * 1024;

    private static FileHandler sFileHandler;
    private static String tagPrefix = "APP.RVuzov.";

    public static void init(Context context) {
        if (Flags.FILE_LOGGING) {
            // Проверяем что sd карта mounted
            if (Environment.getExternalStorageState().compareTo(Environment.MEDIA_MOUNTED) == 0) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + LOG_PATH;
                File dir = new File(path).getParentFile();
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                try {
                    sFileHandler = new FileHandler(path, MAX_FILE_SIZE, 1, true);
                    sFileHandler.setFormatter(new SimpleFormatter());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Пишем сообщение в файл
     *
     * @param level   Уровень логирования
     * @param tag     Тэг
     * @param message Сообщение
     */

    private static void logMessage(Level level, String tag, String message) {
        if (sFileHandler != null) {
            LogRecord record = new LogRecord(level, tag + ": " + message);
            sFileHandler.publish(record);
            sFileHandler.flush();
        }
    }

    /**
     * В LogCat пишем INFO
     *
     * @param tag     Тэг
     * @param message Сообщение
     */

    public static void i(String tag, String message) {
        logMessage(Level.INFO, tag, message);
        if (Flags.DEBUG)
            Log.i(tagPrefix + tag, tag + "." + message);
    }

    /**
     * В LogCat пишем DEBUG
     *
     * @param tag     Тэг
     * @param message Сообщение
     */
    public static void d(String tag, String message) {
        logMessage(Level.INFO, tag, message);
        if (Flags.DEBUG)
            Log.d(tagPrefix + tag, tag + "." + message);
    }

    /**
     * В LogCat пишем WARNING
     *
     * @param tag     Тэг
     * @param message Сообщение
     */
    public static void w(String tag, String message) {
        logMessage(Level.WARNING, tag, message);
        Log.w(tagPrefix + tag, tag + "." + message);
    }

    /**
     * В LogCat пишем ERROR
     *
     * @param tag     Тэг
     * @param message Сообщение
     */
    public static void e(String tag, String message) {
        logMessage(Level.FINEST, tag, message);
        Log.e(tagPrefix + tag, tag + "." + message);
    }

    public static void v(String tag, String message) {
        logMessage(Level.FINEST, tag, message);
        if (Flags.DEBUG)
            Log.v(tagPrefix + tag, tag + "." + message);
    }

    public static void wtf(String tag, String message) {
        logMessage(Level.FINEST, tag, message);
        Log.wtf(tagPrefix + tag, tag + "." + message);
    }

}

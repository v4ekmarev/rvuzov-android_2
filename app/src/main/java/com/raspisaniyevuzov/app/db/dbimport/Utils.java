package com.raspisaniyevuzov.app.db.dbimport;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.util.Pair;

import com.raspisaniyevuzov.app.misc.scheduleclasses.ScheduleTask;
import com.raspisaniyevuzov.app.misc.scheduleclasses.SortableScheduleClass;
import com.raspisaniyevuzov.app.util.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class Utils {

    public static File createDirectoryAndGenFilename() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return null;
        }
        final File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/schedule-application");
        if (!directory.exists()) {
            directory.mkdir();
        }
        final File file = new File(directory.getAbsolutePath() + "/" + System.currentTimeMillis() + ".bin");
        return file;
    }

    /*
     * public static String getFileContents(Context context, String filename)
     * throws FileNotFoundException { final InputStream iStream =
     * context.openFileInput(filename); final Writer writer = new
     * StringWriter(); final char[] buffer = new char[1024]; try { final Reader
     * reader = new BufferedReader(new InputStreamReader(iStream, "UTF-8")); int
     * n; while ((n = reader.read(buffer)) != -1) { writer.write(buffer, 0, n);
     * } } catch (final IOException e) { e.printStackTrace(); } finally { try {
     * iStream.close(); } catch (final IOException e) { return null; } } return
     * writer.toString(); }
     */

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        final int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) {
            return 1;
        } else {
            return k;
        }
    }

    public static Bitmap getThumbnail(Uri uri, Context context, int thumbSize) {
        InputStream input = null;
        try {
            input = context.getContentResolver().openInputStream(uri);
            final BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;
            onlyBoundsOptions.inDither = true;// optional
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
            input.close();
            if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
                return null;
            }
            final int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

            final double ratio = (originalSize > thumbSize) ? (originalSize / thumbSize) : 1.0;
            final BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
            bitmapOptions.inDither = true;// optional
            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
            input = context.getContentResolver().openInputStream(uri);
            final Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
            input.close();
            return bitmap;
        } catch (final Exception e) {
        } finally {
            try {
                input.close();
            } catch (final Exception e) {
            }
        }
        return null;
    }

    public static <T extends SortableScheduleClass> List<Pair<String, List<T>>> groupData(List<T> data, int numChars) {
        final Map<String, List<T>> groupedData = new HashMap<String, List<T>>();
        for (final T entry : data) {
            String entryName = entry.getName();
            if (entryName.length() > 0 & entryName.length() >= numChars) {
                final String character = entryName.substring(0, numChars);
                List<T> group = groupedData.get(character);
                if (group == null) {
                    group = new ArrayList<T>();
                }
                group.add(entry);
                groupedData.put(character, group);
            }
        }

        final List<Pair<String, List<T>>> allData = new ArrayList<Pair<String, List<T>>>();
        final SortedSet<String> keys = new TreeSet<String>(groupedData.keySet());
        for (final String character : keys) {
            final List<T> values = groupedData.get(character);
            Collections.sort(values);
            allData.add(new Pair<String, List<T>>(character, new ArrayList<T>(values)));
        }
        return allData;
    }

    public static Map<String, List<ScheduleTask>> groupTaskByLessonId(List<ScheduleTask> data) {
        final Map<String, List<ScheduleTask>> tasks = new HashMap<String, List<ScheduleTask>>();
        for (final Iterator<ScheduleTask> iterator = data.iterator(); iterator.hasNext(); ) {
            final ScheduleTask task = iterator.next();
            final String lessonId = task.getLessonId();
            if (tasks.containsKey(lessonId)) {
                tasks.get(lessonId).add(task);
            } else {
                final List<ScheduleTask> tasksList = new ArrayList<ScheduleTask>();
                tasksList.add(task);
                tasks.put(lessonId, tasksList);
            }
        }
        return tasks;
    }

    public static <T> byte[] makeByteArrayFromObject(T object) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            return bos.toByteArray();
        } catch (final Exception e) {
        } finally {
            try {
                out.close();
            } catch (final Exception e) {
            }
        }
        return null;
    }

    public static <T> T makeObjectFromByteArray(byte[] array) {
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(new ByteArrayInputStream(array));
            final T object = (T) is.readObject();
            return object;
        } catch (final Exception e) {
            LogUtil.d(DbAdapterScheduleTask.class.getSimpleName(), "makeObjectFromByteArray. Error = " + e.getMessage());
        } finally {
            try {
                is.close();
            } catch (final Exception e) {
            }
        }
        return null;
    }

    public static String makePostBody(Map<String, String> params) {
        String output = "";
        for (String key : params.keySet()) {
            try {
                if (output.length() != 0) {
                    output += "&";
                }
                output += key + "=" + URLEncoder.encode(params.get(key), "UTF-8");
            } catch (Exception e) {
            }
        }
        return output;
    }

    public static Bitmap resizeBitmap(Bitmap bmp, Context context, int thumbSize) {
        final int width = bmp.getWidth();
        final int height = bmp.getHeight();

        final double originalSize = (height > width) ? height : width;
        final double ratio = (originalSize > thumbSize) ? (thumbSize / originalSize) : 1.0;

        final int destWidth = Double.valueOf(width * ratio).intValue();
        final int destHeight = Double.valueOf(height * ratio).intValue();

        final Bitmap output = Bitmap.createScaledBitmap(bmp, destWidth, destHeight, false);
        return output;
    }

    public static String storeImage(Context context, Bitmap image) {
        FileOutputStream fos = null;
        final File file = createDirectoryAndGenFilename();
        if (file == null) {
            return null;
        }
        try {
            fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                fos.close();
            } catch (final Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return file.getAbsolutePath();
    }

    /*
     * public static void writeDataToFile(Context context, String filename,
     * String data) { FileOutputStream fos = null; try { fos =
     * context.openFileOutput(filename, Context.MODE_PRIVATE); } catch (final
     * FileNotFoundException e) { e.printStackTrace(); } try {
     * fos.write(data.getBytes()); fos.close(); } catch (final Exception e) {
     * e.printStackTrace(); } }
     */

    public static Bitmap rotateImage(Bitmap bmp, float degrees) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Matrix mtx = new Matrix();
        mtx.postRotate(degrees);
        bmp = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
        return bmp;
    }

}


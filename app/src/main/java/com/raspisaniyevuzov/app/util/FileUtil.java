package com.raspisaniyevuzov.app.util;

import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;

/**
 * Created by SAPOZHKOV on 09.10.2015.
 */
public class FileUtil {

    public static final String APP_PUBLIC_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/rvuzov";
    public static final String APP_PUBLIC_TEMP_DIR = APP_PUBLIC_DIR + "/tmp";
    public static final String APP_PUBLIC_MEDIA_DIR = APP_PUBLIC_DIR + "/media";
    public static final String APP_PUBLIC_TEMP_IMAGE_FILE = APP_PUBLIC_TEMP_DIR + "/tmp_image.jpg";

    public static void copyInputToOutputStream(InputStream input, OutputStream output) throws Exception {
        try {
            copyStream(input, output);
            closeSilently(input);
        } catch (Exception ex) {
            Log.e(FileUtil.class.getSimpleName(), "copyInputToOutputStream() Cannot save file: " + ex.getMessage());
        } finally {
            closeSilently(output);
        }
    }

    public static String getNewFileName() {
        return APP_PUBLIC_MEDIA_DIR + "/" + getUniqueFileName() + ".png";
    }

    private static String getUniqueFileName() {
            return "" + System.currentTimeMillis();
    }

    /**
     * Transfer bytes from in to out
     *
     * @param in  - input stream
     * @param out - output stream
     * @throws IOException
     */
    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
            out.write(buf, 0, len);
        in.close();
        out.close();
    }

    public static void closeSilently(Closeable c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            // do nothing
        }
    }

    public static void mkDirs() {
        new File(APP_PUBLIC_DIR).mkdirs();
        new File(APP_PUBLIC_TEMP_DIR).mkdirs();
        new File(APP_PUBLIC_MEDIA_DIR).mkdirs();
    }

    public static String upload(String filename) {
        String result = null;
        try {
            HttpPost uploadFile = new HttpPost(Settings.RVUZOV_FILE_UPLOAD_URL);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", new File(filename), ContentType.APPLICATION_OCTET_STREAM, "file.jpg");
            HttpEntity multipart = builder.build();
            uploadFile.setEntity(multipart);
            CloseableHttpResponse response = HttpClients.createDefault().execute(uploadFile);

            java.util.Scanner s = new java.util.Scanner(response.getEntity().getContent()).useDelimiter("\\A");
            result = s.hasNext() ? s.next() : "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}

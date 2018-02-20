package com.raspisaniyevuzov.app.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.Display;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by SAPOZHKOV on 16.09.2015.
 */
public class ImageUtil {

    public static int convertDpToPixel(int dps, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public static int getDisplayWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    /**
     * Get the rotation of the last image added.
     *
     * @param context
     * @return
     */
    public static int getRotation(Context context) {
        int rotation = 0;
        ContentResolver content = context.getContentResolver();
        Cursor mediaCursor = content.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{"orientation", "date_added"}, null, null, "date_added desc");
        if (mediaCursor != null && mediaCursor.getCount() != 0) {
            while (mediaCursor.moveToNext()) {
                rotation = mediaCursor.getInt(0);
                break;
            }
        }
        if (mediaCursor != null)
            mediaCursor.close();
        return rotation;
    }

    public static Bitmap rotateImage(Context context, Uri selectedImage, int rotation) {
        Bitmap rotatedImg = null;
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap img = getScalableBitmap(selectedImage, context);
            if (img != null) {
                rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
                img.recycle();
            }
        }
        return rotatedImg;
    }

    public static int getDisplayHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static Bitmap getScalableBitmap(Uri uri, Context context) {
        InputStream in;
        ContentResolver contentResolver = context.getContentResolver();
        try {
            final int IMAGE_MAX_SIZE = 120000; // 120MP
            in = contentResolver.openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }

            Bitmap b;
            in = contentResolver.openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            return b;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Ресайз изображения
     *
     * @param inputUri    URI изображения для ресайза
     * @param outputPath  Путь для результата
     * @param maxSideSize Максимальный размер стороны
     * @param quality     качество
     * @return Успешность выполнения
     */

    public static boolean resizeBitmap(Uri inputUri, String outputPath, int maxSideSize, int quality, int scale) {
        InputStream is;
        float height;
        float width;

        try {
            is = new BufferedInputStream(new FileInputStream(inputUri.getPath()));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return false;
        }

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inSampleSize = scale;
        bmpFactoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, bmpFactoryOptions);

        if (bitmap == null)
            return false;

        int oldHeight = bitmap.getHeight();
        int oldWidth = bitmap.getWidth();

        if (oldHeight > oldWidth) {
            height = maxSideSize;
            width = oldWidth * height / oldHeight;
        } else {
            width = maxSideSize;
            height = oldHeight * width / oldWidth;
        }

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        File dir = new File(outputPath).getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        OutputStream out;
        try {
            out = new FileOutputStream(outputPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);

        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * Displays image from url in rounded ImageView
     *
     * @param context
     * @param url
     * @param imageView
     */
    public static void displayRoundedImage(final Context context, final Object url, final ImageView imageView) {
        Glide.with(context).load(url).asBitmap().centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    public static Bitmap getResizeBitmap(Uri uri, Context context) {
        Bitmap bitmap;
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inSampleSize = 2;
        bmpFactoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse("file://" + uri)), null, bmpFactoryOptions);
        } catch (FileNotFoundException|NullPointerException e) {
            bitmap = null;
        }
        return bitmap;
    }

    public static Uri saveBitmapToFile(String filename, Bitmap bmp) {
        FileOutputStream out = null;
        Uri result = null;
        try {
            out = new FileOutputStream(filename);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            result = Uri.parse(new File(filename).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Bitmap getBitmap(Uri uri, Context context) {
        Bitmap bitmap;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse("file://" + uri)), null, options);
        } catch (FileNotFoundException e) {
            bitmap = null;
        }
        return bitmap;
    }

    public static void displayImage(String url, ImageView imageView, Context context) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(imageView);
    }

    public static Bitmap getCropBitmap(Bitmap region, Context context) {
        int w = ImageUtil.convertDpToPixel(56, context);
        return ThumbnailUtils.extractThumbnail(region, w, w);
    }

    public static Bitmap getRoundedBitmap(Bitmap bitmap, int corner) {
        return ImageUtil.getRoundedCornerBitmap(bitmap, corner);
    }

}

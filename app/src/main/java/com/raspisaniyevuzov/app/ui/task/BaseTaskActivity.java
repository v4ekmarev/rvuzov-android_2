package com.raspisaniyevuzov.app.ui.task;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.db.model.File;
import com.raspisaniyevuzov.app.db.model.Subject;
import com.raspisaniyevuzov.app.db.model.Task;
import com.raspisaniyevuzov.app.ui.BaseActivity;
import com.raspisaniyevuzov.app.util.DbUtil;
import com.raspisaniyevuzov.app.util.FileUtil;
import com.raspisaniyevuzov.app.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by SAPOZHKOV on 16.10.2015.
 */
public abstract class BaseTaskActivity extends BaseActivity {

    protected Task mCurrentTask;
    protected Subject mSelectedSubject = null;
    protected RealmList<File> mImages = new RealmList<>();
    protected PhotoViewerFragment mPhotoViewerFragment;
    protected LinearLayout mImagesContainer;
    protected boolean mOpenPhotoViewer, mHasChanges = false;
    protected List<String> mImagesToDelete = new ArrayList<>();

    protected abstract boolean isDeletePhotoEnable();

    protected abstract void checkSaveButton();

    protected abstract void showMessage(String msg);

    protected abstract void onCloseActivity();

    public void addImage(final Uri uri, final boolean checkRotate) {
        new AsyncTask<Void, Void, BitmapObject>() {

            @Override
            protected BitmapObject doInBackground(Void... params) {
                boolean rotated = false;
                Bitmap initBitmap = null;
                if (checkRotate) {
                    int rotation = ImageUtil.getRotation(BaseTaskActivity.this);
                    if (rotation > 0) {
                        rotated = true;
                        initBitmap = ImageUtil.rotateImage(BaseTaskActivity.this, uri, rotation);
                    }
                }
                Bitmap resizedBitmap = (initBitmap != null) ? initBitmap : ImageUtil.getResizeBitmap(uri, BaseTaskActivity.this); // resize
                Bitmap bitmapForGallery = getImageForGallery(resizedBitmap);
                BitmapObject bitmapObject = new BitmapObject();
                bitmapObject.bitmapForGallery = bitmapForGallery;
                bitmapObject.bitmapForSave = resizedBitmap;
                bitmapObject.rotated = rotated;
                return bitmapObject;
            }

            @Override
            protected void onPostExecute(BitmapObject bitmaps) {
                if (bitmaps.bitmapForGallery != null)
                    displayImage(bitmaps.bitmapForGallery);
                if (bitmaps.bitmapForSave != null)
                    saveImage(bitmaps.bitmapForSave, bitmaps.rotated, uri);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void addImage(final Uri uri, final File file) {
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap initBitmap = ImageUtil.getBitmap(uri, BaseTaskActivity.this);
                return getImageForGallery(initBitmap);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    displayImage(bitmap);
                    mImages.add(file);
                    checkSaveButton();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void saveImage(final Bitmap bitmap, final boolean needToSave, final Uri uri) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                Uri newUri;
                if (needToSave) {
                    newUri = ImageUtil.saveBitmapToFile(FileUtil.getNewFileName(), bitmap);
                    deletePhotoFromDisk(uri.toString());
                } else newUri = uri;
                boolean success = newUri != null;
                if (success) {
                    com.raspisaniyevuzov.app.db.model.File file = new com.raspisaniyevuzov.app.db.model.File();
                    file.setId(DbUtil.getNewUid());
                    file.setName(newUri.toString());
                    mImages.add(file);
                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                showMessage((success) ? getString(R.string.image_added_success) : getString(R.string.image_added_error));
                checkSaveButton();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class BitmapObject {
        Bitmap bitmapForSave;
        Bitmap bitmapForGallery;
        boolean rotated;
    }

    @Override
    public void onBackPressed() {
        onClose();
    }

    private void displayImage(Bitmap bitmap) {
        View view = getLayoutInflater().inflate(R.layout.task_image, null);
        ((ImageView) view.findViewById(R.id.ivImage)).setImageBitmap(bitmap);
        final int position = mImages.size();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard2(BaseTaskActivity.this);
                findViewById(R.id.photoContainer).setVisibility(View.VISIBLE);
                mOpenPhotoViewer = true;
                if (mPhotoViewerFragment == null)
                    openPhotoViewer(position);
                else
                    mPhotoViewerFragment.setCurrentPhoto(position);
            }
        });
        mImagesContainer.addView(view);
    }

    public List<File> getFileList() {
        return mImages;
    }

    public void deletePhoto(int position) {
        mHasChanges = true;

        mImagesToDelete.add(mImages.get(position).getName());

        for (int i = position + 1; i < mImagesContainer.getChildCount(); i++) {
            View view = mImagesContainer.getChildAt(i);
            final int pos = i - 1;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard2(BaseTaskActivity.this);
                    findViewById(R.id.photoContainer).setVisibility(View.VISIBLE);
                    mOpenPhotoViewer = true;
                    openPhotoViewer(pos);
                }
            });
        }
        mImages.remove(position);
        mImagesContainer.removeViewAt(position);
        if (mImages.size() == 0) onClose();
        else
            mPhotoViewerFragment.setCurrentPhoto(position > 0 ? position - 1 : 0);

        checkSaveButton();
    }

    public boolean deletePhotoFromDisk(String uri) {
        java.io.File file = new java.io.File(Uri.parse(uri).getPath());
        return (file.exists()) && file.delete();
    }

    private void openPhotoViewer(int pos) {
        mPhotoViewerFragment = new PhotoViewerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PhotoViewerFragment.PHOTO_POSITION, pos);
        bundle.putBoolean(PhotoViewerFragment.DELETE_PHOTO_ENABLE, isDeletePhotoEnable());
        mPhotoViewerFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.photoContainer, mPhotoViewerFragment).commit();
    }

    public void onClose() {
        if (mOpenPhotoViewer && mPhotoViewerFragment != null) {
            findViewById(R.id.photoContainer).setVisibility(View.GONE);
            mOpenPhotoViewer = false;
        } else
            onCloseActivity();
    }

    private Bitmap getImageForGallery(final Bitmap resizedBitmap) {
        Bitmap cropBitmap = null;
        if (resizedBitmap != null)
            cropBitmap = ImageUtil.getCropBitmap(resizedBitmap, this); // crop
        Bitmap bitmapImage = null;
        if (cropBitmap != null)
            bitmapImage = ImageUtil.getRoundedBitmap(cropBitmap, 20); // rounded
        return bitmapImage;
    }

}

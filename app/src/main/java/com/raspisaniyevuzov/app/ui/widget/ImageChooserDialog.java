package com.raspisaniyevuzov.app.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.util.FileUtil;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageChooserDialog extends DialogFragment {

    public static final int PICK_FROM_CAMERA = 2;
    public static final int PICK_FROM_FILE = 4;
    public static final String PHOTO_SOURCE_CAMERA = "photo_source_camera";
    public static final String PHOTO_SOURCE_GALLERY = "photo_source_gallery";
    private Uri mImageCaptureUri;
    private Uri mSaveImageUri;
    private String mPhotoSource;

    public interface OnImageChooserResultListener {
        void onImageChooserResult(Uri imageUri, String imageSource);
    }

    public static ImageChooserDialog newInstance() {
        ImageChooserDialog dialog = new ImageChooserDialog();
        dialog.setCancelable(true);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mImageCaptureUri = savedInstanceState.getParcelable("mImageCaptureUri");
            mSaveImageUri = savedInstanceState.getParcelable("mSaveImageUri");
            mPhotoSource = savedInstanceState.getString("mPhotoSource");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ListView listView = (ListView) layoutInflater.inflate(R.layout.image_chooser_dialog, null);

        final String[] items = new String[]{
                getString(R.string.select_image_source_camera),
                getString(R.string.select_image_source_gallery)};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.simple_list_item, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: { // Camera
                        mPhotoSource = PHOTO_SOURCE_CAMERA;
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        mImageCaptureUri = Uri.fromFile(new File(FileUtil.APP_PUBLIC_TEMP_IMAGE_FILE));
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        try {
                            intent.putExtra("return-data", true);
                            startActivityForResult(intent, PICK_FROM_CAMERA);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                    case 1: { // pick from file
                        mPhotoSource = PHOTO_SOURCE_GALLERY;
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                    }
                    break;
                }
            }
        });
        return CustomDialog.getMaterialDialogForList(context, listView, getString(R.string.select_image_source_title));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("mImageCaptureUri", mImageCaptureUri);
        outState.putParcelable("mSaveImageUri", mSaveImageUri);
        outState.putString("mPhotoSource", mPhotoSource);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    ((OnImageChooserResultListener) (getTargetFragment() != null ? getTargetFragment() : getContext())).onImageChooserResult(mImageCaptureUri, mPhotoSource);
                    dismiss();
                } else dismiss();
                break;
            case PICK_FROM_FILE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    mImageCaptureUri = data.getData();
                    if (mImageCaptureUri == null) {
                        dismiss();
                        return;
                    } else {
                        mSaveImageUri = Uri.fromFile(new File(FileUtil.APP_PUBLIC_TEMP_IMAGE_FILE));
                        try {
                            InputStream inputStream = getActivity().getContentResolver().openInputStream(mImageCaptureUri);
                            OutputStream outputStream = getActivity().getContentResolver().openOutputStream(mSaveImageUri);
                            FileUtil.copyInputToOutputStream(inputStream, outputStream);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        ((OnImageChooserResultListener) (getTargetFragment() != null ? getTargetFragment() : getContext())).onImageChooserResult(mSaveImageUri, mPhotoSource);
                        dismiss();
                    }
                } else
                    dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }

}

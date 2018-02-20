package com.raspisaniyevuzov.app.ui.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raspisaniyevuzov.app.R;

import java.io.FileNotFoundException;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoPageFragment extends Fragment {

    private static final String ARGUMENT_IMAGE_URL = "ARGUMENT_IMAGE_URL";
    private Bitmap mBitmap;

    private String imageUrl;
    private PhotoView mImageView;
    private PhotoViewAttacher mAttacher;

    public static Fragment newInstance(String photoUrl) {
        PhotoPageFragment pageFragment = new PhotoPageFragment();
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_IMAGE_URL, photoUrl);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUrl = getArguments().getString(ARGUMENT_IMAGE_URL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Need to call clean-up
        if (mAttacher != null)
            mAttacher.cleanup();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simple_photo_fragment, container, false);
        mImageView = (PhotoView) view.findViewById(R.id.ivPhoto);
        mAttacher = new PhotoViewAttacher(mImageView);

        if (mBitmap != null) displayImageWithZoom();
        else {
            Bitmap bitmap = null;
            try {
                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                bmpFactoryOptions.inSampleSize = 2;
                bmpFactoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(Uri.parse("file://" + imageUrl)), null, bmpFactoryOptions);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                mBitmap = bitmap;
                displayImageWithZoom();
            }
        }

        return view;
    }

    private void displayImageWithZoom() {
        mImageView.setImageBitmap(mBitmap);
        mAttacher.update();
    }

}
package com.testpro.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.testpro.dressmatch.R;
import com.testpro.model.DressSlideItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.PagerAdapter;

public class DressSlideAdapter extends PagerAdapter {
    
    private List<DressSlideItem> dressSlideItem;
    private Context context;

    public DressSlideAdapter(Context context, List<DressSlideItem> dressSlideItem) {
        this.context = context;
        this.dressSlideItem = dressSlideItem;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return dressSlideItem.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View imageLayout = LayoutInflater.from(container.getContext()).inflate(R.layout.dress_slide_template, container, false);

        assert imageLayout != null;
        final ImageView ivSlide = (ImageView) imageLayout.findViewById(R.id.ivSlide);

        File imgFile = new  File(Environment.getExternalStorageDirectory()+"/"+context.getString(R.string.app_fldr_name), dressSlideItem.get(position).getImgNm());
        if(imgFile.exists()){
            //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            //ivSlide.setImageBitmap(myBitmap);

            Bitmap bm = decodeSampledBitmapFromUri(imgFile.getAbsolutePath(), 500, 500);
            ivSlide.setImageBitmap(bm);
        }
        container.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

        Bitmap bm = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }



}

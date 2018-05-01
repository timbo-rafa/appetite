package com.rafaeltimbo.appetite.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rafaeltimbo.appetite.utils.DownloadImageTask;

import java.util.List;

public class PhotoAdapter extends PagerAdapter {
    private Context mContext;
    private List<String> mImageUrls;
    //private int[] mImageUrls;

    public PhotoAdapter(Context context, List<String> imageUrls) {
        this.mContext = context;
        this.mImageUrls = imageUrls;
        //mImageUrls = new int[]{R.drawable.details_photo, R.drawable.food_example};
    }


    @Override
    public int getCount() {
        //return mImageUrls.length;
        return mImageUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setMaxHeight(60);
        imageView.setMaxWidth(60);
        imageView.setScaleType( ImageView.ScaleType.CENTER_CROP );
        //imageView.setImageResource(mImageUrls[position]);
        new DownloadImageTask(imageView).execute(mImageUrls.get(position));
        container.addView(imageView, 0);

        Log.d("IMG", String.valueOf(position) + " " + String.valueOf(getCount()));

        return  imageView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((ImageView)object);
    }
}

package com.ctvit.shitingxizang.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.ctvit.shitingxizang.R;

/**
 * Created by fengmq on 2019/10/30
 */
public class ImageLoader {

    public static void load(Context context, String url, ImageView iv) {
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(iv);
    }


    public static void loadDrawable(Context context, Drawable drawable, ImageView iv) {
        Glide.with(context).load(drawable).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(iv);
    }


    /**
     * 把图片处理成圆形
     *
     * @param context 这个Context最好是传activity或者fragment 生命周期会伴随之
     * @param imgUrl  图片的地址
     * @param iv      需要作为载体的imageView
     */
    public static void loadCircleImage(Context context, String imgUrl, ImageView iv) {
        loadCircleImage(context, imgUrl, iv, 300, 300);
    }

    public static void loadCircleImage(Context context, String imgUrl, ImageView iv, int width, int height) {
        //设置图片圆角角度
        RoundedCorners roundedCorners = new RoundedCorners(30);
        //通过RequestOptions扩展功能
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(width, height)             //圆形
                .circleCrop();
        Glide.with(iv.getContext()).load(imgUrl).diskCacheStrategy(DiskCacheStrategy.ALL).apply(options).into(iv);
    }

}




package com.enipro.presentation.utility;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class BindingUtils {

    @BindingAdapter(value = {"url", "placeholder"}, requireAll = false)
    public static void setImageUrl(ImageView imageView, String imageUrl, Drawable placeholder) {
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .apply(new RequestOptions().placeholder(placeholder))
                .into(imageView);
    }

//    @BindingAdapter(value = {"cropCenterUrl", "placeholder"}, requireAll = false)
//    public static void setImageUrlCropCenter(ImageView view, String imageUrl, Drawable placeholder) {
//
//    }
}
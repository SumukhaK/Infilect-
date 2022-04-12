package com.ksa.infilect.adapter

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.ksa.infilect.R
import com.ksa.infilect.models.Result

class UserRowBinding {

    companion object{

        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl:String){
            //Log.v("UsersDataImageURL ",imageUrl)
            /*imageView.load(imageUrl){
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }*/
            Glide.with(imageView)
                .load(imageUrl)
                .centerCrop()
                .error(R.drawable.ic_error_placeholder)
                .into(imageView)
        }

        @BindingAdapter("parseFullName")
        @JvmStatic
        fun parseFullName(textView: TextView,
                      userResult: Result?){
            if(userResult != null){
                val desc = "${userResult.name.first} ${userResult.name.last}"
                textView.text = desc
            }
        }

    }
}
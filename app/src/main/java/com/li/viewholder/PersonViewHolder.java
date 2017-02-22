package com.li.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.li.bean.InforBean;
import com.li.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * Created by Mr.Jude on 2015/2/22.
 */
public class PersonViewHolder extends BaseViewHolder<InforBean> {
        private TextView mTv_name;
    private ImageView mImg_face;
    private TextView mTv_sign;



    public PersonViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_person);
        mTv_name = $(R.id.person_name);
        mTv_sign = $(R.id.person_sign);
        mImg_face = $(R.id.person_face);

    }

    @Override
    public void setData(final InforBean person) {
        mTv_name.setText(person.getSongName());
        mTv_sign.setText(person.getArtist());
        Glide.with(getContext())
                .load(person.getPicUrl())
                .placeholder(R.mipmap.ic_err_context)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(mImg_face);
    }



}

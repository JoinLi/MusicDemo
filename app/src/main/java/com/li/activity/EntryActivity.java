package com.li.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.li.R;
import com.li.bean.EntryBean;
import com.li.myactivity.IndexActivity;
import com.li.util.Constant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by hcc on 16/4/4.
 * <p>
 * Tips:App启动页面 该页面不要继承AppCompatActivity
 * 会导致界面启动卡顿 加载主题的原因.
 */
public class EntryActivity extends Activity {


    private ImageView mLuanchImage;
    private TextView mFormText;
    private static final int ANIMATION_DURATION = 2000;
    private static final float SCALE_END = 1.13F;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_luanch);
        initView();
        getLuanchImage();
    }

    private void initView() {
        mLuanchImage = (ImageView) findViewById(R.id.iv_luanch);
        mFormText = (TextView) findViewById(R.id.tv_form);
    }

    private void animateImage() {

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(mLuanchImage, "scaleX", 1f, SCALE_END);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mLuanchImage, "scaleY", 1f, SCALE_END);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIMATION_DURATION).play(animatorX).with(animatorY);
        set.start();

        set.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {

                startActivity(new Intent(EntryActivity.this, IndexActivity.class));
                EntryActivity.this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }


    public void getLuanchImage() {
        OkHttpUtils
                .get()
                .url(Constant.IMAGE + "1080*1776")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Glide.with(EntryActivity.this).load(R.drawable.qidong).into(mLuanchImage);
                        mFormText.setText("月下独酌");
                    }

                    @Override
                    public void onResponse(String string, int id) {
                        try {
                            Gson gson = new Gson();
                            EntryBean entryBean = gson.fromJson(string, EntryBean.class);
                            System.err.println("启动" + entryBean.getImg());
                            //设置图片
                            Glide.with(EntryActivity.this).load(entryBean.getImg()).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.qidong).into(mLuanchImage);
                            //设置标题
                            mFormText.setText(entryBean.getText());
                        } catch (Exception e) {
                            System.err.println("出错了");
                            Glide.with(EntryActivity.this).load(R.drawable.qidong).into(mLuanchImage);
                            e.printStackTrace();

                        }
                    }

                });
        animateImage();

    }
}

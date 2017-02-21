package com.li.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.li.R;
import com.li.bean.APIService;
import com.li.bean.InforBean;
import com.li.util.Constant;
import com.li.util.LogUtil;
import com.li.util.PreferenceUtil;
import com.li.util.ToastUtil;
import com.li.view.CustomRelativeLayout;
import com.li.view.CustomSettingView;
import com.li.view.LyricView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nineoldandroids.view.ViewHelper;
import com.wonderkiln.blurkit.BlurLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity_Music extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, LyricView.OnPlayerClickListener {

    private LyricView lyricView;
    private MediaPlayer mediaPlayer;

    private View statueBar;
    private SeekBar display_seek;
    private TextView display_total;
    private TextView display_title;
    private TextView display_position;

    private ImageView btnPre, btnPlay, btnNext, btnSetting, btn_download;

    private int position = 0;
    private State currentState = State.STATE_STOP;
    private ValueAnimator press_animator, up_animator;

    private ViewStub setting_layout, main_download_layout;
    private CustomSettingView customSettingView;
    private CustomRelativeLayout customRelativeLayout;
    private CustomRelativeLayout downRelativeLayout;

    private final int MSG_REFRESH = 0x167;
    private final int MSG_LOADING = 0x177;
    private final int MSG_LYRIC_SHOW = 0x187;

    private long animatorDuration = 120;

    private List<InforBean> list;
    private BlurLayout blurLayout;
    private ImageView relat_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus();
            SharedPreferences share = getSharedPreferences("Data", MODE_PRIVATE);
            if (share.getBoolean("isChecked", false)) {
                setContentView(R.layout.activity_main_music_blurkit);
            } else {
                setContentView(R.layout.activity_main_music);
            }

        } else {
            setContentView(R.layout.activity_main_music);
        }


        initAllViews();
        initAllDatum();
    }

    @TargetApi(19)
    private void setTranslucentStatus() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        final int status = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        params.flags |= status;
        window.setAttributes(params);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void initAllViews() {
//        blurLayout = (BlurLayout) findViewById(R.id.blurLayout);
        relat_img = (ImageView) findViewById(R.id.relat_img);
        statueBar = findViewById(R.id.statue_bar);
        statueBar.getLayoutParams().height = getStatusBarHeight();
        display_title = (TextView) findViewById(R.id.title_view);
        display_position = (TextView) findViewById(android.R.id.text1);
        display_total = (TextView) findViewById(android.R.id.text2);
        display_seek = (SeekBar) findViewById(android.R.id.progress);
        display_seek.setOnSeekBarChangeListener(this);
        btnNext = (ImageView) findViewById(android.R.id.button3);
        btnPlay = (ImageView) findViewById(android.R.id.button2);
        btnPre = (ImageView) findViewById(android.R.id.button1);
        btnSetting = (ImageView) findViewById(R.id.action_setting);
        btn_download = (ImageView) findViewById(R.id.btn_download);
        btnSetting.setOnClickListener(this);
        btn_download.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        lyricView = (LyricView) findViewById(R.id.lyric_view);
        lyricView.setOnPlayerClickListener(this);
        lyricView.setLineSpace(PreferenceUtil.getInstance(MainActivity_Music.this).getFloat(PreferenceUtil.KEY_TEXT_SIZE, 12.0f));
        lyricView.setTextSize(PreferenceUtil.getInstance(MainActivity_Music.this).getFloat(PreferenceUtil.KEY_TEXT_SIZE, 15.0f));
        lyricView.setHighLightTextColor(PreferenceUtil.getInstance(MainActivity_Music.this).getInt(PreferenceUtil.KEY_HIGHLIGHT_COLOR, Color.parseColor("#31C27C")));

        setting_layout = (ViewStub) findViewById(R.id.main_setting_layout);
        main_download_layout = (ViewStub) findViewById(R.id.main_download_layout);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        blurLayout.startBlur();
//        blurLayout.lockView();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        blurLayout.pauseBlur();
    }

    private void initAllDatum() {
        list = getIntent().getExtras().getParcelableArrayList("medias");
        position = getIntent().getExtras().getInt("position");
//        Glide.with(this).load(list.get(position).getPicUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                Drawable drawable = new BitmapDrawable(resource);
//                relat_img.setBackground(drawable);
//                blurLayout.startBlur();
//                blurLayout.lockView();
//            }
//        }); //方法中设置asBitmap可以设置回调类型
//        blurLayout.startBlur();
//        blurLayout.lockView();
        mediaPlayerSetup();  // 准备


    }

    /**
     * 准备
     */
    private void mediaPlayerSetup() {
        display_title.setText(list.get(position).getSongName());
        handler.removeMessages(MSG_LYRIC_SHOW);
        handler.sendEmptyMessageDelayed(MSG_LYRIC_SHOW, 420);
        Glide.with(this).load(list.get(position).getPicUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.qidong).into(relat_img);
//        Glide.with(this).load(list.get(position).getPicUrl()).bitmapTransform(new BlurTransformation(this, 5)).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.qidong).crossFade(1000).into(relat_img);
    }



    /**
     * 停止
     */
    private void stop() {
        if (null != mediaPlayer) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeMessages(MSG_REFRESH);
        lyricView.reset("载入歌词ing...");
        setCurrentState(State.STATE_STOP);
    }

    /**
     * 暂停
     */
    private void pause() {
        if (mediaPlayer != null && currentState == State.STATE_PLAYING) {
            setCurrentState(State.STATE_PAUSE);
            mediaPlayer.pause();
            handler.removeMessages(MSG_REFRESH);
        }
    }

    /**
     * 开始
     */
    private void start() {
        if (mediaPlayer != null && (currentState == State.STATE_PAUSE || currentState == State.STATE_PREPARE)) {
            setCurrentState(State.STATE_PLAYING);
            mediaPlayer.start();
            handler.sendEmptyMessage(MSG_REFRESH);
        }
    }

    /**
     * 上一首
     */
    private void previous() {
        stop();
        position--;
        if (position < 0) {
            position = list.size() - 1;
        }
        mediaPlayerSetup();
    }

    /**
     * 下一首
     */
    private void next() {
        stop();
        position++;
        if (position >= list.size()) {
            position = 0;
        }
        mediaPlayerSetup();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //网络流媒体的缓冲监听
        mediaPlayer.setOnBufferingUpdateListener(MainActivity_Music.this);
        setCurrentState(State.STATE_PREPARE);
        DecimalFormat format = new DecimalFormat("00");
        display_seek.setMax(mediaPlayer.getDuration());
        display_total.setText(format.format(mediaPlayer.getDuration() / 1000 / 60) + ":" + format.format(mediaPlayer.getDuration() / 1000 % 60));
        //MediaPlayer调用了prepareAsync方法后，待完成触发了OnPreparedListener的onPrepared方法后，才能调用MediaPlayer的start方法，否则会报错的。
        //在 start()之前给 流媒体播放结束做监听，否则会重复调用 onCompletion()方法


        //网络流媒体播放结束监听
        mediaPlayer.setOnCompletionListener(MainActivity_Music.this);
        start();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
        display_seek.setSecondaryProgress((int) (mediaPlayer.getDuration() * 1.00f * percent / 100.0f));

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        next();


    }

    /**
     * 设置当前播放状态
     */
    private void setCurrentState(State state) {
        if (state == this.currentState) {
            return;
        }
        this.currentState = state;
        switch (state) {
            case STATE_PAUSE:
                btnPlay.setImageResource(R.mipmap.player_btn_pause);
                break;
            case STATE_PLAYING:
                btnPlay.setImageResource(R.mipmap.player_btn_playing);
                break;
            case STATE_PREPARE:
                if (lyricView != null) {
                    lyricView.setPlayable(true);
                }
                setLoading(false);
                break;
            case STATE_STOP:
                if (lyricView != null) {
                    lyricView.setPlayable(false);
                }
                display_position.setText("--:--");
                display_seek.setSecondaryProgress(0);
                display_seek.setProgress(0);
                display_seek.setMax(100);
                btnPlay.setImageResource(R.mipmap.player_btn_pause);
                setLoading(false);
                break;
            case STATE_SETUP:
                File file = new File(Constant.lyricPath + list.get(position).getSongName() + ".lrc");
                LogUtil.m("绝对路径" + file.getAbsolutePath());
                LogUtil.m("相对路径" + file.getPath());
                if (file.exists()) {
                    lyricView.setLyricFile(file, "utf-8");

                } else {
                    downloadLyric(list.get(position).getLrcUrl(), file);
                }
                btnPlay.setImageResource(R.mipmap.player_btn_pause);
                setLoading(true);
                break;
            default:
                break;
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_REFRESH:
                    if (mediaPlayer != null) {
                        if (!display_seek.isPressed()) {
                            lyricView.setCurrentTimeMillis(mediaPlayer.getCurrentPosition());
                            DecimalFormat format = new DecimalFormat("00");
                            display_seek.setProgress(mediaPlayer.getCurrentPosition());
                            display_position.setText(format.format(mediaPlayer.getCurrentPosition() / 1000 / 60) + ":" + format.format(mediaPlayer.getCurrentPosition() / 1000 % 60));
                        }
                    }
                    handler.sendEmptyMessageDelayed(MSG_REFRESH, 120);
                    break;
                case MSG_LYRIC_SHOW:
                    try {
                        setCurrentState(State.STATE_SETUP);
                        mediaPlayer = new MediaPlayer();
                        //准备完成监听
                        mediaPlayer.setOnPreparedListener(MainActivity_Music.this);
//                       mediaPlayer.reset();// 把各项参数恢复到初始状态
                        mediaPlayer.setDataSource(list.get(position).getLqUrl());
                        mediaPlayer.prepareAsync();// 进行异步缓冲
//                        mediaPlayer.pause();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_LOADING:
                    Drawable background = btnPlay.getBackground();
                    int level = background.getLevel();
                    level = level + 300;
                    if (level > 10000) {
                        level = level - 10000;
                    }
                    background.setLevel(level);
                    handler.sendEmptyMessageDelayed(MSG_LOADING, 50);
                    break;
                default:
                    break;
            }
        }
    };

    private boolean mLoading = false;

    private void setLoading(boolean loading) {
        if (loading && !mLoading) {
            btnPlay.setBackgroundResource(R.drawable.rotate_player_loading);
            handler.sendEmptyMessageDelayed(MSG_LOADING, 200);
            mLoading = true;
            return;
        }
        if (!loading && mLoading) {
            handler.removeMessages(MSG_LOADING);
            btnPlay.setBackgroundColor(Color.TRANSPARENT);
            mLoading = false;
            return;
        }
    }

    @Override
    public void onPlayerClicked(long progress, String content) {
        if (mediaPlayer != null && (currentState == State.STATE_PLAYING || currentState == State.STATE_PAUSE)) {
            mediaPlayer.seekTo((int) progress);
            if (currentState == State.STATE_PAUSE) {
                start();
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            DecimalFormat format = new DecimalFormat("00");
            display_position.setText(format.format(progress / 1000 / 60) + ":" + format.format(progress / 1000 % 60));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeMessages(MSG_REFRESH);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mediaPlayer.seekTo(seekBar.getProgress());
        handler.sendEmptyMessageDelayed(MSG_REFRESH, 120);
    }

    private void downloadLyric(String url, File file) {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.download(url, file.getAbsolutePath(), true, true, new RequestCallBack<File>() {

            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                lyricView.setLyricFile(responseInfo.result, "utf-8");
            }

            @Override
            public void onFailure(HttpException e, String s) {
                lyricView.setLyricFile(null, null);
            }
        });


    }

    @Override
    public void onClick(View view) {
        if (press_animator != null && press_animator.isRunning()) {
            press_animator.cancel();
        }
        if (up_animator != null && up_animator.isRunning()) {
            up_animator.cancel();
        }
        switch (view.getId()) {
            case android.R.id.button1:
                previous();
                break;
            case android.R.id.button2:
                if (currentState == State.STATE_PAUSE) {
                    start();
                    break;
                }
                if (currentState == State.STATE_PLAYING) {
                    pause();
                    break;
                }
                break;
            case android.R.id.button3:
                next();
                break;
            case R.id.action_setting:
                if (customRelativeLayout == null) {
                    customRelativeLayout = (CustomRelativeLayout) setting_layout.inflate();
                    initCustomSettingView();

                }
                customRelativeLayout.show();
                break;
            case R.id.btn_download:
                if (downRelativeLayout == null) {
                    downRelativeLayout = (CustomRelativeLayout) main_download_layout.inflate();
                }
                downRelativeLayout.show();

                break;

            default:
                break;
        }
        press_animator = pressAnimator(view);
        press_animator.start();
    }

    public void myOnClick(View view) {
        DownloadingManager baseActivity = new DownloadingManager(MainActivity_Music.this, list, position);
        switch (view.getId()) {
            case R.id.text_lc:
                if (!list.get(position).getLqUrl().equals("")) {
                    baseActivity.DownloadMusics(list.get(position).getLqUrl(), null);
                    ToastUtil.showToast(this, "开始下载");
                    downRelativeLayout.dismiss();
                } else {

                    ToastUtil.showToast(this, getResources().getString(R.string.toast_message_lc));
                }


                break;
            case R.id.text_bz:
                if (!list.get(position).getHqUrl().equals("")) {
                    baseActivity.DownloadMusics(list.get(position).getHqUrl(), null);
                    ToastUtil.showToast(this, "开始下载");
                    downRelativeLayout.dismiss();
                } else {

                    ToastUtil.showToast(this, getResources().getString(R.string.toast_message_bz));
                }
                break;
            case R.id.text_hq:

                if (!list.get(position).getSqUrl().equals("")) {
                    baseActivity.DownloadMusics(list.get(position).getSqUrl(), null);
                    ToastUtil.showToast(this, "开始下载");
                    downRelativeLayout.dismiss();
                } else {

                    ToastUtil.showToast(this, getResources().getString(R.string.toast_message_hq));
                }
                break;
            case R.id.text_sq:
                if (!list.get(position).getFlacUrl().equals("")) {
                    baseActivity.DownloadMusics(list.get(position).getFlacUrl(), ".flac");
                    ToastUtil.showToast(this, "开始下载");
                    downRelativeLayout.dismiss();
                } else {

                    ToastUtil.showToast(this, getResources().getString(R.string.toast_message_sq));
                }
                break;
            case R.id.btn_close:
                downRelativeLayout.dismiss();
                break;
            default:
                break;
        }

    }

    private void initCustomSettingView() {
        customSettingView = (CustomSettingView) customRelativeLayout.getChildAt(0);
        customSettingView.setOnTextSizeChangeListener(new TextSizeChangeListener());
        customSettingView.setOnColorItemChangeListener(new ColorItemClickListener());
        customSettingView.setOnDismissBtnClickListener(new DismissBtnClickListener());
        customSettingView.setOnLineSpaceChangeListener(new LineSpaceChangeListener());
    }

    private class TextSizeChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                lyricView.setTextSize(15.0f + 3 * progress / 100.0f);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            PreferenceUtil.getInstance(MainActivity_Music.this).putFloat(PreferenceUtil.KEY_TEXT_SIZE, 15.0f + 3 * seekBar.getProgress() / 100.0f);
        }
    }

    private class LineSpaceChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                lyricView.setLineSpace(12.0f + 3 * progress / 100.0f);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            PreferenceUtil.getInstance(MainActivity_Music.this).putFloat(PreferenceUtil.KEY_LINE_SPACE, 12.0f + 3 * seekBar.getProgress() / 100.0f);
        }
    }

    private class DismissBtnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (customRelativeLayout != null) {
                customRelativeLayout.dismiss();
            }
        }
    }

    private class ColorItemClickListener implements CustomSettingView.OnColorItemChangeListener {

        @Override
        public void onColorChanged(int color) {
            lyricView.setHighLightTextColor(color);
            PreferenceUtil.getInstance(MainActivity_Music.this).putInt(PreferenceUtil.KEY_HIGHLIGHT_COLOR, color);
            if (customRelativeLayout != null) {
                customRelativeLayout.dismiss();
            }
        }
    }

    public ValueAnimator pressAnimator(final View view) {
        final float size = view.getScaleX();
        ValueAnimator animator = ValueAnimator.ofFloat(size, size * 0.7f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewHelper.setScaleX(view, (Float) animation.getAnimatedValue());
                ViewHelper.setScaleY(view, (Float) animation.getAnimatedValue());
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ViewHelper.setScaleX(view, size * 0.7f);
                ViewHelper.setScaleY(view, size * 0.7f);
                up_animator = upAnimator(view);
                up_animator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                ViewHelper.setScaleX(view, size * 0.7f);
                ViewHelper.setScaleY(view, size * 0.7f);
            }
        });
        animator.setDuration(animatorDuration);
        return animator;
    }

    public ValueAnimator upAnimator(final View view) {
        final float size = view.getScaleX();
        ValueAnimator animator = ValueAnimator.ofFloat(size, size * 10 / 7.00f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewHelper.setScaleX(view, (Float) animation.getAnimatedValue());
                ViewHelper.setScaleY(view, (Float) animation.getAnimatedValue());
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ViewHelper.setScaleX(view, size * 10 / 7.00f);
                ViewHelper.setScaleY(view, size * 10 / 7.00f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                ViewHelper.setScaleX(view, size * 10 / 7.00f);
                ViewHelper.setScaleY(view, size * 10 / 7.00f);
            }
        });
        animator.setDuration(animatorDuration);
        return animator;
    }

    private enum State {
        STATE_STOP, STATE_SETUP, STATE_PREPARE, STATE_PLAYING, STATE_PAUSE;
    }


    @Override
    protected void onDestroy() {
        stop();
        super.onDestroy();
    }


}



package com.li.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.kyleduo.switchbutton.SwitchButton;
import com.li.R;


/**
 * Created by hcc on 16/5/15 16:33
 * 986483793@qq.com
 */
public class SettingActivity extends AppCompatActivity {

    private SwitchButton blurkit_sb, update_sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();

//
    }

    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("通用设置");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        blurkit_sb = (SwitchButton) findViewById(R.id.blurkit_sb);
        update_sb = (SwitchButton) findViewById(R.id.update_sb);
        SharedPreferences share = getSharedPreferences("Data", MODE_PRIVATE);
        blurkit_sb.setChecked(share.getBoolean("isChecked", false));

        SharedPreferences share_update = getSharedPreferences("Update", MODE_PRIVATE);
        update_sb.setChecked(share_update.getBoolean("isUpdate", true));
        blurkit_sb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences share = getSharedPreferences("Data", MODE_PRIVATE);
                SharedPreferences.Editor edit = share.edit();
                if (isChecked) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        Snackbar.make(buttonView, "sorry，android4.4以下不能开启模糊效果", Snackbar.LENGTH_LONG).show();
                        blurkit_sb.setChecked(false);
                    } else {
                        Snackbar.make(buttonView, "开启高斯模糊", Snackbar.LENGTH_LONG).show();
                        edit.putBoolean("isChecked", isChecked);
                        edit.commit();
                    }

                } else {
                    Snackbar.make(buttonView, "关闭高斯模糊", Snackbar.LENGTH_LONG).show();
                    edit.putBoolean("isChecked", isChecked);
                    edit.commit();
                }

            }
        });

        update_sb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Snackbar.make(buttonView, "开启在线更新", Snackbar.LENGTH_LONG).show();

                } else {
                    Snackbar.make(buttonView, "关闭在线更新", Snackbar.LENGTH_LONG).show();

                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences share_update = getSharedPreferences("Update", MODE_PRIVATE);
        SharedPreferences.Editor edit = share_update.edit();
        edit.putBoolean("isUpdate", update_sb.isChecked());
        edit.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_path_layout: //下载路径
                Snackbar.make(v, "下载路径默认手机内存Musics\\song", Snackbar.LENGTH_LONG).show();
                break;

            case R.id.android_about_app:
                Snackbar.make(v, "点啥点啊，不知道我只是个图标啊", Snackbar.LENGTH_LONG).show();
                break;

            default:
                break;
        }

    }

}

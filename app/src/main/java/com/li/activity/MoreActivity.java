package com.li.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.li.R;
import com.li.view.PickDialog;


/**
 * Created by hcc on 16/5/15 16:33
 * 986483793@qq.com
 */
public class MoreActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        initView();

//
    }

    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("更多选项");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
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
            case R.id.more_btn_about_app: //关于我
                startActivity(new Intent(MoreActivity.this, AppAboutActivity.class));

                break;
            case R.id.cardView: //我的主页
                PickDialog dialog = new PickDialog(MoreActivity.this, "喜欢的话，可以捐助我哦");
                dialog.show();
                break;
            case R.id.more_btn_feed_back: //意见反馈
                startActivity(new Intent(MoreActivity.this, MessageActivity.class));
                break;
            case R.id.more_btn_setting: //通用设置
                Snackbar.make( v, "简单点，说话的方式简单点...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;

            default:
                break;
        }

    }

}

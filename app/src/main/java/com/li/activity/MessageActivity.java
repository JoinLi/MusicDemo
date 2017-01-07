package com.li.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.li.R;
import com.li.util.ToastUtil;


/**
 * Created by hcc on 16/5/15 16:33
 * 100332338@qq.com
 */
public class MessageActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText mFeedBack;

    private TextView mTip;

    private Button mSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();

//
    }

    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("意见反馈");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        mFeedBack = (EditText) findViewById(R.id.feed_edit);
        mTip = (TextView) findViewById(R.id.tip);
        mSubmit = (Button) findViewById(R.id.btn_submit);

        mSubmit.setOnClickListener(this);
        mFeedBack.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mTip.setText(String.valueOf(160 - s.length()));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_submit) {
            String text = mFeedBack.getText().toString().trim();
            if (TextUtils.isEmpty(text)) {

                ToastUtil.showToast(MessageActivity.this, "输入的内容不能为空");

                return;
            }

            sendFeedBackText(text);
        }
    }

    private void sendFeedBackText(String text) {

//        ToastUtil.showToast(MessageActivity.this, "提交成功");

    }
}

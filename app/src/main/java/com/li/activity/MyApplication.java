package com.li.activity;

import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.li.util.Constant;
import com.liulishuo.filedownloader.FileDownloader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MyApplication extends Application {
	// 请求队列
	private static RequestQueue queues;
	private static MyApplication instance;

	public static MyApplication getInstance() {
		return instance;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		queues = Volley.newRequestQueue(getApplicationContext());
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
				.connectTimeout(10000L, TimeUnit.MILLISECONDS)
				.readTimeout(10000L, TimeUnit.MILLISECONDS)
				//其他配置
				.build();

		OkHttpUtils.initClient(okHttpClient);
		instance = this;
		FileDownloader.init(getApplicationContext());
		File file = new File(Constant.lyricPath);
		if(!file.exists()) {
			file.mkdirs();
		}

	}
	public static RequestQueue getApplication(StringRequest stringRequest) {
		return queues;
	}

}

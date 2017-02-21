package com.li.activity;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.li.util.LogUtil;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by hcc on 16/5/15 16:33
 * 986483793@qq.com
 */
public class DownloadingManager extends AppCompatActivity {
    private int index;
    private String name;
    private DownloadManager manager;//下载管理器
    private long downloadId;
    private Context mContext;

    public DownloadingManager(Context mContext, String name, int index) {
        this.name = name;
        this.index = index;
        this.mContext = mContext;
        manager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);


    }

    public void DownloadMusics(String path) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(index);
        query.setFilterByStatus(DownloadManager.STATUS_RUNNING);//正在下载
        Cursor c = manager.query(query);
        if (c.moveToNext()) {
            //正在下载中，不重新下载
        } else {
            //创建下载请求
            DownloadManager.Request down = new DownloadManager.Request(Uri.parse(path));
            //设置允许使用的网络类型，这里是移动网络和wifi都可以
            down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            //显示在下载界面，即下载后的文件在系统下载管理里显示
            down.setVisibleInDownloadsUi(true);
            //设置下载标题
            down.setTitle(name);
            //显示Notification
            down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

            File folder = new File("/Musics/song/");
            LogUtil.m("绝对路径" + folder.getAbsolutePath());
            LogUtil.m("相对路径" + folder.getPath());
            if (!folder.exists() && !folder.isDirectory()) {
                folder.mkdirs();
            }
            //获取url后缀名
            String suffixes = "avi|mpeg|3gp|mp3|mp4|flac|wav|jpeg|gif|jpg|png|apk|exe|pdf|rar|zip|docx|doc";
            Pattern pat = Pattern.compile("[\\.](" + suffixes + ")");//正则判断
            Matcher mc = pat.matcher(path);//条件匹配
            String suffixName = null;
            while (mc.find()) {
                suffixName = mc.group();//截取文件名后缀名
                Log.e("substring:", suffixName);
            }
            down.setDestinationInExternalPublicDir("/Musics/song/", name + suffixName);
            //允许被扫描到
            down.allowScanningByMediaScanner();
            //将下载请求放入队列,返回值为downloadId
            downloadId = manager.enqueue(down);
        }

    }


}

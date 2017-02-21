package com.li.activity;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import com.li.bean.InforBean;
import com.li.util.Constant;
import com.li.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


/**
 * Created by hcc on 16/5/15 16:33
 * 986483793@qq.com
 */
public class DownloadingManager extends AppCompatActivity  {
    private List<InforBean> list;
    private int index;
    private DownloadManager manager;//下载管理器
    private long downloadId;
    private Context mContext;

    public DownloadingManager(Context mContext, List<InforBean> list, int index) {
        this.list = list;
        this.index = index;
        this.mContext=mContext;
        manager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);


    }

   public  void DownloadMusics(String path, String flac) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(list.get(index).getSongId());
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
            down.setTitle(list.get(index).getSongName());
            //显示Notification
            down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

            File folder = new File("/Musics/song/");
            LogUtil.m("绝对路径"+folder.getAbsolutePath());
            LogUtil.m("相对路径"+folder.getPath());
            if (!folder.exists() && !folder.isDirectory()) {
                folder.mkdirs();
            }
            //设置下载后文件存放的位置，在SDCard/Android/data/你的应用的包名/files/目录下面
            if (flac != null) {
                down.setDestinationInExternalPublicDir("/Musics/song/",list.get(index).getSongName() +flac);
            } else {
                down.setDestinationInExternalPublicDir("/Musics/song/", list.get(index).getSongName() + ".mp3");
            }

            flac = null;//falc制空
            //允许被扫描到
            down.allowScanningByMediaScanner();
            //将下载请求放入队列,返回值为downloadId
            downloadId = manager.enqueue(down);
        }

    }


}

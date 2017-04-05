package com.li.util;

import android.os.Environment;

/**
 * Created by MarioStudio on 2016/7/18.
 */
public class Constant {

    public static final String lyricPath = Environment.getExternalStorageDirectory() + "/Musics/lyric/";
    public static final String  SongPath = Environment.getExternalStorageDirectory() + "/Musics/song/";
    public static final String IMAGE = "http://news-at.zhihu.com/api/4/start-image/";  // 启动图 start-image/1080*1776

    //2.20 更新api
    public static final String  Song_QQ_Path ="http://3g.gljlw.com/music/";
    public static final String  Song_Update ="https://raw.githubusercontent.com/JoinLi/index/master/music";

    //2.24 增加新api  post
    public static final String  Song_New_Path ="http://1024me.butterfly.mopaasapp.com/show.php?t=netmusic";
}

package com.li.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.li.bean.APIService;
import com.li.bean.UpdateBean;
import com.li.util.Constant;
import com.li.util.LogUtil;
import com.li.util.ToastUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import okhttp3.Call;


public class Updae_MainActivity extends AppCompatActivity {

    private int m_newVerCode; // 最新版的版本号
    private String m_newVerName; // 最新版的版本名
    private String m_appNameStr = "webbiao.apk"; // 下载到本地要给这个APP命的名字
    private String m_appPath; // 下载地址
    private String m_update; // 更新说明
    private MaterialDialog updateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化相关变量
    }

    /**
     * 从服务器获取当前最新版本号，如果成功返回TURE，如果失败，返回FALSE
     *
     * @return
     */
    public void CheckNewestVersion() {
        OkHttpUtils
                .get()
                .url(Constant.Song_Update)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Snackbar.make(getCurrentFocus(), "当前为最新版本", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            LogUtil.m(response);
                            Gson gson = new Gson();
                            UpdateBean updateBean = gson.fromJson(response, UpdateBean.class);
                            m_newVerName = updateBean.getMydata().get(0).getVersionName();
                            m_newVerCode = updateBean.getMydata().get(0).getVersionCode();
                            m_appPath = updateBean.getMydata().get(0).getDizhi();
                            m_update=updateBean.getMydata().get(0).getBbmingcheng();
                            LogUtil.m("版本名称"+m_newVerName+ "版本号" + m_newVerCode+"下载地址"+m_appPath);
                            int vercode = getVerCode(getApplicationContext()); //
                            if (m_newVerCode > vercode) {
                                LogUtil.m("更新新版本+m_newVerCode" );
                                doNewVersionUpdate(); // 更新新版本
                            }else{
                                LogUtil.m("已是最新版本" );
                                ToastUtil.showToast(Updae_MainActivity.this,"已是最新版本");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }

                });


    }

    /**
     * 提示更新新版本
     */
    private void doNewVersionUpdate() {
        String verName = getVerName(getApplicationContext());
//        final String str = "当前版本：" + verName + " ,发现新版本：" + m_newVerName + " ,是否更新？";
        new MaterialDialog.Builder(this)
                .title("客户端更新")
                .content(m_update)
                .positiveText("确认")
                .negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        showUpdateDialog(m_appPath, m_update);
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })

                .show() //设置点击空白处diaolg不消失
                .setCanceledOnTouchOutside(false);
    }


    /**
     * 显示版本更新对话框
     *
     * @param apkUrl
     */
    private void showUpdateDialog(final String apkUrl, String description) {
        updateDialog = new MaterialDialog.Builder(this)
                .title("客户端更新")
                .content(description)
                .contentGravity(GravityEnum.CENTER)
                .progress(false, 100, false)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                })
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        apkDownload(apkUrl);
                    }
                }).show()
                ;
    }

    /**
     * 下载apk
     *
     * @param apkUrl
     */
    public void apkDownload(String apkUrl) {
        BaseDownloadTask task = FileDownloader.getImpl().create(apkUrl);
        task.setPath(getApkPath());
        task.setListener(new FileDownloadSampleListener() {
            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                LogUtil.d("apk download:" + soFarBytes * 100 / totalBytes);
                int progress = soFarBytes * 100 / totalBytes;
                downloadProgress(progress);
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                super.completed(task);
                downloadProgress(100);
                File file = new File(task.getPath());
                openApk(MyApplication.getInstance(), file);
            }
        });
        task.start();

    }

    private void downloadProgress(int progress) {
        if (updateDialog != null && updateDialog.isShowing()) {
            updateDialog.setProgress(progress);
            if (progress == 100) {
                updateDialog.dismiss();
            }
        }
    }

    /**
     * 获取apk放置的地址
     *
     * @return
     */
    public String getApkPath() {

        File file = new File(Environment.getExternalStorageDirectory(), m_appNameStr);
        if (file.exists()) {
            file.delete();
        }
        return file.getPath();
    }


    /**
     * open apk
     *
     * @param context
     * @param apk
     */
    public static void openApk(Context context, File apk) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apk),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    public static int getVerCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }


    }

    /**
     * 获取版本名称
     *
     * @param context
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("msg", e.getMessage());
        }
        return verName;
    }
}
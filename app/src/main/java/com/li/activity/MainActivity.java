package com.li.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnDismissListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.li.R;
import com.li.adapter.PersonAdapter;
import com.li.bean.InforBean;
import com.li.util.ClearEditText;
import com.li.util.NetUtils;
import com.li.util.ToastUtil;
import com.li.util.Util;
import com.li.widget.floatingmusicmenu.FloatingMusicMenu;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import me.curzbin.library.BottomDialog;
import me.curzbin.library.Item;
import me.curzbin.library.OnItemClickListener;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity implements RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, OnDismissListener, com.bigkoo.alertview.OnItemClickListener {
    private EasyRecyclerView recyclerView;
    private PersonAdapter adapter;
    private List<InforBean> list = new ArrayList<InforBean>();
    private int page = 1;
    private ClearEditText mClearEditText;
    private String context = "薛之谦";
    private String sou = "wy"; //搜索引擎
    private int index; //点击item的位置
    private AlertView mAlertView;
    //    private NetReceiver mReceiver = new NetReceiver();
    //FloatingMusicButton
    private FloatingMusicMenu musicMenu;
    private FloatingActionButton playingBtn, modeBtn, detailBtn, nextBtn;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(mReceiver, mFilter);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit_qd);
        recyclerView = (EasyRecyclerView) findViewById(R.id.recyclerView);
        setSupportActionBar(toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, Util.dip2px(this, 0.5f), Util.dip2px(this, 72), 0);
        itemDecoration.setDrawLastItem(false);
        recyclerView.addItemDecoration(itemDecoration);
        adapter = new PersonAdapter(this);
        recyclerView.setAdapterWithProgress(adapter);
        adapter.setMore(R.layout.view_more, this);
        adapter.setNoMore(R.layout.view_nomore);
        adapter.setError(R.layout.view_error, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {

            }

            @Override
            public void onErrorClick() {
                adapter.resumeMore();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new BottomDialog(MainActivity.this)
                                .title(R.string.share_title)
                                .orientation(BottomDialog.LINEAR)   //HORIZONTAL  VERTICAL   GRID  LINEAR
                                .inflateMenu(R.menu.menu_share)
                                .itemClick(new OnItemClickListener() {
                                    @Override
                                    public void click(Item item) {
                                        MusicSouSuo(item.getTitle());
                                        Toast.makeText(MainActivity.this, getString(R.string.share_title) + item.getTitle(), Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .show();
                    }
                });

        adapter.setOnItemLongClickListener(new RecyclerArrayAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(int position) {
                index = position;
                new AlertView(null, null, null, new String[]{getResources().getString(R.string.text_message_lc), getResources().getString(R.string.text_message_bz), getResources().getString(R.string.text_message_hq), getResources().getString(R.string.text_message_sq)},
                        new String[]{"取消",},
                        MainActivity.this, null, MainActivity.this).show();

                return true;
            }
        });
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                final Intent intent = new Intent(MainActivity.this, MainActivity_Music.class);
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);                                       //一定要获得 adapter数据 List<InforBean>类型
                bundle.putParcelableArrayList("medias", (ArrayList<? extends Parcelable>) adapter.getAllData());
                intent.putExtras(bundle);
//                if (NetUtils.getConnectedType(MainActivity.this) == 1) {
//                    startActivity(intent);
//                } else {
//
//                }
                switch (NetUtils.getConnectedType(MainActivity.this)) {
                    case 0: //移动
                        final SharedPreferences share = MainActivity.this.getSharedPreferences(
                                "Data", MainActivity.this.MODE_PRIVATE);
                        if (!share.getBoolean("flist", false)) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("提示");
                            dialog.setMessage("目前手机处于移动网络状态,是否继续播放？");
                            dialog.setCancelable(false); //设置按下返回键不能消失
                            dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences.Editor editor = share.edit();
                                    editor.putBoolean("flist", true);
                                    editor.commit();
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                            dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();//显示弹出窗口
                        } else {
                            startActivity(intent);
                        }
                        break;
                    case 1://wifi
                        startActivity(intent);
                        break;
                    case -1://无网络
                        Snackbar.make(getCurrentFocus(), "你在逗我吧，没网怎么听歌啊！", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        break;
                    default:
                        break;
                }
            }
        });
        recyclerView.setRefreshListener(this);  //下拉刷新
        adapter.addAll(list);
        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterData(s.toString());
            }
        });
    }


    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {

        if (TextUtils.isEmpty(filterStr)) {

            adapter.addAll(list);


        } else {
            context = filterStr;
            page = 1;
            initData();

        }


    }

    @Override
    public void onLoadMore() {
        page++;
        initData();
    }

    @Override
    public void onRefresh() {
        page = 1;
        initData();

    }


    private void initData() {
        String path = "http://api.itwusun.com/music/search/" + sou + "/" + page + "?format=json&sign=a5cc0a8797539d3a1a4f7aeca5b695b9&keyword=" + context;
        try {
            OkHttpUtils
                    .get()
                    .url(path)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (page == 1) {
                                adapter.clear();


                            } else {

                                adapter.stopMore();
                            }
                        }

                        @Override
                        public void onResponse(String string, int id) {
                            try {
                                if (page == 1) {//暂无数据
                                    adapter.clear();


                                }

                                Type type = new TypeToken<List<InforBean>>() {
                                }.getType();
                                Gson gson = new Gson();
                                list = gson.fromJson(string, type);
                                adapter.addAll(list);
                            } catch (Exception e) {
                                adapter.stopMore();
                                e.printStackTrace();

                            }


                        }

                    });

        } catch (Exception e) {
            adapter.stopMore();
            e.printStackTrace();

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_mode:
//                //切换日夜间模式
//                mNightModeHelper.toggle();
//                return true;

            case R.id.action_settings:
                //设置
                startActivity(new Intent(MainActivity.this, MoreActivity.class));
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void MusicSouSuo(String title) {

        if (title.equals("网易")) {
            sou = "wy";
        } else if (title.equals("qq")) {
            //kkkfdk
            sou = "qq";
        } else if (title.equals("虾米")) {
            sou = "xm";
        } else if (title.equals("百度")) {
            sou = "bd";
        } else if (title.equals("酷狗")) {
            sou = "kg";
        } else if (title.equals("酷我")) {
            sou = "kw";
        } else if (title.equals("5sing")) {
            sou = "fs";
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDismiss(Object o) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(mReceiver);
        SharedPreferences share = MainActivity.this.getSharedPreferences(
                "Data", MainActivity.this.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public void onItemClick(Object o, int position) {
        System.err.println("位置" + position);
        DownloadingManager baseActivity = new DownloadingManager(MainActivity.this,  adapter.getAllData(), index);
            switch (position) {
                case 0: //流畅
                    if (!TextUtils.isEmpty(adapter.getAllData().get(index).getLqUrl())) {
                        SnackerShow();
                        baseActivity.DownloadMusics(adapter.getAllData().get(index).getLqUrl(), null);
                    } else {
                        SnackerShowMessage(R.string.toast_message_lc);
                    }
                    break;
                case 1://标准
                    if (!TextUtils.isEmpty(adapter.getAllData().get(index).getHqUrl())) {
                        SnackerShow();
                        baseActivity.DownloadMusics( adapter.getAllData().get(index).getHqUrl(), null);
                    } else {
                        SnackerShowMessage(R.string.toast_message_bz);
                    }
                    break;
                case 2://HQ音质
                    if (!TextUtils.isEmpty(adapter.getAllData().get(index).getSqUrl())) {
                        SnackerShow();
                        baseActivity.DownloadMusics( adapter.getAllData().get(index).getSqUrl(), null);
                    } else {
                        SnackerShowMessage(R.string.toast_message_hq);
                    }
                    break;
                case 3://无损音质
                    if (!TextUtils.isEmpty(adapter.getAllData().get(index).getFlacUrl())) {
                        SnackerShow();
                        baseActivity.DownloadMusics( adapter.getAllData().get(index).getFlacUrl(), ".flac");
                    } else {
                      SnackerShowMessage(R.string.toast_message_sq);

                    }
                    break;


                default:
                    break;
            }


    }
    private void  SnackerShow(){
        Snackbar.make( getCurrentFocus(),getResources().getString(R.string.snacker_message), Snackbar.LENGTH_LONG)
                .show();
    }
    private void  SnackerShowMessage(int string){
        Snackbar.make( getCurrentFocus(),getResources().getString(string), Snackbar.LENGTH_LONG)
                .show();
    }

    /**
     * 点击两次退出
     */
    // 点击两次退出
    boolean isExit;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true;
                Toast.makeText(getApplicationContext(), "再按一次退出",
                        Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        isExit = false;

                    }

                }, 2000);

                return false;
            }

        }

        return super.onKeyDown(keyCode, event);
    }


//    public class NetReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
//                isConnected = NetUtils.isNetworkConnected(context);
//            System.out.println("网络状态：" + isConnected);
//            System.out.println("wifi状态：" + NetUtils.isWifiConnected(context));//状态1
//            System.out.println("移动网络状态：" + NetUtils.isMobileConnected(context)); //状态0  断开网络为-1
//            System.out.println("网络连接类型：" + NetUtils.getConnectedType(context));
//
//                if (isConnected) {
//                    Toast.makeText(context, "已经连接网络" + NetUtils.getConnectedType(context), Toast.LENGTH_LONG).show();
//
//
//                } else {
//
//
//                }
//
//            }
//        }
//
//    }
}
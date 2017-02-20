package com.li.myactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnDismissListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.li.R;
import com.li.activity.DownloadingManager;
import com.li.activity.MoreActivity;
import com.li.adapter.PersonAdapter;
import com.li.bean.APIService;
import com.li.bean.InforBean;
import com.li.util.Constant;
import com.li.util.LogUtil;
import com.li.util.NetUtils;
import com.li.util.ToastUtil;
import com.li.util.Util;
import com.umeng.analytics.MobclickAgent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;

import me.curzbin.library.BottomDialog;
import me.curzbin.library.Item;
import me.curzbin.library.OnItemClickListener;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IndexActivity extends AppCompatActivity implements RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    private EasyRecyclerView recyclerView;
    private PersonAdapter adapter;
    private List<InforBean> list = new ArrayList<InforBean>();
    private int page = 1;
    private EditText mClearEditText;
    private String context = "薛之谦";
    private String sou = "wy"; //搜索引擎
    private int index; //点击item的位置
    private AlertView mAlertView;
    private ImageView ic_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ic_search = (ImageView) findViewById(R.id.ic_search);
        mClearEditText = (EditText) findViewById(R.id.filter_edit_so);
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
        ic_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context = mClearEditText.getText().toString();
                onRefresh();
                LogUtil.m("内容" + context);

            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new BottomDialog(IndexActivity.this)
                                .title(R.string.share_title)
                                .orientation(BottomDialog.LINEAR)   //HORIZONTAL  VERTICAL   GRID  LINEAR
                                .inflateMenu(R.menu.menu_share)
                                .itemClick(new OnItemClickListener() {
                                    @Override
                                    public void click(Item item) {
                                        MusicSouSuo(item.getTitle());
                                        Toast.makeText(IndexActivity.this, getString(R.string.share_title) + item.getTitle(), Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .show();
                    }
                });
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                final Intent intent = new Intent(IndexActivity.this, MainActivity_Music1.class);
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putString("sou", sou);    //一定要获得 adapter数据 List<InforBean>类型
                bundle.putParcelableArrayList("medias", (ArrayList<? extends Parcelable>) adapter.getAllData());
                intent.putExtras(bundle);

                switch (NetUtils.getConnectedType(IndexActivity.this)) {
                    case 0: //移动
                        final SharedPreferences share = IndexActivity.this.getSharedPreferences(
                                "Data", IndexActivity.this.MODE_PRIVATE);
                        if (!share.getBoolean("flist", false)) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(IndexActivity.this);
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
        initData();
    }


    @Override
    public void onLoadMore() {
        page++;
        list.clear();
        initData();
    }

    @Override
    public void onRefresh() {
        page = 1;
        initData();

    }

    private void initData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.Song_QQ_Path)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        APIService service = retrofit.create(APIService.class);
        Observable<String> observable = service.getCtring(sou,context, page);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.err.println("" + e);
                        if (page == 1) {
                            adapter.clear();
                            list.clear();
                        } else {

                            adapter.stopMore();
                        }
                    }

                    @Override
                    public void onNext(String string) {
                        System.err.println("" + string);
                        if (page == 1) {
                            adapter.clear();
                            list.clear();

                        }
                        Document doc = Jsoup.parse(string);
                        Elements elements = doc.select("div.line1");
                        for (Element element : elements) {
                            InforBean bean = new InforBean();
                            bean.setSongName(element.select("a").text().replace("播放", "").replace("去天天动听","").replace("去网易","").toString().trim());
                            bean.setType(element.select("a").attr("href"));
//                                    bean.setTitle(element.select("b").text());
//                                    element.select("b").text();
                            LogUtil.m("歌曲" + element.select("a").text());
                            LogUtil.m("链接" + element.select("a").attr("href"));

                            list.add(bean);

                        }
                        adapter.addAll(list);
                    }
                });
    }
//private void initData() {
//
//    try {
//        OkHttpUtils
//                .get()
//                .url(Constant.Song_QQ_Path)
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        if (page == 1) {
//                            adapter.clear();
//
//                        } else {
//
//                            adapter.stopMore();
//                        }
//                    }
//
//                    @Override
//                    public void onResponse(String string, int id) {
//                        try {
//                            if (page == 1) {//暂无数据
//                                adapter.clear();
//
//                            }
//
//                            System.err.println("" + string);
//                            adapter.addAll(list);
//                        } catch (Exception e) {
//                            adapter.stopMore();
//                            e.printStackTrace();
//
//                        }
//
//
//                    }
//
//                });
//
//    } catch (Exception e) {
//        adapter.stopMore();
//        e.printStackTrace();
//
//    }
//
//
//}
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
                startActivity(new Intent(IndexActivity.this, MoreActivity.class));
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
            sou = "ttdt";
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
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences share = IndexActivity.this.getSharedPreferences(
                "Data", IndexActivity.this.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        editor.clear();
        editor.commit();
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
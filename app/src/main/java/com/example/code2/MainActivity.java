package com.example.code2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.NetworkErrorException;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.TextLinks;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    //listview组件
    private ListView lvNewsList;
    //存储new信息
    private List<News> newsData;
    //适配器
    private NewsAdapter adapter;
    //
    private int[] mCols = new int[]{Constants.NEWS_COL5 , Constants.NEWS_COL7 , Constants.NEWS_COL8 , Constants.NEWS_COL10 , Constants.NEWS_COL11};
    private int mCurrentColIndex = 0;

    @SerializedName("网络请求成功或失败后做的事情")
    private okhttp3.Callback callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.e("flag", "Failed to connect server!");
            e.printStackTrace();
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            if(response.isSuccessful()){
                Log.d("flag","response success");
                //拿到返回的响应体
                final String body = response.body().string();
                Log.d("flag",body);
                //将 HTTP 响应体解析并获得新闻列表数据的每⼀条新闻信息
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //json解析
                            JSONObject jsonObject1 = new JSONObject(body);
                            Log.d("flag","code is "+jsonObject1.getString("code"));
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("result");
                            Log.d("flag","allnum is "+jsonObject2.getString("allnum"));
                            JSONArray jsonArray = jsonObject2.getJSONArray("newslist");
                            for(int i = 0;i < jsonArray.length(); i++){
                                JSONObject jsonObject3 = (JSONObject) jsonArray.getJSONObject(i);
                                News tp = new News();
                                tp.setmId(jsonObject3.getString("id"));
                                tp.setmPublishTime(jsonObject3.getString("ctime"));
                                Log.d("flag","title is "+jsonObject3.getString("title"));
                                tp.setDescription(jsonObject3.getString("description"));
                                tp.setmSource(jsonObject3.getString("source"));
                                tp.setmPicUrl(jsonObject3.getString("picUrl"));
                                tp.setmContentUrl(jsonObject3.getString("url"));
                                adapter.add(tp);
                                tp.setmTitle(jsonObject3.getString("title"));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }else{
                Log.e("flag", "Failed to connect server!");
            }
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //初始化listview
        initData();
    }
    private void initData() {
        newsData = new ArrayList<>();
        adapter = new NewsAdapter(MainActivity.this ,R.layout.list_item , newsData);
        //设置listview适配器
        lvNewsList.setAdapter(adapter);
        //进行网络请求
        refreshData(1);
    }
    private void initView(){
        //获取listview
        lvNewsList = findViewById(R.id.list);
        //设置item的点击事件
        lvNewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                if(adapter != null){
                    News news = adapter.getItem(i);
                    intent.putExtra(Constants.NEWS_DETAIL_URL_KEY,news.getmContentUrl());
                    startActivity(intent);
                }
            }
        });
    }
    private void refreshData(final int page){
        //网络请求不用主线程
        Log.d("flag","request start");
        new Thread(new Runnable() {
            @Override
            public void run() {
                NewsRequest newsRequest = new NewsRequest();
                newsRequest.setCol(mCols[mCurrentColIndex]);
                newsRequest.setNum(Constants.NEWS_NUM);
                newsRequest.setPage(page);
                String urlParams = newsRequest.toString();
                //拼接http网址
                Request request = new Request.Builder().url(Constants.GENERAL_NEWS_URL + urlParams).get().build();
                try{
                    OkHttpClient client = new OkHttpClient();
                    client.newCall(request).enqueue(callback);
                    Log.d("flag","request success");
                }catch (NetworkOnMainThreadException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
//适配器
class NewsAdapter extends ArrayAdapter<News> {

    private List<News> mNewData;
    private Context mContext;
    private int resourceId;

    public NewsAdapter(@NonNull Context context, int resource, @NonNull List<News> data) {
        super(context, resource, data);
        this.mContext = context;
        this.mNewData = data;
        this.resourceId = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        News news = getItem(position);
        View view;
        final ViewHolder vh;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            vh = new ViewHolder();
            vh.tvTitle = view.findViewById(R.id.tv_title);
            vh.tvSource = view.findViewById(R.id.tv_subtitle);
            vh.ivImage = view.findViewById(R.id.iv_image);
            vh.ivDelete = view.findViewById(R.id.iv_delete);
            vh.tvPublishTime = view.findViewById(R.id.tv_publish_time);
            view.setTag(vh);
        } else {
            view = convertView;
            vh = (ViewHolder) view.getTag();
        }
        vh.tvTitle.setText(news.getmTitle());
        vh.tvSource.setText(news.getmSource());
        vh.ivDelete.setTag(position);
        vh.tvPublishTime.setText(news.getmPublishTime());
        if(news.getmPicUrl().length() != 0){
            Glide.with(mContext).load(news.getmPicUrl()).into(vh.ivImage);
        }else{
            Glide.with(mContext).load(R.drawable.ic_action_name).into(vh.ivImage);
            Log.d("flag","pic is null");
        }

        return view;
    }


    class ViewHolder {
        TextView tvTitle;
        TextView tvSource;
        ImageView ivImage;
        TextView tvPublishTime;
        ImageView ivDelete;
    }
}
//接口信息类
//url请求格式为：https://apis.tianapi.com/generalnews/index?key=b5e7aa96d6e56ad994add5889ba37f6f&num=10&col=6
final class Constants {
    private Constants() {
    }

    public static final int NEWS_NUM = 15;
    public static String SERVER_URL = "https://apis.tianapi.com/";
    public static String ALL_NEWS_PATH = "allnews/";
    public static String GENERAL_NEWS_PATH = "generalnews/index";

    public static String API_KEY = "b5e7aa96d6e56ad994add5889ba37f6f";

    public static String ALL_NEWS_URL = SERVER_URL + ALL_NEWS_PATH;
    public static String GENERAL_NEWS_URL = SERVER_URL + GENERAL_NEWS_PATH;

    public static int NEWS_COL5 = 5;
    public static int NEWS_COL7 = 7;
    public static int NEWS_COL8 = 8;
    public static int NEWS_COL10 = 10;
    public static int NEWS_COL11 = 11;

    public static String NEWS_DETAIL_URL_KEY = "news_detail_url_key";
}
//新闻信息类
class News{
    News(){}
    public String getmId() {
        return mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getDescription() {
        return description;
    }
    public String getmPicUrl() {
        return mPicUrl;
    }

    public String getmContentUrl() {
        return mContentUrl;
    }

    public String getmPublishTime() {
        return mPublishTime;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setmPicUrl(String mPicUrl) {
        this.mPicUrl = mPicUrl;
    }

    public void setmContentUrl(String mContentUrl) {
        this.mContentUrl = mContentUrl;
    }

    public void setmPublishTime(String mPublishTime) {
        this.mPublishTime = mPublishTime;
    }

    public String getmSource() {
        return mSource;
    }

    public void setmSource(String mSource) {
        this.mSource = mSource;
    }

    @SerializedName("id")
    private String mId;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("description")
    private String description;
    @SerializedName("PicUrl")
    private String mPicUrl;
    @SerializedName("Url")
    private String mContentUrl;
    @SerializedName("ctime")
    private String mPublishTime;

    @SerializedName("source")
    private String mSource;
}

//NewsRequest 新闻请求参数类,属于这个api的参数，不同api有不同的参数
class NewsRequest{
    public int getNum() {
        return num;
    }

    public int getCol() {
        return col;
    }

    public int getPage() {
        return page;
    }

    public int getRand() {
        return rand;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setRand(int rand) {
        this.rand = rand;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @NonNull
    @Override
    //拼接http请求的网址
    public String toString() {
        String retValue;
        retValue = "?" + "key=" + Constants.API_KEY + "&num=" + num + "&col=" + col;
        if(page != -1) {
            retValue +="&page=" + page;
        }
        return retValue;
    }

    private int num;
    private int col;
    private int page = -1;
    private int rand;
    private String keyword;
}

//网络请求成功后返回的json文件,处理响应体里面的键值对
//T对应响应体里面的newslist
class BaseResponse<T>{
    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    @SerializedName("code")
    private int code;//http返回的code
    @SerializedName("msg")
    private String msg;
    public final static int RESPONSE_SUCCESS = 0;
    @SerializedName("newslist")
    private T data;
    public BaseResponse(){}
}
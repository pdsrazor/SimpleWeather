package nuaa.wsm.simpleweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nuaa.wsm.simpleweather.R;
import nuaa.wsm.simpleweather.application.WeatherApplication;
import nuaa.wsm.simpleweather.db.SimpleWeatherDB;
import nuaa.wsm.simpleweather.model.County;
import nuaa.wsm.simpleweather.model.WeatherInfo;
import nuaa.wsm.simpleweather.util.HttpCallbackListener;
import nuaa.wsm.simpleweather.util.HttpUtil;
import nuaa.wsm.simpleweather.util.Utility;

/**
 * Created by Fear on 2016/9/8.
 */
public class WeatherActivity extends Activity implements View.OnClickListener
            , SwipeRefreshLayout.OnRefreshListener, ViewPager.OnPageChangeListener {

    //private LinearLayout weatherInfoLayout;
    /**
     * 用于显示城市名
     */
    private TextView cityNameText;
    /**
     * 用于显示发布时间
     */
   // private TextView publishText;
    /**
     * 用于显示天气描述信息
     */
   // private TextView weatherDespText;
    /**
     * 用于显示气温1
     */
  //  private TextView temp1Text;
    /**
     * 用于显示气温2
     */
   // private TextView temp2Text;
    /**
     * 用于显示当前日期
     */
  //  private TextView currentDateText;
    /**
     * 切换城市按钮
     */
    private Button switchCity;
    /**
     * 更新天气按钮
     */
    private Button refreshWeather;

    //SwipeRefreshLayout weather_swipeRefreshLayout;

    ViewPager mViewPager;

    private int selected_page;

    private View view_added;
    //private DataCollector dataCollector;
    private SimpleWeatherDB simpleWeatherDB;
    private LayoutInflater inflater;
    private SharedPreferences sharedPreferences;
    private List<View> mViewList;
    private List<WeatherInfo> mWeatherList;
    private String countyCode;
    private boolean requestState;
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        // 初始化各控件 变量
        Button switchCity = (Button) findViewById(R.id.switch_city);
        Button settings = (Button) findViewById(R.id.settings);
        cityNameText = (TextView)findViewById(R.id.city_name);
        switchCity.setOnClickListener(WeatherActivity.this);
        settings.setOnClickListener(WeatherActivity.this);
        simpleWeatherDB = SimpleWeatherDB.getInstance(WeatherApplication.getContext());/* 确保context不为空 */
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);

        /* 设置view pager */
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mViewPager.setOnPageChangeListener(WeatherActivity.this);
        inflater = getLayoutInflater();
        mViewList = new ArrayList<View>();/*需要在set adapter前面*/
        mWeatherList = new ArrayList<WeatherInfo>();
        mViewPager.setAdapter(pagerAdapter);

        /* *
         *  根据上个activity传来的county_code决定显示哪个area的天气信息
         */
        countyCode = getIntent().getStringExtra("county_code");
        selected_page = 0;
        if(simpleWeatherDB.loadSelectedArea().size() > 0) {
            for(County tmp : simpleWeatherDB.loadSelectedArea()) {
                if(tmp.getCountyCode().equals(countyCode)) {
                    Log.d("wsm", "tmp.getCountycode:" + tmp.getCountyCode());
                    break;
                }
                selected_page++;
            }
        }
        /* activity创建时先预加载已经选择过的area的天气信息到view */
        List<County> selectedArea = simpleWeatherDB.loadSelectedArea();
        List<WeatherInfo> all_weahterinfo = simpleWeatherDB.loadWeatherInfo();
        if(all_weahterinfo.size() > 0) {
            Log.d("wsm", "size > 0");
            for(WeatherInfo tmp : all_weahterinfo) {
                View view = inflater.inflate(R.layout.swipe_refresh_layout, null);
                mViewList.add(view);
                pagerAdapter.notifyDataSetChanged();
                initSwipeRefreshLayout(view, WeatherActivity.this, tmp);
            }
        }

        queryWeatherCode(countyCode);//优化：不要每次都是去访问网络
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        countyCode = getIntent().getStringExtra("county_code");
        selected_page = 0;
        if(simpleWeatherDB.loadSelectedArea().size() > 0) {
            for(County tmp : simpleWeatherDB.loadSelectedArea()) {
                if(tmp.getCountyCode().equals(countyCode)) {
                    break;
                }
                selected_page++;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        countyCode = getIntent().getStringExtra("county_code");
        from = getIntent().getStringExtra("from");
        if(from.equals("ChooseAreaActivity")) {//新增的area，去查询天气并显示
            Log.d("wsm", " from ChooseAreaActivity");
            //mViewList.clear();
            queryWeatherCode(countyCode);
        } else if(from.equals("SelectedAreaDisplayActivity")){ //点击现有的area跳转，直接显示
            Log.d("wsm", " from SelectedAreaDisplayActivity");
            mViewPager.setCurrentItem(selected_page);
        } else { //上个界面点击返回键退出或则从启动页跳转，工作在oncreate中做，什么也不做

        }
        getIntent().putExtra("from", "");//reset from的值，以便在点击返回键时，形成正确的逻辑。保证返回时page停留在正确的index上。
    }

    void showAndStoreArea(int index) {//所有的数据管理放到一个service里？
        List<County> selectedArea = simpleWeatherDB.loadSelectedArea();
        List<WeatherInfo> all_weahterinfo = simpleWeatherDB.loadWeatherInfo();
        WeatherInfo added_weather = all_weahterinfo.get(all_weahterinfo.size()-1);
        if(mViewList.size() == all_weahterinfo.size()) {
            initSwipeRefreshLayout(mViewList.get(selected_page), WeatherActivity.this, added_weather);
        } else {
            View view = inflater.inflate(R.layout.swipe_refresh_layout, null);
            mViewList.add(view);
            initSwipeRefreshLayout(view, WeatherActivity.this, added_weather);
        }
        //这里需要设置一下标题
        cityNameText.setText(selectedArea.get(index).getCountyName());
        pagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(index);

    }

    private void initSwipeRefreshLayout(View view, WeatherActivity activity, WeatherInfo weatherInfo) {

        LinearLayout weatherInfoLayout = (LinearLayout) view.findViewById(R.id.weather_info_layout);
        TextView publishText = (TextView)view.findViewById(R.id.publish_text);
        TextView weatherDespText = (TextView) view.findViewById(R.id.weather_desp);
        TextView temp1Text = (TextView) view.findViewById(R.id.temp1);
        TextView temp2Text = (TextView) view.findViewById(R.id.temp2);
        TextView currentDateText = (TextView) view.findViewById(R.id.current_date);

        cityNameText.setText(weatherInfo.getAreaName());
        temp1Text.setText(weatherInfo.getTmp1());
        temp2Text.setText(weatherInfo.getTmp2());
        weatherDespText.setText(weatherInfo.getWeather_desp());
        if(weatherInfo.getTmp1() == null)
            publishText.setText("更新失败");
        else
            publishText.setText("今天" + weatherInfo.getPublish_time() + "发布");

        currentDateText.setText(weatherInfo.getCurrent_date());

        weatherInfoLayout.setVisibility(View.VISIBLE);

        ((SwipeRefreshLayout) view).setOnRefreshListener(WeatherActivity.this);

    }

    void setHttpRequestState(boolean state) {
        this.requestState = state;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.switch_city:/* 跳转到area选择列表 */
                Intent intent = new Intent(this, SelectedAreaDisplayActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                //finish();
                break;
            case R.id.settings:/* 跳转到设置界面 */
                Intent intent_settings = new Intent(this, SettingActivity.class);
                startActivity(intent_settings);
                break;
            default:
                break;
        }
    }

    /**
     * 查询县级代号所对应的天气代号。
     */
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" +
                countyCode + ".xml";
        queryFromServer(address, "countyCode", countyCode);
    }

    /**
     * 查询天气代号所对应的天气。
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" +
                weatherCode + ".html";
        queryFromServer(address, "weatherCode", null);
    }

    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
     */
    private void queryFromServer(final String address, final String type, final String countyCode) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        // 从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            simpleWeatherDB.saveWeatherCode(countyCode, weatherCode);
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    // 处理服务器返回的天气信息，将天气信息存储进数据库
                    Utility.handleWeatherResponse(WeatherActivity.this,
                            response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //what to do?
                            setHttpRequestState(true);
                            showAndStoreArea(selected_page);
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // weather_swipeRefreshLayout.setRefreshing(false);
                        //publishText.setText("同步失败");
                        Utility.handleWeahterResponseFail(WeatherActivity.this,
                                simpleWeatherDB.GetNameFromCodeInSelectArea(WeatherActivity.this.countyCode));
                        setHttpRequestState(false);
                        showAndStoreArea(selected_page);
                    }
                });
            }
        });
    }


    @Override
    public void onRefresh() {
        /*
        publishText.setText("同步中...");
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        String weatherCode = prefs.getString("weather_code", "");
        if (!TextUtils.isEmpty(weatherCode)) {
            queryWeatherInfo(weatherCode);
        }*/
        Toast.makeText(WeatherActivity.this, "dasdasd", Toast.LENGTH_SHORT).show();
    }

    PagerAdapter pagerAdapter = new PagerAdapter() {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mViewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            // TODO Auto-generated method stub
            container.removeView(mViewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            container.addView(mViewList.get(position));


            return mViewList.get(position);
        }
    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("wsm", "index=" + position);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("county_code", simpleWeatherDB.loadSelectedArea().get(position).getCountyCode());
        editor.commit();
        Log.d("wsm", "now: " + sharedPreferences.getString("county_code", ""));
        //更改标题栏的城市名称
        cityNameText.setText(simpleWeatherDB.loadSelectedArea().get(position).getCountyName());
        cityNameText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

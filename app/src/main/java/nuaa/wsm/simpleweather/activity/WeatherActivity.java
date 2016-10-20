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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import nuaa.wsm.simpleweather.R;
import nuaa.wsm.simpleweather.model.ViewCollector;
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

   // SwipeRefreshLayout weather_swipeRefreshLayout;

    ViewPager mViewPager;

    private View view_added;
    //private List<View> viewList;//view数组

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        // 初始化各控件
        Button switchCity = (Button) findViewById(R.id.switch_city);
        Button settings = (Button) findViewById(R.id.settings);
        cityNameText = (TextView)findViewById(R.id.city_name);
        switchCity.setOnClickListener(WeatherActivity.this);
        settings.setOnClickListener(WeatherActivity.this);

        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mViewPager.setOnPageChangeListener(WeatherActivity.this);
        LayoutInflater inflater=getLayoutInflater();
        mViewPager.setAdapter(pagerAdapter);


        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            // 有县级代号时就去查询天气
            view_added = inflater.inflate(R.layout.swipe_refresh_layout, null);
            ViewCollector.viewList.add(view_added);
            ViewCollector.cityNameList.add(getIntent().getStringExtra("county_name"));
            cityNameText.setText(getIntent().getStringExtra("county_name"));
            cityNameText.setVisibility(View.VISIBLE);
            pagerAdapter.notifyDataSetChanged();
            //publishText.setText("同步中...");
            //weatherInfoLayout.setVisibility(View.INVISIBLE);
            //cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            // 没有县级代号时就直接显示本地天气
            //showWeather();
            for(View view : ViewCollector.viewList) {
                initSwipeRefreshLayout(view, WeatherActivity.this);
            }

        }


        //swiperefreshlayout
        /*
        weather_swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        weather_swipeRefreshLayout.setOnRefreshListener(this);
        //weather_swipeRefreshLayout.setColorSchemeColors(0xff0000);
        weather_swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        weather_swipeRefreshLayout.setDistanceToTriggerSync(200);
        weather_swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.holo_blue_light);
        */
    }


    private void initSwipeRefreshLayout(View view, WeatherActivity activity) {

        LinearLayout weatherInfoLayout = (LinearLayout) view.findViewById(R.id.weather_info_layout);
        //TextView cityNameText = (TextView) view.findViewById(R.id.city_name);
        TextView publishText = (TextView)view.findViewById(R.id.publish_text);
        TextView weatherDespText = (TextView) view.findViewById(R.id.weather_desp);
        TextView temp1Text = (TextView) view.findViewById(R.id.temp1);
        TextView temp2Text = (TextView) view.findViewById(R.id.temp2);
        TextView currentDateText = (TextView) view.findViewById(R.id.current_date);


        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
       // cityNameText.setText(prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
      //  cityNameText.setVisibility(View.VISIBLE);

        ((SwipeRefreshLayout)view).setOnRefreshListener(WeatherActivity.this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.switch_city:
                Intent intent = new Intent(this, SelectedAreaDisplayActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.settings:
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
        queryFromServer(address, "countyCode");
    }
    /**
     * 查询天气代号所对应的天气。
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" +
                weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
     */
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        // 从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    // 处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this,
                            response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //weather_swipeRefreshLayout.setRefreshing(false);
                            //showWeather();
                            initSwipeRefreshLayout(ViewCollector.viewList.get(ViewCollector.viewList.size()-1), WeatherActivity.this);
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
                    }
                });
            }
        });
    }

    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
     */
    private void showWeather(View view) {
/*
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        cityNameText.setText( prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
*/
        //Intent intent = new Intent(this, AutoUpdateService.class);
        //startService(intent);
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
            return ViewCollector.viewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            // TODO Auto-generated method stub
            container.removeView(ViewCollector.viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            container.addView(ViewCollector.viewList.get(position));


            return ViewCollector.viewList.get(position);
        }
    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        cityNameText.setText(ViewCollector.cityNameList.get(position));
        cityNameText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

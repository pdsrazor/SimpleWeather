package nuaa.wsm.simpleweather.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nuaa.wsm.simpleweather.R;
import nuaa.wsm.simpleweather.application.WeatherApplication;
import nuaa.wsm.simpleweather.db.SimpleWeatherDB;
import nuaa.wsm.simpleweather.model.County;

/**
 * Created by Fear on 2016/10/18.
 */
public class SelectedAreaDisplayActivity extends Activity implements OnClickListener{

    Button add_city;
    Button edit_city;
    ListView mListView;
    TextView tips_textview;
    ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<String>();
    private List<County> selected_area_list;
    SimpleWeatherDB simpleWeatherDB;

    Boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.selected_area_display_layout);

        add_city = (Button)findViewById(R.id.add_city);
        edit_city = (Button)findViewById(R.id.edit_city);
        mListView = (ListView)findViewById(R.id.display_city_listview);
        tips_textview = (TextView)findViewById(R.id.tips);

        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        add_city.setOnClickListener(this);
        edit_city.setOnClickListener(this);

        simpleWeatherDB = SimpleWeatherDB.getInstance(WeatherApplication.getContext());/* 获得sharedpreference */
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);

        /* 设置listview */
        adapter = new ArrayAdapter<String>(SelectedAreaDisplayActivity.this, android.R.layout.simple_list_item_1, dataList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //查询数据库，找到对应的城市code，跳转到天气信息页并显示pagerview中对应的城市天气
                String CountyName = dataList.get(position);
                String CountyCode = simpleWeatherDB.GetCodeFromNameInSelectArea(CountyName);
                Log.d("wsm", "name="+CountyName+ ", code= " + CountyCode);
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(SelectedAreaDisplayActivity.this).edit();/*设置最后选中的城市  */
                editor.putString("county_code", CountyCode);
                editor.commit();
                Intent weatherInfoIntent = new Intent(SelectedAreaDisplayActivity.this, WeatherActivity.class);
                weatherInfoIntent.putExtra("county_name", CountyName);
                weatherInfoIntent.putExtra("county_code", CountyCode);
                weatherInfoIntent.putExtra("from", "SelectedAreaDisplayActivity");
                weatherInfoIntent.putExtra("isBackPressed", "no");
                startActivity(weatherInfoIntent);
                finish();
            }
        });

        //没有添加任何area时的提示？
        tips_textview.setVisibility(View.GONE);

        /* 加载数据库，填充adapter， 通知listview */
        selected_area_list = simpleWeatherDB.loadSelectedArea();
        for(County tmpCounty : selected_area_list) {
            dataList.add(tmpCounty.getCountyName());
        }
        adapter.notifyDataSetChanged();

        /*注册本地广播接收器，用于接收area添加消息*/
        IntentFilter intentFilterForAreaAdded = new IntentFilter();
        intentFilterForAreaAdded.addAction("nuaa.wsm.SimpleWeahter.cityAdded");
        LocalBroadcastManager.getInstance(SelectedAreaDisplayActivity.this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dataList.clear();
                selected_area_list = simpleWeatherDB.loadSelectedArea();
                if(selected_area_list.size() > 0) {
                    Log.d("wsm", "selected_area_list > 0");
                    for(County tmpCounty : selected_area_list) {
                        dataList.add(tmpCounty.getCountyName());
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }, intentFilterForAreaAdded);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if(resultCode ==RESULT_OK) {
                    Log.d("wsm", "onActivityResult");
                    dataList.clear();
                    selected_area_list = simpleWeatherDB.loadSelectedArea();
                    if(selected_area_list.size() > 0) {
                        Log.d("wsm", "selected_area_list > 0");
                        for(County tmpCounty : selected_area_list) {
                            dataList.add(tmpCounty.getCountyName());
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_city:
                Toast.makeText(SelectedAreaDisplayActivity.this, "not implented", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_city:
                Intent intent = new Intent(SelectedAreaDisplayActivity.this, ChooseAreaActivity.class);
                startActivityForResult(intent, 0);
                finish();
                break;
        }
    }
}

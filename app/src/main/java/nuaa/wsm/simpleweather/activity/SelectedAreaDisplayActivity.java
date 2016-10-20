package nuaa.wsm.simpleweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
        simpleWeatherDB = SimpleWeatherDB.getInstance(this);
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);


        if (prefs.getBoolean("area_selected", false)
                && !isFromWeatherActivity) {// 已经选择了城市且不是从WeatherActivity跳转过来，才会直接跳转到
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if(!prefs.getBoolean("area_selected", false)){
           // mListView.setVisibility(View.GONE);
            /*
            AlertDialog.Builder builder = new AlertDialog.Builder(SelectedAreaDisplayActivity.this);
            builder.setTitle("添加城市");
            builder.setMessage("确定添加吗？");
            builder.setCancelable(true);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(SelectedAreaDisplayActivity.this, ChooseAreaActivity.class);
                    startActivityForResult(intent, 0);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
            */
        } else {
            //tips_textview.setVisibility(View.GONE);
        }

        /*
        selected_area_list = simpleWeatherDB.loadSelectedArea();
        if(selected_area_list.size() == 0)
            dataList.add("暂无数据，请添加城市");
        */
        adapter = new ArrayAdapter<String>(SelectedAreaDisplayActivity.this, android.R.layout.simple_list_item_1, dataList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //查询数据库，找到对应的城市code，跳转到天气信息页并显示pagerview中对应的城市天气
            }
        });

        tips_textview.setVisibility(View.GONE);
        selected_area_list = simpleWeatherDB.loadSelectedArea();
        for(County tmpCounty : selected_area_list) {
            dataList.add(tmpCounty.getCountyName());
        }
       adapter.notifyDataSetChanged();


        add_city.setOnClickListener(this);
        edit_city.setOnClickListener(this);

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
                break;
        }
    }
}

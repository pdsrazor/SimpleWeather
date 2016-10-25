package nuaa.wsm.simpleweather.model;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import nuaa.wsm.simpleweather.application.WeatherApplication;
import nuaa.wsm.simpleweather.db.SimpleWeatherDB;

/**
 * Created by Fear on 2016/10/18.
 */
public class DataCollector {

    private static List<View> viewList;
    private static List<String> cityNameList;
    private static List<County> countyList;

    private static DataCollector instance;

    private DataCollector() {
        viewList = new ArrayList<View>();
        cityNameList = new ArrayList<String>();
        countyList = SimpleWeatherDB.getInstance(WeatherApplication.getContext()).loadSelectedArea();

    }

    public synchronized static DataCollector getInstace() {
        if(instance == null) {
            instance = new DataCollector();
        }

        return instance;
    }

    public List<View> getViewList() {
        return this.viewList;
    }

    public List<String> getCityNameList() {
        return this.cityNameList;
    }

    public List<County> getCountyList() {
        return this.countyList;
    }

    public void LoadCountyListFromDB() {

    }
}

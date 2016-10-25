package nuaa.wsm.simpleweather.application;

import android.app.Application;
import android.content.Context;

import nuaa.wsm.simpleweather.model.DataCollector;

/**
 * Created by Fear on 2016/10/20.
 */
public class WeatherApplication extends Application {

    public static DataCollector dataCollector;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        dataCollector = DataCollector.getInstace();
    }

    public static Context getContext() {
        return context;
    }
}

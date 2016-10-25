package nuaa.wsm.simpleweather.util;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nuaa.wsm.simpleweather.application.WeatherApplication;
import nuaa.wsm.simpleweather.db.SimpleWeatherDB;
import nuaa.wsm.simpleweather.model.City;
import nuaa.wsm.simpleweather.model.County;
import nuaa.wsm.simpleweather.model.Province;
import nuaa.wsm.simpleweather.model.WeatherInfo;

/**
 * Created by Fear on 2016/9/6.
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(SimpleWeatherDB
                                                                       coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    // 将解析出来的数据存储到Province表
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }


    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCitiesResponse(SimpleWeatherDB coolWeatherDB,
                                               String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    // 将解析出来的数据存储到City表
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountiesResponse(SimpleWeatherDB coolWeatherDB,
                                                 String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    // 将解析出来的数据存储到County表
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleWeatherCodeResponse(SimpleWeatherDB coolWeatherDB,
                                                 String response, String countyCode) {
        if (!TextUtils.isEmpty(response)) {
            // 从服务器返回的数据中解析出天气代号
            String[] array = response.split("\\|");
            if (array != null && array.length == 2) {
                String weatherCode = array[1];
                //天气代码填充到数据库中
                coolWeatherDB.saveWeatherCode(countyCode, weatherCode);
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的JSON数据，并将解析出的数据存储到本地。
     */
    public static WeatherInfo handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
                    weatherDesp, publishTime);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",
                    Locale.CHINA);
            WeatherInfo weatherInfo1 = new WeatherInfo(cityName, temp1, temp2, weatherDesp, publishTime,sdf.format(new Date()));
            return weatherInfo1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean handleWeahterResponseFail(Context context, String county_name) {
        saveWeatherInfo(context, county_name, null, null, null, null, null);
        return true;
    }

    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
     */
    public static void saveWeatherInfo(Context context, String cityName,
                                       String weatherCode, String temp1, String temp2, String weatherDesp, String
                                               publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",
                Locale.CHINA);
        ContentValues contentValues = new ContentValues();
        //contentValues.put("city_selected", true);
        contentValues.put("area_name", cityName);
        //contentValues.put("weather_code", weatherCode);
        contentValues.put("temp1", temp1);
        contentValues.put("temp2", temp2);
        contentValues.put("weather_desp", weatherDesp);
        contentValues.put("publish_time", publishTime);
        contentValues.put("current_date", sdf.format(new Date()));

        SimpleWeatherDB simpleWeatherDB = SimpleWeatherDB.getInstance(WeatherApplication.getContext());
        if(!simpleWeatherDB.confirm_already_add_by_name(cityName)) {
            simpleWeatherDB.saveWeatherInfo(contentValues);
            Log.d("wsm", "not found in weatherinfo table, save it");
        }
        else {
            simpleWeatherDB.updateWeatherInfo(contentValues, cityName);
            Log.d("wsm", "found in weatherinfo table, update it");
        }
    }


}

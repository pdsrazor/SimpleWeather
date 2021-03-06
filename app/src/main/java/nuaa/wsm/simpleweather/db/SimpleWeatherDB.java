package nuaa.wsm.simpleweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import nuaa.wsm.simpleweather.model.City;
import nuaa.wsm.simpleweather.model.County;
import nuaa.wsm.simpleweather.model.Province;
import nuaa.wsm.simpleweather.model.WeatherInfo;

/**
 * Created by Fear on 2016/9/5.
 */
public class SimpleWeatherDB {

    /**
     * 数据库名
     */
    public static final String DB_NAME = "cool_weather";
    /**
     * 数据库版本
     */
    public static final int VERSION = 1;
    private static SimpleWeatherDB simpleWeatherDB;
    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     */
    private SimpleWeatherDB(Context context) {
        SimpleWeatherOpenHelper dbHelper = new SimpleWeatherOpenHelper(context,
                DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }


    /**
     * 获取SimpleWeatherDB的实例。
     */
    public synchronized static SimpleWeatherDB getInstance(Context context) {
        if(simpleWeatherDB == null) {
            simpleWeatherDB = new SimpleWeatherDB(context);
        }

        return simpleWeatherDB;
    }

    /**
     * 将Province实例存储到数据库。
     */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    /**
     * 从数据库读取全国所有的省份信息。
     */
    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db
                .query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor
                        .getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor
                        .getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将City实例存储到数据库。
     */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("City", null, values);
        }
    }

    /**
     * 从数据库读取某省下所有的城市信息。
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "province_id = ?",
                new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor
                        .getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor
                        .getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }
        return list;
    }


    /**
     * 将County实例存储到数据库。
     */
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());
            db.insert("County", null, values);
        }
    }


    /**
     * 从数据库读取某城市下所有的县信息。
     */
    public List<County> loadCounties(int cityId) {
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("County", null, "city_id = ?",
                new String[] { String.valueOf(cityId) }, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor
                        .getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor
                        .getColumnIndex("county_code")));
                county.setCityId(cityId);
                list.add(county);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将天气代码放入指定的条目
     */
    public boolean saveWeatherCode(String countyCode, String weatherCode) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("weather_code", weatherCode);
        db.update("County", contentValues, "county_code = ?", new String[] {countyCode});
        return true;
    }

    /**
     * 保存天气信息
    * */
    public void saveWeatherInfo(ContentValues contentValues) {
        db.insert("WeatherInfo", null, contentValues);
    }


    public void updateWeatherInfo(ContentValues contentValues, String area_name) {
        db.update("WeatherInfo", contentValues, "area_name = ?", new String[]{area_name});
    }

    public List<WeatherInfo> loadWeatherInfo() {
        List<WeatherInfo> all_weatherinfo = new ArrayList<WeatherInfo>();
        Cursor cursor = db
                .query("WeatherInfo", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                WeatherInfo weatherInfo = new WeatherInfo();
                weatherInfo.setAreaName(cursor.getString(cursor.getColumnIndex("area_name")));
                weatherInfo.setCurrent_date(cursor.getString(cursor.getColumnIndex("current_date")));
                weatherInfo.setPublish_time(cursor.getString(cursor.getColumnIndex("publish_time")));
                weatherInfo.setTmp1(cursor.getString(cursor.getColumnIndex("temp1")));
                weatherInfo.setTmp2(cursor.getString(cursor.getColumnIndex("temp2")));
                weatherInfo.setWeather_desp(cursor.getString(cursor.getColumnIndex("weather_desp")));
                all_weatherinfo.add(weatherInfo);
            } while (cursor.moveToNext());
        }

        return all_weatherinfo;
    }


    /**
     * 将选择的area实例存储到数据库。
     */
    public void saveSelectedArea(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            db.insert("SelectedArea", null, values);
        }
    }

    /**
     * 从数据库读取选择的area。
     */
    public List<County> loadSelectedArea() {
        List<County> list = new ArrayList<County>();
        Cursor cursor = db
                .query("SelectedArea", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor
                        .getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor
                        .getColumnIndex("county_code")));
                list.add(county);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
    * 从SelectedArea表中，确认当前选中的area是否已经存在
    * */
    public boolean confirm_already_add(String area_code) {
        Cursor cursor = db
                .query("SelectedArea", null, "county_code=?", new String[]{area_code}, null, null, null);
        if(cursor.moveToFirst()) {
            return  true;
        }

        return false;
    }

    /**
     * 从WeatherINfo表中，确认当前选中的area是否已经存在
     * */
    public boolean confirm_already_add_by_name(String area_name) {
        Cursor cursor = db
                .query("WeatherInfo", null, "area_name=?", new String[]{area_name}, null, null, null);
        if(cursor.moveToFirst()) {
            return  true;
        }

        return false;
    }


    /**
     * 根据area name查找到area code
     * */
    public String GetCodeFromNameInSelectArea(String name) {
        Cursor cursor = db
                .query("SelectedArea", null, "county_name=?", new String[]{name}, null, null, null);
        if(cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("county_code"));
        }
        return null;
    }

    /**
     * 根据area code查找到area name
     * */
    public String GetNameFromCodeInSelectArea(String code) {
        Cursor cursor = db
                .query("SelectedArea", null, "county_code=?", new String[]{code}, null, null, null);
        if(cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("county_name"));
        }
        return null;
    }
}

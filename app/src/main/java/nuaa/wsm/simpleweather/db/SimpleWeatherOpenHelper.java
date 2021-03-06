package nuaa.wsm.simpleweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Fear on 2016/9/5.
 */
public class SimpleWeatherOpenHelper extends SQLiteOpenHelper{

    public static final String CREATE_PROVINCE = "create table Province ("
                                        + "id integer primary key autoincrement, "
                                        + "province_name text, "
                                        + "province_code text)";

    public static final String CREATE_CITY = "create table City ("
            + "id integer primary key autoincrement, "
            + "city_name text, "
            + "city_code text, "
            + "province_id integer)";

    public static final String CREATE_COUNTY = "create table County ("
            + "id integer primary key autoincrement, "
            + "county_name text, "
            + "county_code text, "
            + "weather_code text,"
            + "city_id integer)";

    public static final String CREATE_SLECTED_AREA = "create table SelectedArea ("
            + "id integer primary key autoincrement, "
            + "county_name text, "
            + "county_code text, "
            + "weather_code text,"
            + "city_id integer)";

    public static final String CREATE_WEATHER_INFO = "create table WeatherInfo ("
            + "id integer primary key autoincrement, "
            + "area_name text, "
            + "temp1 text,"
            + "temp2 text,"
            + "weather_desp text,"
            + "publish_time text,"
            + "area_code text,"
            + "current_date text)";

    public SimpleWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PROVINCE);
        sqLiteDatabase.execSQL(CREATE_COUNTY);
        sqLiteDatabase.execSQL(CREATE_CITY);
        sqLiteDatabase.execSQL(CREATE_SLECTED_AREA);
        sqLiteDatabase.execSQL(CREATE_WEATHER_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

package nuaa.wsm.simpleweather.model;

/**
 * Created by Fear on 2016/10/24.
 */
public class WeatherInfo {
    private String AreaName;
    private String tmp1;
    private String tmp2;
    private String weather_desp;
    private String publish_time;
    private String current_date;
    private String area_code;

    public WeatherInfo() {
        this.AreaName = null;
        this.tmp1 = null;
        this.tmp2 = null;
        this.weather_desp = null;
        this.publish_time = null;
        this.current_date = null;
    }

    public WeatherInfo(String AreaName, String tmp1, String tmp2
            , String weather_desp, String publish_time, String current_date) {
        this.AreaName = AreaName;
        this.tmp1 = tmp1;
        this.tmp2 = tmp2;
        this.weather_desp = weather_desp;
        this.publish_time = publish_time;
        this.current_date = current_date;
    }

    public String getCurrent_date() {
        return current_date;
    }

    public String getTmp1() {
        return tmp1;
    }

    public String getTmp2() {
        return tmp2;
    }

    public String getWeather_desp() {
        return weather_desp;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public String getAreaName() {
        return AreaName;
    }

    public void setAreaName(String areaName) {
        AreaName = areaName;
    }

    public void setTmp1(String tmp1) {
        this.tmp1 = tmp1;
    }

    public void setTmp2(String tmp2) {
        this.tmp2 = tmp2;
    }

    public void setWeather_desp(String weather_desp) {
        this.weather_desp = weather_desp;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public void setCurrent_date(String current_date) {
        this.current_date = current_date;
    }

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }
}

package nuaa.wsm.simpleweather.model;

/**
 * Created by Fear on 2016/9/5.
 */
public class County {

    private int id;
    private String countyName;
    private String countyCode;
    private int cityId;
    private String weatherCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public void setWeatherCode(String weatherCode) {
        this.weatherCode = weatherCode;
    }


    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getWeatherCode() {
        return this.weatherCode;
    }
}

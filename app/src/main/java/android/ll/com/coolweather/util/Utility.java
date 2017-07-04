package android.ll.com.coolweather.util;

import android.ll.com.coolweather.db.City;
import android.ll.com.coolweather.db.County;
import android.ll.com.coolweather.db.Province;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 010256931 on 2017/7/4.
 */
public class Utility {
    /**
    * 解析返回的“省”信息并存入数据库
    * @author Chang Le
    * created at 2017/7/4 11:11
    */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObj =  allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setId(provinceObj.getInt("id"));
                    province.setProvinceName(provinceObj.getString("name"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析返回的“市”信息并存入数据库
     *
     * @author Chang Le
     * created at 2017/7/4 11:12
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray cities = new JSONArray(response);
                for (int i = 0; i < cities.length(); i++) {
                    JSONObject cityObj = cities.getJSONObject(i);
                    City city = new City();
                    city.setId(cityObj.getInt("id"));
                    city.setCityName(cityObj.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析“乡”数据并存入数据库
     *
     * @author Chang Le
     * created at 2017/7/4 11:18
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray counties = new JSONArray(response);
                for (int i = 0; i < counties.length(); i++) {
                    JSONObject countyObj = counties.getJSONObject(i);
                    County county = new County();
                    county.setId(countyObj.getInt("id"));
                    county.setCountyName(countyObj.getString("name"));
                    county.setWeatherId(countyObj.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}

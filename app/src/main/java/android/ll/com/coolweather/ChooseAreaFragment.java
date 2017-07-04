package android.ll.com.coolweather;


import android.app.ProgressDialog;
import android.ll.com.coolweather.db.City;
import android.ll.com.coolweather.db.County;
import android.ll.com.coolweather.db.Province;
import android.ll.com.coolweather.util.HttpUtil;
import android.ll.com.coolweather.util.Utility;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseAreaFragment extends Fragment {


    //UI Component
    private TextView title_text;
    private Button back_btn;
    private ListView list_view;
    //data
    private List<String> list_data = new ArrayList<>();
    private List<Province> list_pro;
    private List<City> list_city;
    private List<County> list_county;
    private ArrayAdapter<String> list_adapter;
    //flag
    public static final int CHOOSE_PROVINCE = 1;
    public static final int CHOOSE_CITY = 2;
    public static final int CHOOSE_COUNTY = 3;
    //current
    private int current_choose;
    private Province cur_province;
    private City cur_city;
    private County cur_county;
    private ProgressDialog progressDialog;


    public ChooseAreaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_area, container, false);
        title_text = (TextView) view.findViewById(R.id.title_text);
        back_btn = (Button) view.findViewById(R.id.back_btn);
        list_view = (ListView) view.findViewById(R.id.list_item);
        list_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list_data);
        list_view.setAdapter(list_adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryProvinces();
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (current_choose) {
                    case CHOOSE_PROVINCE :
                        cur_province = list_pro.get(position);
                        queryCities();
                        break;
                    case CHOOSE_CITY:
                        cur_city = list_city.get(position);
                        queryCounties();
                        break;
                    case CHOOSE_COUNTY:
                        // TODO: 2017/7/4 显示天气
                        cur_county = list_county.get(position);
                        break;
                    default:
                        break;
                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_choose == CHOOSE_CITY) {
                    queryProvinces();
                } else if (current_choose == CHOOSE_COUNTY) {
                    queryCities();
                }
            }
        });
    }

    private void queryCounties() {
        // TODO: 2017/7/4
    }

    private void queryCities() {
        title_text.setText(cur_province.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", String.valueOf(cur_province.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    private void queryProvinces() {
        // TODO: 2017/7/4 更新数据 设置当前标志位
        title_text.setText("中国");
        back_btn.setVisibility(View.GONE);
        list_pro = DataSupport.findAll(Province.class);
        if (list_pro.size() > 0) {
            if (list_data.size() > 0) {
                list_data.clear();
            }
            for (Province province :
                    list_pro) {
                list_data.add(province.getProvinceName());
            }
            list_adapter.notifyDataSetChanged();
            list_view.setSelection(0);
            current_choose = CHOOSE_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequst(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }
/**
* getActivity().runOnUiThread();
* @author Chang Le
* created at 2017/7/4 15:09
*/
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resultText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(resultText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(resultText, cur_province.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(resultText, cur_city.getId());
                }

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            }else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }

                        }
                    });
                }
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}

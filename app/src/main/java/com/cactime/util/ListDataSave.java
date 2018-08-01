package com.cactime.util;

import static android.content.Context.MODE_PRIVATE;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.cactime.api.PastData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by andy on 2018/3/1.
 */

public class ListDataSave {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public ListDataSave(Context mContext, String preferenceName) {
        preferences = mContext.getSharedPreferences(preferenceName, MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * 保存List
     * @param tag
     * @param datalist
     */
    public <PastData> void setDataList(String tag, List<PastData> datalist) {
        if (null == datalist){
            return;
        }
        if (datalist.size() == 0){
            editor.remove(tag);
            editor.commit();
        }
        else{
            Gson gson = new Gson();
            //转换成json数据，再保存
            String strJson = gson.toJson(datalist);
            editor.clear();
            editor.putString(tag, strJson);
            editor.commit();
        }
    }

    /**
     * 获取List
     * @param tag
     * @return
     */
    public List<PastData> getDataList(String tag) {
        List<PastData> datalist=new ArrayList<PastData>();
        String strJson = preferences.getString(tag, null);
        if (strJson != null)
        {
            Gson gson = new Gson();
            Type type = new TypeToken<List<PastData>>(){}.getType();
            datalist = gson.fromJson(strJson, type);
        }
        return datalist;

    }
}
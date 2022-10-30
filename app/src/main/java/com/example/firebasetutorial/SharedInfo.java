package com.example.firebasetutorial;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedInfo
{
    SharedPreferences sharedPreferences;
    public SharedInfo(Context context)
    {
        sharedPreferences=context.getSharedPreferences(
                "sharedInformation",Context.MODE_PRIVATE);
    }
    public void saveInfo(String idImgProfile)
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("idImgProfile",idImgProfile);
        editor.apply();
    }
    public String getIdImgProfile()
    {
        return sharedPreferences.getString("idImgProfile","default");
    }
}

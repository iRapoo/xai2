package xyz.quenix.xai2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.util.HashMap;
import java.util.Map;

import xyz.quenix.xai2.MyLibs.*;

public class ControllerActivity extends Activity {

    public boolean isFirstStart;
    public StringRequest DeviceRequest;
    public StringRequest GroupRequest;
    public StringRequest TeachRequest;
    public RequestQueue queue;
    public String UID = "";
    public String VERSION = "";

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        final String SERIAL = Build.SERIAL;
        final String BRAND = Build.BRAND;
        final String MANUFACTURER = Build.MANUFACTURER;
        final String PRODUCT = Build.PRODUCT;
        UID = SERIAL+BRAND+MANUFACTURER+PRODUCT;

        try {
            VERSION = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        queue = Volley.newRequestQueue(context);

        DeviceRequest = new StringRequest(Request.Method.POST, isInternet.API + "device",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("true"))
                            Storage.saveData(context, "INFO_VERSION", VERSION);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Ошибка Http запроса...", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("UID", SERIAL);
                params.put("BRAND", BRAND);
                params.put("MANUFACTURER", MANUFACTURER);
                params.put("PRODUCT", PRODUCT);
                params.put("VERSION", VERSION);

                return params;
            }
        };

        GroupRequest = new StringRequest(Request.Method.GET, isInternet.API + "list_group",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Storage.saveData(context,"GROUPS_LIST",response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ошибка Http запроса...", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        TeachRequest = new StringRequest(Request.Method.GET, isInternet.API + "list_teach",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Storage.saveData(context,"TEACH_LIST",response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ошибка Http запроса...", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        if(isInternet.active(context))
        {

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    isFirstStart = Storage.emptyData(context, "firstStart");

                    if (!Storage.loadData(context, "INFO_VERSION").equals(VERSION)) {
                        queue.add(DeviceRequest);
                        queue.add(GroupRequest);
                        queue.add(TeachRequest);
                    }

                    if (isFirstStart) {
                        Intent IntroIntent = new Intent(ControllerActivity.this, MyIntro.class);
                        startActivity(IntroIntent);
                        finish();
                    }else
                    {
                        if(Storage.emptyData(context, "NOW_GROUP") && isInternet.TYPE > 1){
                            Intent NoInternetIntent = new Intent(ControllerActivity.this, NoInternetActivity.class);
                            startActivity(NoInternetIntent);
                            finish();
                        }else {
                            Intent SheduleIntent = new Intent(ControllerActivity.this, SheduleActivity.class);
                            startActivity(SheduleIntent);
                            finish();
                        }
                    }
                }
            });
            t.start();

        } else {
            Intent NoInternetIntent = new Intent(ControllerActivity.this, NoInternetActivity.class);
            startActivity(NoInternetIntent);
            finish();
        }

    }

}

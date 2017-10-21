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


import xyz.quenix.xai2.MyLibs.*;

public class ControllerActivity extends Activity {

    public boolean isFirstStart;
    public String UID = "";
    public String VERSION = "";

    Context context = this;

    public RequestQueue queue = Volley.newRequestQueue(context);

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

        if(isInternet.active(context))
        {

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    isFirstStart = Storage.emptyData(context, "firstStart");

                    if (isFirstStart) {
                        Intent IntroIntent = new Intent(ControllerActivity.this, MyIntro.class);
                        startActivity(IntroIntent);
                    }else
                    {

                        if (!Storage.loadData(context, "INFO_VERSION").equals(VERSION) && isInternet.active(context)) {
                            StringRequest GroupRequest = new StringRequest(Request.Method.GET, isInternet.API + "list_group",
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
                            StringRequest TeachRequest = new StringRequest(Request.Method.GET, isInternet.API + "list_teach",
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
                            queue.add(GroupRequest);
                            queue.add(TeachRequest);
                        }

                    }
                }
            });
            t.start();





        } else {
            Intent NoInternetIntent = new Intent(this, NoInternetActivity.class);
            startActivity(NoInternetIntent);
        }

        //finish();

    }

}

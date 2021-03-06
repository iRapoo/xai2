package xyz.quenix.xai2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.florent37.awesomebar.ActionItem;
import com.github.florent37.awesomebar.AwesomeBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarListener;

import xyz.quenix.xai2.MyLibs.*;
import xyz.quenix.xai2.model.*;

public class SheduleActivity extends AppCompatActivity {

    @Bind(R.id.bar)
    AwesomeBar bar;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private HorizontalCalendar horizontalCalendar;
    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLineModel> mDataList = new ArrayList<>();
    private GradientDrawable.Orientation mOrientation;
    private boolean mWithLinePadding;

    public RequestQueue queue;
    public StringRequest SheduleRequest;

    Context context = this;

    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"), Locale.UK);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Date date = calendar.getTime();
        final String now_group = (Storage.emptyData(context, "NOW_GROUP")) ? getResources().getString(R.string.select) : Storage.loadData(context, "NOW_GROUP");

        if(now_group.split(".,").length>1)
            setTheme(R.style.AppThemeTeach);
        else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_shedule);
        ButterKnife.bind(this);

        queue = Volley.newRequestQueue(context);

        final Intent SelectIntent = new Intent(SheduleActivity.this, SelectActivity.class);

        if(Storage.emptyData(context, "NOW_GROUP")){
            mDataList.add(new TimeLineModel(getResources().getString(R.string.ER_CONTENT),
                    getResources().getString(R.string.ER_LABEL), OrderStatus.INACTIVE));

            startActivity(SelectIntent);
        }else{

            final Boolean translate = (Storage.loadData(context,"translate").equals("true")) ? true : false;
            final String[] tmp_s_shedule = Storage.loadData(context,"S_SHEDULE").split(":,");
            String S_SHEDULE = "";

            if(!isInternet.active(context) && Storage.emptyData(context, now_group)){
                mDataList.add(new TimeLineModel(getResources().getString(R.string.ni_text),
                        getResources().getString(R.string.ER_LABEL), OrderStatus.INACTIVE));
            }

            if(isInternet.active(context) && Storage.emptyData(context, now_group)) {

                mDataList = new ArrayList<>();
                mDataList.add(new TimeLineModel(getResources().getString(R.string.loading),
                        getResources().getString(R.string.GET_DATA), OrderStatus.INACTIVE));
                //initView(now_group, DATE.getWeek(date), DATE.getWeekType(date));

                SheduleRequest = new StringRequest(Request.Method.POST, isInternet.API + "schedule",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Storage.saveData(context, now_group, response);

                                mDataList = new ArrayList<>();

                                initView(now_group, DATE.getWeek(date), DATE.getWeekType(date));
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Ошибка Http запроса...", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("group", now_group);
                        params.put("translate", translate + "");

                        return params;
                    }
                };

                queue.add(SheduleRequest);

                for(int i = 0; i < tmp_s_shedule.length; i++){
                    if(tmp_s_shedule[i].equals(now_group)){

                        S_SHEDULE  = (Storage.emptyData(context, "S_SHEDULE")) ?
                                ":," + now_group : Storage.loadData(context, "S_SHEDULE") + ":," + now_group;

                        Storage.saveData(context, "S_SHEDULE", S_SHEDULE);

                    }
                }

                /*Intent SheduleIntent = new Intent(SheduleActivity.this, SheduleActivity.class);
                SheduleIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(SheduleIntent);*/

            }
        }

        /*
         * Awesone bar
         */

        bar.addAction(R.drawable.awsb_ic_edit_animated, now_group);

        bar.setActionItemClickListener(new AwesomeBar.ActionItemClickListener() {
            @Override
            public void onActionItemClicked(int position, ActionItem actionItem) {
                startActivity(SelectIntent);
            }
        });

        //bar.addOverflowItem("overflow 1");
        //bar.addOverflowItem("overflow 2");
        bar.setOverflowActionItemClickListener(new AwesomeBar.OverflowActionItemClickListener() {
            @Override
            public void onOverflowActionItemClicked(int position, String item) {
                Toast.makeText(getBaseContext(), item+" clicked", Toast.LENGTH_LONG).show();
            }

        });

        bar.setOnMenuClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        bar.displayHomeAsUpEnabled(false);

        /*
         * Awesome bar end
         */

        /*
         * Calendar view
         */

        /** end after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        /** start before 1 month from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, 0);

        final Calendar defaultDate = Calendar.getInstance();
        defaultDate.add(Calendar.MONTH, -1);
        defaultDate.add(Calendar.DAY_OF_WEEK, +5);

        horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .startDate(startDate.getTime())
                .endDate(endDate.getTime())
                .datesNumberOnScreen(5)
                .dayNameFormat("EEE")
                .dayNumberFormat("dd")
                .monthFormat("MMM")
                .showDayName(true)
                .showMonthName(true)
                .selectedDateBackground(ContextCompat.getDrawable(this, R.drawable.selector))
                .textColor(Color.WHITE, Color.WHITE)
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Date date, int position) {

                if(!Storage.emptyData(context, now_group)) {
                    mDataList = new ArrayList<>();
                    initView(now_group, DATE.getWeek(date), DATE.getWeekType(date));
                }


                //Toast.makeText(SheduleActivity.this, position + " is selected!", Toast.LENGTH_SHORT).show();
            }

        });

        /*
         * Calendar view end
         */

        /*
         * Timeline view
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);

        initView(now_group, DATE.getWeek(date), DATE.getWeekType(date));

        /*
         * Timeline view end
         */

    }

    private void initView(String now_group, int day, int type) {
        setDataListItems(now_group, day, type);
        mTimeLineAdapter = new TimeLineAdapter(mDataList);
        mRecyclerView.setAdapter(mTimeLineAdapter);
    }

    private void setDataListItems(String now_group, int day, int type){
        if(!Storage.emptyData(context, now_group) && day < 5) {

            String _data;
            OrderStatus _status;
            Boolean flag = false;
            final Date date = calendar.getTime();

            for(int i = 1; i < 5; i++) {

                _data = JSON.getJSON(context, "day" + day, i + "-" + type, now_group);
                _status = (i==DATE.getNowTime() && day == DATE.getWeek(date)) ? OrderStatus.ACTIVE : OrderStatus.COMPLETED;

                if(!_data.equals("0")) {
                    flag = true;
                    mDataList.add(new TimeLineModel(_data.split("//")[0], _data.split("//")[1], _status));
                }else {
                    mDataList.add(new TimeLineModel(getResources().getString(R.string.no_less) + "", "0", _status));
                }

            }

            if(!flag){
                mDataList = new ArrayList<>();
                mDataList.add(new TimeLineModel(getResources().getString(R.string.day_of_rest), "0", OrderStatus.INACTIVE));
            }

        }

        if(day > 4){
            mDataList.add(new TimeLineModel(getResources().getString(R.string.day_of_rest), "0", OrderStatus.INACTIVE));
        }
    }

}

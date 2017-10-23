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

import com.github.florent37.awesomebar.ActionItem;
import com.github.florent37.awesomebar.AwesomeBar;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_shedule);
        ButterKnife.bind(this);

        final Intent SelectIntent = new Intent(SheduleActivity.this, SelectActivity.class);

        if(Storage.emptyData(context, "NOW_GROUP")){
            startActivity(SelectIntent);
        }

        /*
         * Awesone bar
         */

        String now_group = (Storage.emptyData(context, "NOW_GROUP")) ? getResources().getString(R.string.select) : Storage.loadData(context, "NOW_GROUP");

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
                Toast.makeText(SheduleActivity.this, DateFormat.getDateInstance().format(date) + " is selected!", Toast.LENGTH_SHORT).show();
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

        initView();
        /*
         * Timeline view end
         */

    }

    private void initView() {
        setDataListItems();
        mTimeLineAdapter = new TimeLineAdapter(mDataList);
        mRecyclerView.setAdapter(mTimeLineAdapter);
    }

    private void setDataListItems(){
        mDataList.add(new TimeLineModel(getResources().getString(R.string.ER_CONTENT),
                getResources().getString(R.string.ER_LABEL), OrderStatus.INACTIVE));
    }

}

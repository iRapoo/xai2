package xyz.quenix.xai2;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import xyz.quenix.xai2.MyLibs.*;

public class ControllerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        TextView test = (TextView) findViewById(R.id.test);

        if(isInternet.active())
        {
            test.setText("Интернет есть");
        }else test.setText("Нет доступа к интернету");

        test.setText("" + isInternet.active() + isInternet.HOST);

    }
}

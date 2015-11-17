package wyz.android.com.batterydoctor.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import butterknife.Bind;
import butterknife.ButterKnife;
import wyz.android.com.batterydoctor.R;

/**
 * Created by wangyuzhe on 11/15/15.
 */
public class Fragment1 extends Fragment {

    @Bind(R.id.circle_view)
    CircleProgressView mCircleView;
    @Bind(R.id.text_temp)
    TextView textTemp;
    @Bind(R.id.text_vol)
    TextView textVol;
    @Bind(R.id.tech)
    TextView textTech;
    @Bind(R.id.text_title)
    TextView textTitle;
    @Bind(R.id.img_bt_gps)
    ImageView imgBtGps;
    @Bind(R.id.graph)
    GraphView graph;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"20:00", "21:00", "22:00"});
        staticLabelsFormatter.setVerticalLabels(new String[]{"30%", "60%", "100%"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{new DataPoint(0,0),new DataPoint(20,100),new DataPoint(21,95)});
        graph.addSeries(series);

        mCircleView.setUnit("%");
        mCircleView.setUnitColor(mCircleView.getTextColor());
        mCircleView.setShowUnit(true);
        mCircleView.setOnTouchListener(null);
        mCircleView.setBarColor(getResources().getColor(R.color.beginColor), getResources().getColor(R.color.centerColor), getResources().getColor(R.color.endColor));
        mCircleView.setTextMode(TextMode.PERCENT);
        BatteryBroadcastReceiver batteryBroadcastReceiver = new BatteryBroadcastReceiver();
        getActivity().registerReceiver(batteryBroadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public class BatteryBroadcastReceiver extends BroadcastReceiver {

        private int mCurrentPower;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int level = intent.getIntExtra("level", 0);//剩余容量
                int scale = intent.getIntExtra("scale", 100);//电池最大值
                int vol = intent.getIntExtra("voltage", 0);//电压
                int temp = intent.getIntExtra("temperature", 0);//温度
                int status = intent.getIntExtra("status", 0);//状态
                int health = intent.getIntExtra("health", 0);//健康
                String tech = intent.getStringExtra("technology");
                mCurrentPower = level * 100 / scale;
                mCircleView.setValue(mCurrentPower);
                mCircleView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                DecimalFormat df = new DecimalFormat("0.0");
                textTemp.setText(String.valueOf(df.format(temp * 0.1)));
                textVol.setText(String.valueOf(df.format(vol * 0.001)));
                textTech.setText(tech);

                String statusString = "";
                String healthString = "";
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        statusString = "unknown";
                        break;
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        statusString = "charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        statusString = "discharging";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        statusString = "not charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        statusString = "full";
                        break;
                }
                switch (health) {
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        healthString = "unknown";
                        break;
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        healthString = "good";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        healthString = "overheat";
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        healthString = "dead";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        healthString = "voltage";
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                        healthString = "unspecified failure";
                        break;
                }
            }

        }

    }
}

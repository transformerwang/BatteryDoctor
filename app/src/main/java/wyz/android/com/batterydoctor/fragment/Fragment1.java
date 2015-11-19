package wyz.android.com.batterydoctor.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import butterknife.Bind;
import butterknife.ButterKnife;
import wyz.android.com.batterydoctor.R;
import wyz.android.com.batterydoctor.service.GraphService;

/**
 * Created by wangyuzhe on 11/15/15.
 */
public class Fragment1 extends Fragment implements View.OnClickListener{
    @Bind(R.id.text_health)
    TextView textHealth;
    @Bind(R.id.text_charge)
    TextView textCharge;
    @Bind(R.id.img_bt_wifi)
    ImageView imgBtWifi;
    @Bind(R.id.img_bt_bluetooth)
    ImageView imgBtBluetooth;
    @Bind(R.id.img_bt_network)
    ImageView imgBtNetwork;
    private List<DataPoint> mList = new ArrayList<>();
    private List<DataPoint> mListNew = new ArrayList<>();
    LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
    @Bind(R.id.circle_view)
    CircleProgressView mCircleView;
    @Bind(R.id.text_temp)
    TextView textTemp;
    @Bind(R.id.text_vol)
    TextView textVol;
    @Bind(R.id.tech)
    TextView textTech;
    @Bind(R.id.img_bt_gps)
    ImageView imgBtGps;
    @Bind(R.id.graph)
    GraphView graph;
    private BatteryBroadcastReceiver batteryBroadcastReceiver;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().stopService(new Intent(getActivity(), GraphService.class));
        try {
            FileInputStream fis = getActivity().openFileInput("DataList4.txt");
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            while ((line = br.readLine()) != null) {
                double x = Double.valueOf(line.split(",")[0]);
                double y = Double.valueOf(line.split(",")[1]);
                DataPoint dataPoint = new DataPoint(x, y);
                mList.add(dataPoint);
                Log.d("x", String.valueOf(x));
            }
            br.close();
            isr.close();
            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        View view = inflater.inflate(R.layout.fragment1, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        graph.getGridLabelRenderer().setNumHorizontalLabels(13);
        graph.getGridLabelRenderer().setNumVerticalLabels(11);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return super.formatLabel(value, isValueX) + ":00";
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(24);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);

        imgBtWifi.setOnClickListener(this);
        imgBtBluetooth.setOnClickListener(this);
        imgBtGps.setOnClickListener(this);
        imgBtNetwork.setOnClickListener(this);

        mCircleView.setUnit("%");
        mCircleView.setUnitColor(mCircleView.getTextColor());
        mCircleView.setShowUnit(true);
        mCircleView.setOnTouchListener(null);
        mCircleView.setBarColor(getResources().getColor(R.color.beginColor), getResources().getColor(R.color.centerColor), getResources().getColor(R.color.endColor));
        mCircleView.setTextMode(TextMode.PERCENT);


        batteryBroadcastReceiver = new BatteryBroadcastReceiver();
        getActivity().registerReceiver(batteryBroadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(batteryBroadcastReceiver);
        getActivity().startService(new Intent(getActivity(), GraphService.class));

        try {
            FileOutputStream fos = getActivity().openFileOutput("DataList4.txt", getActivity().MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
//            Time time = new Time();
//            bw.write(time.yearDay);
            for (DataPoint dataPoint : mListNew) {
                bw.write(String.valueOf(dataPoint.getX()) + "," + String.valueOf(dataPoint.getY()) + "\t\n");
                Log.d("xd", String.valueOf(dataPoint.getX()));
                Log.d("yd", String.valueOf(dataPoint.getY()));
            }
            bw.close();
            osw.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.img_bt_wifi:
                Toast.makeText(getActivity(),"Wifi",Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_bt_bluetooth:
                Toast.makeText(getActivity(),"Bluetooth",Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_bt_gps:
                Toast.makeText(getActivity(),"Gps",Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_bt_network:
                Toast.makeText(getActivity(),"Net",Toast.LENGTH_SHORT).show();
                break;
        }
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
                String tech = intent.getStringExtra("technology");//电池品牌
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

                setPower(level * 100 / scale);
                setStatus(status);
                setHealth(health);
            }

        }

    }

    public void setPower(int currentPower) {
        Time time = new Time();
        time.setToNow();
        DataPoint dataPoint = new DataPoint((double) time.hour + (double) time.minute / 100, currentPower);
        Log.e("x", String.valueOf(dataPoint.getX()));
        Log.e("y", String.valueOf(dataPoint.getY()));
        Double current = (double) time.hour + (double) time.minute;
        if (mList.size() > 0 && current < mList.get(mList.size() - 1).getX()) {
            getActivity().deleteFile("DataList4.txt");
            Log.e("delete", "deleteTXT");
        }
        mList.add(dataPoint);
        mListNew.add(dataPoint);
        DataPoint[] mArrayPoint = mList.toArray(new DataPoint[mList.size()]);
        series.resetData(mArrayPoint);
        graph.addSeries(series);
    }

    public void setStatus(int status) {
        String statusString = "";
        switch (status) {
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                statusString = "Status: unknown";
                break;
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusString = "Status: charging";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusString = "Status: discharging";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                statusString = "Status: not charging";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                statusString = "Status: full";
                break;
        }
        textCharge.setText(statusString);
    }

    public void setHealth(int health) {
        String healthString = "";
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                healthString = "Unknown";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthString = "Good";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthString = "Overheat";
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthString = "Dead";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthString = "Voltage";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthString = "Unspecified failure";
                break;
        }
        textHealth.setText(healthString);
    }

}

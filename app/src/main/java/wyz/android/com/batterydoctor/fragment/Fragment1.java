package wyz.android.com.batterydoctor.fragment;


import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
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
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import butterknife.Bind;
import butterknife.ButterKnife;
import wyz.android.com.batterydoctor.R;

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
    private List<DataPoint> mList = new ArrayList<>();
    LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
    private BatteryBroadcastReceiver batteryBroadcastReceiver;
    private WifiManager wifiManager = null;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private String statusString;
    private String BatteryStatus;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

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
        mCircleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(new java.util.Date());
        graph.setTitle(date);
        graph.setTitleTextSize(40);
        graph.setTitleColor(R.color.colorAccent);
        graph.getGridLabelRenderer().setNumHorizontalLabels(13);
        graph.getGridLabelRenderer().setNumVerticalLabels(11);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return super.formatLabel(value, isValueX) + ":00";
                } else {
                    return super.formatLabel(value, isValueX) + "%";
                }
            }
        });
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(24);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
        DataPoint[] mArrayPoint = mList.toArray(new DataPoint[mList.size()]);
        series.resetData(mArrayPoint);
        graph.addSeries(series);

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

        wifiManager = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);

        batteryBroadcastReceiver = new BatteryBroadcastReceiver();
        getActivity().registerReceiver(batteryBroadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(batteryBroadcastReceiver);
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.img_bt_wifi:
                int status = wifiManager.getWifiState();
                if (status == 1) {
                    wifiManager.setWifiEnabled(true);
                    imgBtWifi.setImageResource(R.mipmap.wifi);
                    Toast.makeText(getActivity(),"Start Wifi",Toast.LENGTH_SHORT).show();
                }else if(status == 3){
                    wifiManager.setWifiEnabled(false);
                    imgBtWifi.setImageResource(R.mipmap.wifioff);
                    Toast.makeText(getActivity(),"Close Wifi",Toast.LENGTH_SHORT).show();
                }
                // Toast.makeText(getActivity(),"Wifi",Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_bt_bluetooth:
                if (bluetoothAdapter == null) {
                    Toast.makeText(getActivity(),"No bluetooth device found!",Toast.LENGTH_SHORT).show();
                } else if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.disable();
                    imgBtBluetooth.setImageResource(R.mipmap.bluetoothoff);
                    Toast.makeText(getActivity(),"Stop Bluetooth",Toast.LENGTH_SHORT).show();
                } else if (!bluetoothAdapter.isEnabled()){
                    bluetoothAdapter.enable();
                    imgBtBluetooth.setImageResource(R.mipmap.blueetooth);
                    Toast.makeText(getActivity(),"Start Bluetooth",Toast.LENGTH_SHORT).show();
                }
                // Toast.makeText(getActivity(),"Bluetooth",Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_bt_gps:
                initGPS();
                //Toast.makeText(getActivity(),"Gps",Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_bt_network:
                if(!getMobileDataState(getActivity(),null)){
                    setMobileData(getActivity(), true);
                    imgBtNetwork.setImageResource(R.mipmap.net);
                    Toast.makeText(getActivity(),"Start Network",Toast.LENGTH_SHORT).show();
                }else {
                    setMobileData(getActivity(), false);
                    imgBtNetwork.setImageResource(R.mipmap.netoff);
                    Toast.makeText(getActivity(),"Close Network",Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(getActivity(),"Net",Toast.LENGTH_SHORT).show();
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
                int plugged = intent.getIntExtra("plugged", 0);//充电方式
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
                textTemp.setText(df.format(temp * 0.1) + "℃");
                textVol.setText(String.valueOf(df.format(vol * 0.001))+ "V");
                textTech.setText(tech);

                setPower(mCurrentPower);
                setStatus(status);
                setHealth(health);
                charginMethod(plugged);

            }

        }

    }

    public void setPower(int currentPower) {
        Time time = new Time();
        time.setToNow();
        DataPoint dataPoint = new DataPoint((double) time.hour + (double) time.minute / 100, currentPower);
        Log.e("x", String.valueOf(dataPoint.getX()));
        Log.e("y", String.valueOf(dataPoint.getY()));
        mList.add(dataPoint);
        DataPoint[] mArrayPoint = mList.toArray(new DataPoint[mList.size()]);
        series.resetData(mArrayPoint);
        graph.addSeries(series);
    }

    public void setStatus(int status) {
        statusString = "";
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
        textCharge.setText("Status: " + statusString);
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

    //GPS
    private void initGPS() {
        final LocationManager locationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {

            Toast.makeText(getActivity(), "Please turn on GPS",
                    Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("Please turn on GPS");
            dialog.setPositiveButton("Yes",
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                        }
                    });
            dialog.setNeutralButton("Cancel", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        } else {

            // 弹出Toast
            Toast.makeText(getActivity(), "Please turn off GPS",
                    Toast.LENGTH_LONG).show();
//          // 弹出对话框
//          new AlertDialog.Builder(this).setMessage("GPS is ready")
//                  .setPositiveButton("OK", null).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("Please turn off GPS");
            dialog.setPositiveButton("Yes",
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                        }
                    });
            dialog.setNeutralButton("Cancel", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        }

    }
    public static void setMobileData(Context pContext, boolean pBoolean) {

        try {

            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            Class ownerClass = mConnectivityManager.getClass();

            Class[] argsClass = new Class[1];
            argsClass[0] = boolean.class;

            Method method = ownerClass.getMethod("setMobileDataEnabled", argsClass);

            method.invoke(mConnectivityManager, pBoolean);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Setting error: " + e.toString());
        }
    }

    public static boolean getMobileDataState(Context pContext, Object[] arg) {

        try {

            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            Class ownerClass = mConnectivityManager.getClass();

            Class[] argsClass = null;
            if (arg != null) {
                argsClass = new Class[1];
                argsClass[0] = arg.getClass();
            }

            Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);

            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);

            return isOpen;

        } catch (Exception e) {
            // TODO: handle exception

            System.out.println("Error");
            return false;
        }
    }
    @Override
    public void onResume() {
        final LocationManager locationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        super.onResume();
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            imgBtGps.setImageResource(R.mipmap.gpsoff);
        }else{
            imgBtGps.setImageResource(R.mipmap.gps);
        }
    }

    @Override
    public void onStart() {
        final LocationManager locationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        int status = wifiManager.getWifiState();
        super.onStart();

        //check gps
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            imgBtGps.setImageResource(R.mipmap.gpsoff);
        }else{
            imgBtGps.setImageResource(R.mipmap.gps);
        }

        //check wifi
        if (status == 1) {
            imgBtWifi.setImageResource(R.mipmap.wifioff);
        }else if(status == 3){
            imgBtWifi.setImageResource(R.mipmap.wifi);
        }

        //check bluetooth
        if (bluetoothAdapter.isEnabled()) {
            imgBtBluetooth.setImageResource(R.mipmap.blueetooth);
        } else if (!bluetoothAdapter.isEnabled()){
            imgBtBluetooth.setImageResource(R.mipmap.bluetoothoff);
        }

        //check network
        if(!getMobileDataState(getActivity(), null)){
            imgBtNetwork.setImageResource(R.mipmap.netoff);
        }else {
            imgBtNetwork.setImageResource(R.mipmap.net);
        }
    }

    public void charginMethod(int plugged)
    {
        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                BatteryStatus = "AC";
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                BatteryStatus = "USB";
                break;
        }
    }


}

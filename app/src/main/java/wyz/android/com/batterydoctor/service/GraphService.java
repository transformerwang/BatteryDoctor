package wyz.android.com.batterydoctor.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import wyz.android.com.batterydoctor.MainActivity;
import wyz.android.com.batterydoctor.R;
import wyz.android.com.batterydoctor.model.Battery;

/**
 * Created by wangyuzhe on 11/16/15.
 */
public class GraphService extends Service {
    private List<DataPoint> mList = new ArrayList<>();
    private Intent serviceIntent;
    private PendingIntent resultPendingIntent;
    private NotificationManager notificationManager;
    private int level;
    private int scale;

    BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                level = intent.getIntExtra("level", 0);//剩余容量
                scale = intent.getIntExtra("scale", 100);//电池最大值
                int vol = intent.getIntExtra("voltage", 0);//电压
                int temp = intent.getIntExtra("temperature", 0);//温度
                int status = intent.getIntExtra("status", 0);//状态
                int health = intent.getIntExtra("health", 0);//健康
                int plugged = intent.getIntExtra("plugged", 0);//充电方式
                String tech = intent.getStringExtra("technology");//电池品牌
                int mCurrentPower = level * 100 / scale;
                Battery battery = new Battery(mCurrentPower,vol,temp,status,health,plugged,tech);
                Bundle bundle = new Bundle();
                bundle.putSerializable("battery",battery);
                serviceIntent.putExtras(bundle);
                sendBroadcast(serviceIntent);//发送电池信息给activity
                DecimalFormat df = new DecimalFormat("0.0");
                notification(df.format(temp * 0.1), mCurrentPower, setStatus(status), charginMethod(plugged));
                Time time = new Time();
                time.setToNow();
//                if(time.minute % 5 ==0)
//                {
                    DataPoint dataPoint = new DataPoint((double)time.hour + (double)time.minute/100,mCurrentPower);
                    Log.e("xs",String.valueOf(dataPoint.getX()));
                    Log.e("ys", String.valueOf(dataPoint.getY()));
                try {
                    FileOutputStream fos = openFileOutput("DataList4.txt", MODE_APPEND);
                    OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
                    BufferedWriter bw = new BufferedWriter(osw);
//                    for (DataPoint dataPoint : mList) {
                        bw.write(String.valueOf(dataPoint.getX()) + "," + String.valueOf(dataPoint.getY()) + "\t\n");
//                        Log.i("x", String.valueOf(dataPoint.getX()));
//                        Log.i("y",String.valueOf(dataPoint.getY()));
//                    }
                    bw.close();
                    osw.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                    mList.add(dataPoint);

//                }
                Double current = (double) time.hour + (double) time.minute;
                if(mList.size() > 0 && current < mList.get(mList.size()-1).getX())
                {
                    File file = new File(getFilesDir(),"DataList4.txt");
                    if(file.exists()) {
                        file.delete();
                    }
                }

            }
        }
    };


    @Override
    public void onCreate() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryChangedReceiver,intentFilter);
        serviceIntent = new Intent("com.android.wyz.batterydoctor.Receiver");

        Intent intent = new Intent(this, MainActivity.class);
        resultPendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        return new LocalBinder();
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryChangedReceiver);

    }

    public void notification(String temp, int power, String statusCharge, String statusMethod)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Battery Level: " + String.valueOf(power) + "%")
                .setContentText(statusCharge + "(" + statusMethod + ")/" + temp + "℃")
                .setSmallIcon(R.mipmap.gps)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.wifioff))
                .setContentIntent(resultPendingIntent);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        notificationManager.notify(0, notification);
    }

    public String setStatus(int status) {
        String statusString = "";
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
        return statusString;
    }

    public String charginMethod(int plugged)
    {
        String BatteryStatus = "";
        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                BatteryStatus = "AC";
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                BatteryStatus = "USB";
                break;
        }
        return BatteryStatus;
    }
}

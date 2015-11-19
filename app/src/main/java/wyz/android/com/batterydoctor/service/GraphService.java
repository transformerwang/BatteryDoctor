package wyz.android.com.batterydoctor.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyuzhe on 11/16/15.
 */
public class GraphService extends Service {
    private List<DataPoint> mList = new ArrayList<>();

    private int level;
    private int scale;
    BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                level = intent.getIntExtra("level", 0);//剩余容量
                scale = intent.getIntExtra("scale", 100);//电池最大值

                int mCurrentPower = level * 100 / scale;
                Time time = new Time();
                time.setToNow();
                if(time.minute % 10 ==0)
                {
                    DataPoint dataPoint = new DataPoint((double)time.hour + (double)time.minute/100,mCurrentPower);
                    Log.e("xs",String.valueOf(dataPoint.getX()));
                    Log.e("ys",String.valueOf(dataPoint.getY()));
                    mList.add(dataPoint);
                }
                Double current = (double) time.hour + (double) time.minute;
                if(mList.size() > 0 && current < mList.get(mList.size()-1).getX())
                {
                    deleteFile("DataList4.txt");
                    Log.e("delete", "deleteTXT");
                }


//                if(callBackListener!=null) {
//                    callBackListener.callBack(mList);
//                }
            }
        }
    };


    @Override
    public void onCreate() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryChangedReceiver,intentFilter);
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        return new LocalBinder();
        return null;
    }

//    public class LocalBinder extends Binder
//    {
//        public GraphService getService()
//        {
//            return GraphService.this;
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryChangedReceiver);
        try {
            FileOutputStream fos = openFileOutput("DataList4", MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            for (DataPoint dataPoint : mList) {
                bw.write(String.valueOf(dataPoint.getX()) + "," + String.valueOf(dataPoint.getY()) + "\t\n");
                Log.i("x", String.valueOf(dataPoint.getX()));
                Log.i("y",String.valueOf(dataPoint.getY()));
            }
            bw.close();
            osw.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

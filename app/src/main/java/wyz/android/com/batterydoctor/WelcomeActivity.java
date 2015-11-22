package wyz.android.com.batterydoctor;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import su.levenetc.android.textsurface.Text;
import su.levenetc.android.textsurface.TextBuilder;
import su.levenetc.android.textsurface.TextSurface;
import su.levenetc.android.textsurface.animations.Alpha;
import su.levenetc.android.textsurface.animations.Delay;
import su.levenetc.android.textsurface.animations.Sequential;
import su.levenetc.android.textsurface.animations.Slide;
import su.levenetc.android.textsurface.contants.Align;
import su.levenetc.android.textsurface.contants.Side;
import wyz.android.com.batterydoctor.service.GraphService;

/**
 * Created by wangyuzhe on 11/21/15.
 */
public class WelcomeActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if(!getService())//判断service是否在运行，如果没有，新建一个service
        {
            startService(new Intent(this, GraphService.class));
            Log.e("b", "service is running for the first time");
        }
        TextSurface textSurface = (TextSurface)findViewById(R.id.text_surface);
        Text text = TextBuilder.create("BatteryDoctor")
                .setSize(60)
                .setAlpha(0)
                .setColor(Color.BLACK)
                .setPosition(Align.SURFACE_CENTER)
                .build();
        textSurface.play(new Sequential(
                Slide.showFrom(Side.TOP, text, 500),
                Delay.duration(500),
                Alpha.hide(text, 1500)

        ));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    public boolean getService()
    {
        ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfoList = activityManager.getRunningServices(40);
        for(int i=0; i<serviceInfoList.size(); i++)
        {
            if(serviceInfoList.get(i).service.getClassName().equals("wyz.android.com.batterydoctor.service.GraphService"))
            {
                return true;
            }
        }
        return false;
    }
}

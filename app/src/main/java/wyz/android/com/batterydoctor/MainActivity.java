package wyz.android.com.batterydoctor;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wyz.android.com.batterydoctor.adapter.TabFragmentPagerAdapter;
import wyz.android.com.batterydoctor.fragment.Fragment1;
import wyz.android.com.batterydoctor.fragment.Fragment2;
import wyz.android.com.batterydoctor.fragment.Fragment3;

public class MainActivity extends AppCompatActivity {
    private List<Fragment> mList = new ArrayList<>();
    private boolean mBool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File file = new File(getApplicationContext().getFilesDir(),"DataList4.txt");
        if(!file.exists())
        {
            Log.e("a", "Create DataList4.txt！");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mList.add(new Fragment1());
        mList.add(new Fragment2());
        mList.add(new Fragment3());

        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager(),mList,this));
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

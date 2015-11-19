package wyz.android.com.batterydoctor.fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import wyz.android.com.batterydoctor.R;
import wyz.android.com.batterydoctor.adapter.BrowseRunningAppAdapter;
import wyz.android.com.batterydoctor.adapter.RunningAppInfo;

/**
 * Created by wangyuzhe on 11/15/15.
 */
public class Fragment3 extends Fragment {

    private static final String SCHEME = "package";


    private List<RunningAppInfo> mlistAppInfo = null;
    private PackageManager pm;

    @Bind(R.id.listviewApp)
    ListView listview;
    @Bind(R.id.tvInfo)
    TextView tvInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment3,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        mlistAppInfo = new ArrayList<RunningAppInfo>();

        tvInfo.setText("All Running apps");
        mlistAppInfo = queryAllRunningAppInfo();
        // }
        BrowseRunningAppAdapter browseAppAdapter = new BrowseRunningAppAdapter(getActivity(), mlistAppInfo);
        listview.setAdapter(browseAppAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RunningAppInfo res = mlistAppInfo.get(position);
                String pkg = res.getPkgName();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts(SCHEME, pkg, null);
                intent.setData(uri);

                startActivity(intent);


            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mlistAppInfo = queryAllRunningAppInfo();
        BrowseRunningAppAdapter browseAppAdapter = new BrowseRunningAppAdapter(getActivity(), mlistAppInfo);
        listview.setAdapter(browseAppAdapter);
    }

    private List<RunningAppInfo> queryAllRunningAppInfo() {
        pm = getActivity().getPackageManager();
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(pm));// 排序

        // 保存所有正在运行的包名 以及它所在的进程信息
        Map<String, ActivityManager.RunningAppProcessInfo> pgkProcessAppMap = new HashMap<String, ActivityManager.RunningAppProcessInfo>();

        ActivityManager mActivityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager
                .getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {
            int pid = appProcess.pid; // pid
            String processName = appProcess.processName; // 进程名

            String[] pkgNameList = appProcess.pkgList; // 获得运行在该进程里的所有应用程序包

            // 输出所有应用程序的包名
            for (int i = 0; i < pkgNameList.length; i++) {
                String pkgName = pkgNameList[i];
                // 加入至map对象里
                pgkProcessAppMap.put(pkgName, appProcess);
            }
        }
        // 保存所有正在运行的应用程序信息
        List<RunningAppInfo> runningAppInfos = new ArrayList<RunningAppInfo>(); // 保存过滤查到的AppInfo

        for (ApplicationInfo app : listAppcations) {
            // 如果该包名存在 则构造一个RunningAppInfo对象
            if (pgkProcessAppMap.containsKey(app.packageName)) {
                // 获得该packageName的 pid 和 processName
                int pid = pgkProcessAppMap.get(app.packageName).pid;
                String processName = pgkProcessAppMap.get(app.packageName).processName;
                runningAppInfos.add(getAppInfo(app, pid, processName));
            }
        }

        return runningAppInfos;

    }
    // 某一特定经常里所有正在运行的应用程序
    private List<RunningAppInfo> querySpecailPIDRunningAppInfo(Intent intent , int pid) {


        String[] pkgNameList = intent.getStringArrayExtra("EXTRA_PKGNAMELIST");
        String processName = intent.getStringExtra("EXTRA_PROCESS_NAME");

        //update ui
        tvInfo.setText("id"+pid +" Runing apps  :  "+pkgNameList.length);

        pm = getActivity().getPackageManager();

        // 保存所有正在运行的应用程序信息
        List<RunningAppInfo> runningAppInfos = new ArrayList<RunningAppInfo>(); // 保存过滤查到的AppInfo

        for(int i = 0 ; i<pkgNameList.length ;i++){
            //根据包名查询特定的ApplicationInfo对象
            ApplicationInfo appInfo;
            try {
                appInfo = pm.getApplicationInfo(pkgNameList[i], 0);
                runningAppInfos.add(getAppInfo(appInfo, pid, processName));
            }
            catch (PackageManager.NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }  // 0代表没有任何标记;
        }
        return runningAppInfos ;
    }


    // 构造一个RunningAppInfo对象 ，并赋值
    private RunningAppInfo getAppInfo(ApplicationInfo app, int pid, String processName) {
        RunningAppInfo appInfo = new RunningAppInfo();
        appInfo.setAppLabel((String) app.loadLabel(pm));
        appInfo.setAppIcon(app.loadIcon(pm));
        appInfo.setPkgName(app.packageName);

        //appInfo.setPid(pid);
        // appInfo.setProcessName(processName);

        return appInfo;
    }

}



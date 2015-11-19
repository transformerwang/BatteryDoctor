package wyz.android.com.batterydoctor.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import wyz.android.com.batterydoctor.R;
import wyz.android.com.batterydoctor.adapter.MyAdapter;

/**
 * Created by wangyuzhe on 11/15/15.
 */
public class Fragment2 extends Fragment implements AdapterView.OnItemClickListener {

    private static final String SCHEME = "package";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    private List<Map<String, Object>> list = null;
    private ProgressDialog pd;
    private Context mContext;
    private PackageManager mPackageManager;
    private List<ResolveInfo> mAllApps;

    @Bind(R.id.softlist)
    ListView softlist;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment2,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        mContext = getActivity();
        mPackageManager = getActivity().getPackageManager();

        bindMsg();
    }

    /**
     * 检查系统应用程序，添加到应用列表中
     */
    private void bindMsg() {
        //应用过滤条件
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mAllApps = mPackageManager.queryIntentActivities(mainIntent, 0);
        softlist.setAdapter(new MyAdapter(mContext, mAllApps,mPackageManager));
        softlist.setOnItemClickListener(this);

        //按报名排序
        Collections.sort(mAllApps, new ResolveInfo.DisplayNameComparator(mPackageManager));

    }


    /**
     * 单击应用程序后进入系统应用管理界面
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

        ResolveInfo res = mAllApps.get(position);
        String pkg = res.activityInfo.packageName;
        Intent intent = new Intent();

        final int apiLevel = Build.VERSION.SDK_INT;

        if (apiLevel >= 9) {//2.2版本后
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, pkg, null);
            intent.setData(uri);
        } else {//2.2之前
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, pkg);
        }
        startActivity(intent);
    }

}



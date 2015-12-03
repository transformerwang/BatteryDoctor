package wyz.android.com.batterydoctor.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import wyz.android.com.batterydoctor.R;

/**
 * Created by wangyuzhe on 11/19/15.
 */
public class MyAdapter extends BaseAdapter {

    private Context context;
    private List<ResolveInfo> resInfo;
    private ImageView app_icon = null;
    private TextView app_tilte = null, app_des = null;
    private ResolveInfo res;
    private LayoutInflater infater = null;
    private PackageManager mPackageManager;

    public MyAdapter(Context context, List<ResolveInfo> resInfo, PackageManager mPackageManager) {
        this.context = context;
        this.resInfo = resInfo;
        this.mPackageManager = mPackageManager;
        infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        return resInfo.size();
    }

    @Override
    public Object getItem(int arg0) {

        return arg0;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //	View view = null;
        ViewHolder holder = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = infater.inflate(R.layout.soft_row, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            //     view = convertView ;
            holder = (ViewHolder) convertView.getTag();
        }
        //获取应用程序包名，程序名称，程序图标
        res = resInfo.get(position);
        holder.appIcon.setImageDrawable(res.loadIcon(mPackageManager));
        holder.tvAppLabel.setText(res.loadLabel(mPackageManager).toString());
        return convertView;
    }

    //设定界面布局
    class ViewHolder {
        ImageView appIcon;
        TextView tvAppLabel;

        public ViewHolder(View view) {
            this.appIcon = (ImageView) view.findViewById(R.id.img);
            this.tvAppLabel = (TextView) view.findViewById(R.id.name);
        }
    }
}

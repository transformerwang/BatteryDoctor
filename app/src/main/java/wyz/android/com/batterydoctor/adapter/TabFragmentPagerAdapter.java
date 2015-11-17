package wyz.android.com.batterydoctor.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import java.util.ArrayList;
import java.util.List;

import wyz.android.com.batterydoctor.R;

/**
 * Created by wangyuzhe on 11/15/15.
 */
public class TabFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private String[] mTitle = {"Settings","Running","All"};
    private List<Fragment> mList = new ArrayList<>();
    private Context mContext;
    private int[] imageId = {R.drawable.fully2,R.drawable.list,R.drawable.music};

    public TabFragmentPagerAdapter(FragmentManager fm, List<Fragment> list, Context context)
    {
        super(fm);
        mList = list;
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = mContext.getResources().getDrawable(imageId[position]);
        image.setBounds(0,0,image.getIntrinsicWidth(),image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image,ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan,0,1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;

    }
}

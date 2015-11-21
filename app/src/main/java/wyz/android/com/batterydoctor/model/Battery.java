package wyz.android.com.batterydoctor.model;

import java.io.Serializable;

/**
 * Created by wangyuzhe on 11/21/15.
 */
public class Battery implements Serializable {

    private int mCurrent;
    private int mVol;
    private int mTemp;
    private int mStatus;
    private int mHealth;
    private int mPlugged;
    private String mTech;

    public Battery(int mCurrent, int mVol, int mTemp, int mStatus, int mHealth, int mPlugged, String mTech) {
        this.mCurrent = mCurrent;
        this.mVol = mVol;
        this.mTemp = mTemp;
        this.mStatus = mStatus;
        this.mHealth = mHealth;
        this.mPlugged = mPlugged;
        this.mTech = mTech;
    }

    public int getmCurrent() {
        return mCurrent;
    }

    public void setmCurrent(int mCurrent) {
        this.mCurrent = mCurrent;
    }

    public int getmVol() {
        return mVol;
    }

    public void setmVol(int mVol) {
        this.mVol = mVol;
    }

    public int getmTemp() {
        return mTemp;
    }

    public void setmTemp(int mTemp) {
        this.mTemp = mTemp;
    }

    public int getmStatus() {
        return mStatus;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public int getmHealth() {
        return mHealth;
    }

    public void setmHealth(int mHealth) {
        this.mHealth = mHealth;
    }

    public int getmPlugged() {
        return mPlugged;
    }

    public void setmPlugged(int mPlugged) {
        this.mPlugged = mPlugged;
    }

    public String getmTech() {
        return mTech;
    }

    public void setmTech(String mTech) {
        this.mTech = mTech;
    }


}

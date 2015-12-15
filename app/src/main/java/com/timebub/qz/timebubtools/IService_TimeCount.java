package com.timebub.qz.timebubtools;

/**
 * Created by zxqso on 2015/10/27.
 */
public interface IService_TimeCount {
    public void setTimeCount(int hour,int min);
    public void setEnabled(boolean enabled);
    public int getHour();
    public int getMin();
}

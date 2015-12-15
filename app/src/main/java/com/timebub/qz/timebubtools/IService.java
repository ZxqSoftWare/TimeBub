package com.timebub.qz.timebubtools;

/**
 * Created by zxqso on 2015/10/19.
 */
public interface IService {
    public void callTempStopProtect(String packname);
    public void setStudyStatus(boolean flag);
    public void setLockingStatus(boolean lockflag);
    public boolean isLocking();
    public  boolean isStudy();
}

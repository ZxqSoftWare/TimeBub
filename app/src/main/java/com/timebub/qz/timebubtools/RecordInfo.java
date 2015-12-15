package com.timebub.qz.timebubtools;

/**
 * Created by zxqso on 2015/10/30.
 */
public class RecordInfo {
    private String studyDate = "";
    private String studySucceedTimes = "";
    private String studyFailedTimes = "";

    public void setStudyDate(String studyDate) {
        this.studyDate = studyDate;
    }


    public void setStudySucceedTimes(String studySucceedTimes) {
        this.studySucceedTimes = studySucceedTimes;
    }

    public void setStudyFailedTimes(String studyFailedTimes) {
        this.studyFailedTimes = studyFailedTimes;
    }

    public String getStudyDate() {
        return studyDate;
    }


    public String getStudySucceedTimes() {
        return studySucceedTimes;
    }

    public String getStudyFailedTimes() {
        return studyFailedTimes;
    }
}

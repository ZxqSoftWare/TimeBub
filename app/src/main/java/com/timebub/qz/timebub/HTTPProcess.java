package com.timebub.qz.timebub;

import android.content.Context;

import com.timebub.qz.timebubtools.TimeBubSharePreference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxqso on 2015/10/15.
 */
public class HTTPProcess {
    public static HTTPProcess httpProcessTemp = null;
    Context context;
    String userToken = "";
    String userID = "";

    public HTTPProcess(Context context) {
        httpProcessTemp = this;
        this.context = context;
        TimeBubSharePreference sharePreference = new TimeBubSharePreference(context);
        userToken = sharePreference.getData("userToken");
        userID = sharePreference.getData("userID");
    }

    /**
     * HttpPost模块
     *
     * @param aimUrl   目标URL
     * @param postForm 需要传输的数据表单
     * @param isToken  是否要标记Token
     * @return 返回Post的返回值
     */

    String httpPostParams(String aimUrl, List<NameValuePair> postForm, int isToken) {
        String result = "";
        String host = Public_Enum.baseURL + aimUrl;
        HttpPost baseInfoPost = new HttpPost(host);
        HttpClient baseInfoClient = new DefaultHttpClient();
        HttpResponse getResponse;
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        HttpConnectionParams.setSoTimeout(httpParams, 5000);
        ConnectTimeoutException CTE = null;
        Exception e = null;
        try {

            if (isToken == 1) {
                baseInfoPost.addHeader("userId", String.valueOf(userID));
                baseInfoPost.addHeader("userToken", userToken);
            }
            if (postForm != null)
                baseInfoPost.setEntity(new UrlEncodedFormEntity(postForm, HTTP.UTF_8));
            baseInfoPost.setParams(httpParams);
            getResponse = baseInfoClient.execute(baseInfoPost);
            String tem = String.valueOf(getResponse.getStatusLine().getStatusCode());
            if (getResponse.getStatusLine().getStatusCode() == 200) {
                StringBuffer regBuffer = new StringBuffer();
                HttpEntity regEntity = getResponse.getEntity();
                InputStream getRegStream = regEntity.getContent();
                BufferedReader getBuffer = new BufferedReader(new InputStreamReader(getRegStream, HTTP.UTF_8));
                String readTem = "";
                while ((readTem = getBuffer.readLine()) != null) {
                    regBuffer.append(readTem);
                }
                result = regBuffer.toString();
            }
        } catch (ConnectTimeoutException CTEe) {
            CTE = CTEe;
        } catch (Exception CEe) {
            e = CEe;
            CEe.printStackTrace();
        }
        if (CTE == null && e == null)
            return result;
        else {
            return "timeout";
        }
    }


    /**
     * HttpGet模块
     *
     * @param aimUrl  目标URL
     * @param isToken 是否要标记Token
     * @return 返回get的数据
     */
    String httpGetResponse(String aimUrl, int isToken) {
        HttpGet getSource = new HttpGet(Public_Enum.baseURL + aimUrl);
        HttpResponse getViewResponse;
        String result = "";
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        HttpConnectionParams.setSoTimeout(httpParams, 5000);
        ConnectTimeoutException CTE = null;
        Exception e = null;
        try {
            getSource.setParams(httpParams);
            if (isToken == 1) {
                getSource.addHeader("userId", String.valueOf(userID));
                getSource.addHeader("userToken", userToken);
            }
            getViewResponse = new DefaultHttpClient().execute(getSource);
            String tem = String.valueOf(getViewResponse.getStatusLine().getStatusCode());
            if (getViewResponse.getStatusLine().getStatusCode() == 200) {
                StringBuffer gradeBuffer = new StringBuffer();
                HttpEntity getGradeEntity = getViewResponse.getEntity();
                InputStream getGradeStream = getGradeEntity.getContent();
                BufferedReader getGradeReader = new BufferedReader(
                        new InputStreamReader(getGradeStream/* , "GB2312") */));
                String readTemp = "";
                while ((readTemp = getGradeReader.readLine()) != null) {
                    gradeBuffer.append(readTemp);
                }
                result = gradeBuffer.toString();
            }
        } catch (ConnectTimeoutException CTEe) {
            CTE = CTEe;
        } catch (Exception CEe) {
            CEe.printStackTrace();
            e = CEe;
        }
        if (CTE == null && e == null)
            return result;
        else
            return "timeout";

    }


    /**
     * HttpPut模块
     *
     * @param aimUrl  目标URL
     * @param putForm 传输表单
     * @param isToken 是否标记Token
     * @return
     */
    String httpPutParam(String aimUrl, List<NameValuePair> putForm, int isToken) {
        String result = "";
        HttpPut httpPut = new HttpPut(Public_Enum.baseURL + aimUrl);
        HttpClient putClient = new DefaultHttpClient();
        HttpResponse putResponse;
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        HttpConnectionParams.setSoTimeout(httpParams, 5000);
        ConnectTimeoutException CTE = null;
        Exception e = null;
        try {
            httpPut.setParams(httpParams);
            if (isToken == 1) {
                httpPut.addHeader("userId", String.valueOf(userID));
                httpPut.addHeader("userToken", userToken);
            }
            if (putForm != null)
                httpPut.setEntity(new UrlEncodedFormEntity(putForm, HTTP.UTF_8));
            putResponse = putClient.execute(httpPut);
            String tem = String.valueOf(putResponse.getStatusLine().getStatusCode());
            if (putResponse.getStatusLine().getStatusCode() == 200) {
                StringBuffer gradeBuffer = new StringBuffer();
                HttpEntity getGradeEntity = putResponse.getEntity();
                InputStream getGradeStream = getGradeEntity.getContent();
                BufferedReader getGradeReader = new BufferedReader(
                        new InputStreamReader(getGradeStream/* , "GB2312") */));
                String readTemp = "";
                while ((readTemp = getGradeReader.readLine()) != null) {
                    gradeBuffer.append(readTemp);
                }
                result = gradeBuffer.toString();
            }
        } catch (ConnectTimeoutException CTEe) {
            CTE = CTEe;
        } catch (Exception CEe) {
            e = CEe;
            CEe.printStackTrace();
        }
        if (CTE == null && e == null)
            return result;
        else
            return "timeout";
    }

    /**
     * 新用户注册
     *
     * @param userPhoneNum 手机号码
     * @param userPassword 用户密码
     * @param userName     用户名
     * @param userGender   用户性别
     */
    String regNewUser(String userPhoneNum, String userPassword, String userName, String userGender) {
        List<NameValuePair> postForm = new ArrayList<NameValuePair>();
        String result = "";
        postForm.add(new BasicNameValuePair("userTel", userPhoneNum));
        postForm.add(new BasicNameValuePair("userPw", userPassword));
        postForm.add(new BasicNameValuePair("userName", userName));
        postForm.add(new BasicNameValuePair("userGender", userGender));
        result = httpPostParams("/user", postForm, 0);
        return result;
    }


    /**
     * 更新用户信息
     *
     * @param userPhoneNum
     * @param userPassword
     * @param userName
     * @param userGender
     * @return
     */
    String updateUserInfo(String userPhoneNum, String userPassword, String userName, String userGender) {
        List<NameValuePair> putForm = new ArrayList<NameValuePair>();
        String result = "";
//        putForm.add(new BasicNameValuePair("userTel", userPhoneNum));
//        putForm.add(new BasicNameValuePair("userPw", userPassword));
//        putForm.add(new BasicNameValuePair("userName", userName));
//        putForm.add(new BasicNameValuePair("userGender", userGender));
        result = httpPutParam("/user?userTel=" + userPhoneNum + "&userPw=" + userPassword + "&userName=" + userName + "&userGender=" + userGender, null, 1);
        return result;
    }


    /**
     * 用户登录
     *
     * @param userPhoneNum 用户电话号码
     * @param userPassword 用户密码
     * @return 返回登录结果
     */
    String userLogin(String userPhoneNum, String userPassword) {
        List<NameValuePair> postForm = new ArrayList<NameValuePair>();
        String result = "";
        postForm.add(new BasicNameValuePair("userTel", userPhoneNum));
        postForm.add(new BasicNameValuePair("userPw", userPassword));
        result = httpPostParams("/user/auth", postForm, 0);
        return result;
    }


    /**
     * 检查Token有效性
     *
     * @return 返回检查结果，0为无效，1为有效
     */
    String checkToken() {
        List<NameValuePair> postForm = new ArrayList<NameValuePair>();
        String result = "";
        postForm.add(new BasicNameValuePair("userToken", userToken));
        result = httpPostParams("/user/verifyToken", postForm, 1);
        return result;
    }


    /**
     * 用ID查询用户
     *
     * @param userId 用户ID
     * @return 返回该ID的信息
     */
    String getUserInfo(String userId) {
        String result = "";
        result = httpGetResponse("/user/" + userId, 1);
        return result;
    }

    /**
     * 签到
     *
     * @return 签到信息
     */

    String signUp() {
        String result = "";
        result = httpPostParams("/user/sign", null, 1);
        return result;
    }

    String lookPsw(String phoneNum, String Psw){
        String result="";
        List<NameValuePair>postForm=new ArrayList<>();
        postForm.add(new BasicNameValuePair("userTel",phoneNum));
        postForm.add(new BasicNameValuePair("userPw",Psw));
        postForm.add(new BasicNameValuePair("passwdToken","ZGVmYXVsdF9hdmF0YXIucG5nShiJianPao"));
        result=httpPostParams("/user/passwd",postForm,0);
        return  result;
    }


    /**
     * 用户个人信息统计
     *
     * @return
     */
    String getUserEvents() {
        String result = "";
        result = httpGetResponse("/user/total", 1);
        return result;
    }

    /**
     * 获取用户学习数据
     *
     * @return
     */

    String getUserStudyInfo() {
        String result = "";
        result = httpGetResponse("/study/general", 1);
        return result;
    }

    /**
     * 获取用户学习情况
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    String getUserStudyRecord(String fromDate, String toDate) {
        String result = "";
        result = httpGetResponse("/study/record?fromDate=" + fromDate + "&toDate=" + toDate, 1);
        return result;
    }

    /**
     * 用户开始学习
     *
     * @param timeLength
     * @param appNameArray
     * @return
     */
    String upLoadStudyInfo(String timeLength, String appNameArray) {
        String result = "";
        List<NameValuePair> postForm = new ArrayList<NameValuePair>();
        postForm.add(new BasicNameValuePair("planTime", timeLength));
        postForm.add(new BasicNameValuePair("lockApps", appNameArray));
        result = httpPostParams("/study/lock", postForm, 1);
        return result;
    }

    /**
     * 用户解锁
     *
     * @param lockID
     * @param studyStatus
     * @param realTime
     * @param money
     * @return
     */

    public String upLoadUnlockInfo(String lockID, String studyStatus, String realTime, String money) {
        String result = "";
        List<NameValuePair> postForm = new ArrayList<NameValuePair>();
        postForm.add(new BasicNameValuePair("lockStatus", studyStatus));
        if (studyStatus.equals("1")) {
            postForm.add(new BasicNameValuePair("realTime", realTime));
            postForm.add(new BasicNameValuePair("defaultMoney", money));
        }
        httpPutParam("/study/lock/" + lockID, postForm, 1);
        return result;
    }

    /**
     * 用户强制退出app
     *
     * @return
     */
    String shutDownApp() {
        String result = "";
        result = httpPostParams("/study/shutdown", null, 1);
        return result;
    }

    /**
     * 获取用户总积分
     *
     * @return
     */
    String getUserTotalPoint() {
        String result = "";
        result = httpGetResponse("/points/total", 1);
        return result;
    }

    String getSignState(){
        String result=httpGetResponse("/user/sign",1);
        return result;
    }


    /**
     * 获取更新信息
     *
     * @return
     */
    String getAppVersion() {
        String result = "";
        result = httpGetResponse("/app/version", 1);
        return result;
    }


    /**
     * 反馈
     *
     * @param content 要反馈的内容
     * @return
     */
    String sendFeedBack(String content) {
        List<NameValuePair> contentForm = new ArrayList<NameValuePair>();
        String result = "";
        contentForm.add(new BasicNameValuePair("content", content));
        result = httpPostParams("/app/feedback", contentForm, 1);
        return result;
    }
}

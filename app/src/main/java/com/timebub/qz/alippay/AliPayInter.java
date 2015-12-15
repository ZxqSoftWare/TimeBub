package com.timebub.qz.alippay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.alipay.sdk.app.PayTask;
import com.timebub.qz.timebub.HTTPProcess;
import com.timebub.qz.timebub.TimeBubAppLocking;
import com.timebub.qz.timebub.TimeBubStudyFail;
import com.timebub.qz.timebub.TimeBubUnLocking;
import com.timebub.qz.timebubtools.TimeBubSharePreference;
import com.timebub.qz.timebubtools.TimeBubTools;

public class AliPayInter {

    // 商户PID
    public static final String PARTNER = "2088911917764830";
    // 商户收款账号
    public static final String SELLER = "hzhqizhe@163.com";

    String orderName = "";

    String orderDescribe = "";

    String orderMoney = "";
    //    String a="\n" +
//            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6Odxmq4NttE1+0fZQmpDxx7jf6ZViRRwIJgOWgMN5VNHs38WGAEy7a+u3VO5xWtZMU70lN0wQfpV8ww/UIeIvRDEKHrW5FCPRvBAZ+ZQFXQ/XMoUUhbiIrGg6U05YsAOVUbXcJfBo5tM1m9fxHm2m7bIgkPUAUx6gw3P8vceFvwIDAQAB"
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALo53Garg220TX7R" +
            "9lCakPHHuN/plWJFHAgmA5aAw3lU0ezfxYYATLtr67dU7nFa1kxTvSU3TBB+lXzD" +
            "D9Qh4i9EMQoetbkUI9G8EBn5lAVdD9cyhRSFuIisaDpTTliwA5VRtdwl8Gjm0zWb" +
            "1/EebabtsiCQ9QBTHqDDc/y9x4W/AgMBAAECgYBigfbnTHSyVDzmB9SeoJRpgPd3" +
            "Yk0F9X61hL5DH4vHI6oD/f6zOndccOL4UHHs/lr7dxRHmm+fDgavP1OhJ+dHp93b" +
            "NPpxbBCcOfXGcyCHvpkMZ/7JIA7VNzLM4WknJ+qYIj8btbwGD3MqUouJ+9OxjkQx" +
            "t/TKHHUncQlYzyU8qQJBAOpEwrsqdq/Ou5cou7Ph4nysm+9pXtakT81Hl1OJ0AUf" +
            "N6AoxxTABXX8EGt6KYE6dGMVAfOLciLCenlavwGY1W0CQQDLgDhkXcKdB9lyRYpS" +
            "YBC7U3zURa68d6zYezUjPikWwKdqJe2L1WAdPuZZPaVLnjBzsaf3X93Mwz0RwY7n" +
            "AUhbAkEAgiTg6YmVleWLYVZHsI70e76IgmBPR37QtoHnF4mf/rhw9pCwyF5Eql71" +
            "4D9lf10x+zlYdCwrpBPWZkR7iC+XoQJAC+h95EmkSMIb1FcDaByc/gk9pcJKHlc2" +
            "cxiKnHxXtTAi29BKXUNoM23p1tEHqiwgk7SSqqQee4v1N+PGs/fcPwJAAUjHRuiM" +
            "PYue9L9cJPLEZWHXbZJN14byQmTuVitEItIHq7jQjxMJPGZ7ruJcdIci1osR/Q82" +
            "ZKPnYhxTxT5B2A==";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    private static final int CANCEL_STUDY = 3;

    Activity context;

    TimeBubTools tools;
    TimeBubSharePreference sharePreference;

    public AliPayInter(Activity context, String orderName, String orderDescribe, String orderMoney) {
        this.context = context;
        tools = new TimeBubTools(context.getApplicationContext());
        sharePreference = new TimeBubSharePreference(context.getApplicationContext());
        this.orderName = orderName;
        this.orderDescribe = orderDescribe;
        this.orderMoney = orderMoney;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        tools.makeToast("支付成功");
                        TimeBubUnLocking.unLocking.stopAppLockingService();
                        Date startTime;
                        Date endTime;
                        GregorianCalendar date1 = new GregorianCalendar();
                        GregorianCalendar date2 = new GregorianCalendar();
                        ;
                        String start = sharePreference.getData("startDate");
                        String lockID = sharePreference.getData("lockID");
                        startTime = new Date(Long.parseLong(start));
                        date1.setTime(startTime);
                        endTime = new Date(System.currentTimeMillis());
                        date2.setTime(endTime);
                        double between = (date2.getTimeInMillis() - date1.getTimeInMillis()) / 1000 / 60;
                        studyFail(lockID, String.valueOf(between));
                        if(TimeBubAppLocking.appLocking!=null){
                            TimeBubAppLocking.appLocking.finish();
                        }

                        sharePreference.saveData("lastState","normal");
                        Intent intent = new Intent(context, TimeBubStudyFail.class);
                        context.startActivity(intent);
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            tools.makeToast("支付结果确认中");

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            tools.makeToast("支付失败");

                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    tools.makeToast("检查结果为：" + msg.obj);
                    break;
                }
                case CANCEL_STUDY: {
                    String result = msg.getData().getString("result");
                    tools.makeToast("好的咯，你就这么放弃了");
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void pay() {
        if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE)
                || TextUtils.isEmpty(SELLER)) {
            new AlertDialog.Builder(context)
                    .setTitle("警告")
                    .setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface, int i) {
                                    //
                                    tools.makeToast("支付被迫退出");
                                }
                            }).show();
            return;
        }
        // 订单
        String orderInfo = getOrderInfo(orderName, orderDescribe, orderMoney);

        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + getSignType();
        tools.makeToast("正在支付");
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(context);
                // 调用支付接口，获取支付结果

                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     */
    public void check(View v) {
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(context);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();

    }

    public void studyFail(final String appID, final String realTime) {
        Runnable studyFailed = new Runnable() {
            @Override
            public void run() {
                HTTPProcess httpProcess = new HTTPProcess(context.getApplicationContext());
                String result = httpProcess.httpProcessTemp.upLoadUnlockInfo(appID, "2", realTime, orderName);
                Message msg = new Message();
                msg.what = CANCEL_STUDY;
                Bundle bundle = new Bundle();
                bundle.putString("result", result);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        };
        Thread studyFailprocess = new Thread(studyFailed);
        studyFailprocess.start();
    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(context);
        String version = payTask.getVersion();
        tools.makeToast(version);
    }

    /**
     * create the order info. 创建订单信息
     */
    public String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    public String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
                Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    public String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }

}

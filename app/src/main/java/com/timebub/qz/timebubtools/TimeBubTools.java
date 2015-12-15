package com.timebub.qz.timebubtools;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeoutException;

/**
 * Created by zxqso on 2015/10/17.
 */
public class TimeBubTools {
    Context context;
    String toastMsg;

    public TimeBubTools(Context context) {
        this.context = context;
    }

    public void makeToast(String toastMsg) {
        Toast toast = Toast.makeText(context.getApplicationContext(), toastMsg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public File getUpdateFile(String url, String filePath, ProgressDialog progressDialog) {
        try {
            URL url1 = new URL(url);
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            int max = conn.getContentLength();
            progressDialog.setMax(max);
            InputStream is = conn.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            int progress = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                progress += len;
                progressDialog.setProgress(progress);
                Thread.sleep(30);
            }
            fos.flush();
            fos.close();
            is.close();
            return file;
        } catch (ConnectTimeoutException cte) {
            cte.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFileName(String url) {
        return url.substring(url.lastIndexOf("/" + 1, url.length()));
    }

    public String getCurrentVer() {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionName + " verCode=" + String.valueOf(info.versionCode);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void installAPK(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public boolean fileIsExists(File file) {
        try {
//            File f=new File("/storage/sdcard0/Manual/test.pdf");
            File f = file;
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }
}

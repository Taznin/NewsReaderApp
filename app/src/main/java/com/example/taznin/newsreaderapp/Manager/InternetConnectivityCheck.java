package com.example.taznin.newsreaderapp.Manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetConnectivityCheck {
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=conMgr.getActiveNetworkInfo();

        return networkInfo!=null && networkInfo.isConnected();

    }
}

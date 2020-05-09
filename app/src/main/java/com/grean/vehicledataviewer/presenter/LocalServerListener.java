package com.grean.vehicledataviewer.presenter;

/**
 * Created by weifeng on 2020/5/8.
 */

public interface LocalServerListener {
    void onComplete(boolean result);
    void readyToScan();
}

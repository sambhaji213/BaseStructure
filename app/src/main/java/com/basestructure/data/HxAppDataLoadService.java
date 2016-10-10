package com.basestructure.data;

import android.app.IntentService;
import android.content.Intent;

/**
 * @author Sambhaji on 21-April-2016
 */
public class HxAppDataLoadService extends IntentService {
    private static final String TAG = HxAppDataLoadService.class.toString();

    public HxAppDataLoadService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /*TblRefCurrency refCurrency = new TblRefCurrency();
        refCurrency.addToCurrencyTable();*/
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

package com.viveksb007.attendenceclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    Button btnScanAP;
    TextView tvAP;
    WifiManager wifiManager;
    ScanReceiver scanReceiver;
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnScanAP = (Button) findViewById(R.id.button);
        btnScanAP.setOnClickListener(this);
        tvAP = (TextView) findViewById(R.id.tv_scan_result);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        scanReceiver = new ScanReceiver();

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
    }

    @Override
    protected void onResume() {
        registerReceiver(scanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(scanReceiver);
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                break;
        }
    }


    private class ScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "Received");
            List<ScanResult> wifiScanList = wifiManager.getScanResults();
            sb = new StringBuilder();
            sb.append("\n        Number Of Wifi connections :" + wifiScanList.size() + "\n\n");

            for (int i = 0; i < wifiScanList.size(); i++) {
                sb.append(new Integer(i + 1).toString() + ". ");
                sb.append((wifiScanList.get(i)).toString());
                sb.append("\n\n");
            }
            tvAP.setText(sb);
        }
    }
}

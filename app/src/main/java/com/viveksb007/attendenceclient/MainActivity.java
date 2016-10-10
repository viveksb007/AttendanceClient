package com.viveksb007.attendenceclient;

import android.Manifest;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();
    private static final int RC_READ_PHONE_STATE = 101;
    private static final int RC_CHANGE_WIFI_STATE = 102;
    TextView textResponse;
    TextView tv_device_ID;
    Button btnMarkAttendence;
    String DEVICE_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermissions();
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        //MAC_ADDRESS = wifiInfo.getMacAddress();

        tv_device_ID = (TextView) findViewById(R.id.tv_device_id);
        PrefManager manager = new PrefManager(this);
        DEVICE_ID = manager.getDeviceId();
        TextView uname = (TextView) findViewById(R.id.tv_name);
        uname.setText(manager.getUsername());
        TextView rollNo = (TextView) findViewById(R.id.tv_roll_no);
        rollNo.setText(manager.getRollNo());
        tv_device_ID.setText("Device ID : " + DEVICE_ID);
        btnMarkAttendence = (Button) findViewById(R.id.connect);
        textResponse = (TextView) findViewById(R.id.response);
        btnMarkAttendence.setOnClickListener(buttonConnectOnClickListener);
    }

    private void getPermissions() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_PHONE_STATE)) {
            EasyPermissions.requestPermissions(this, "This app needs to Read Device ID for classification", RC_READ_PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
        }
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.CHANGE_WIFI_STATE)) {
            EasyPermissions.requestPermissions(this, "This app needs this permission to function properly", RC_CHANGE_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    View.OnClickListener buttonConnectOnClickListener =
            new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    MyClientTask myClientTask = new MyClientTask();
                    myClientTask.execute();
                }
            };

    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";

        MyClientTask() {
            dstAddress = "192.168.43.1";
            dstPort = 8080;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);

                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                if (DEVICE_ID != null) {
                    dataOutputStream.writeUTF(DEVICE_ID);
                }

                response = dataInputStream.readUTF();

            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {

                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            switch (response) {
                case "404":
                    textResponse.setText(String.format("%s Not Marked", response));
                    break;
                case "200":
                    textResponse.setText(R.string.success_attendance);
                    stopWifi();
                    break;
                case "420":
                    textResponse.setText(R.string.duplicate_message);
                    stopWifi();
                    break;
                default:
                    textResponse.setText(response);
                    break;
            }
            super.onPostExecute(result);
        }

    }

    public WifiManager mWifiManager;

    private void stopWifi() {
        mWifiManager = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }
}

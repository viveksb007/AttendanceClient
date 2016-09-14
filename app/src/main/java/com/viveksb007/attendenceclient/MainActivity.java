package com.viveksb007.attendenceclient;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button btnMarkAttendence;
    String MAC_ADDRESS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        MAC_ADDRESS = wifiInfo.getMacAddress();

        editTextAddress = (EditText) findViewById(R.id.address);
        editTextPort = (EditText) findViewById(R.id.port);
        btnMarkAttendence = (Button) findViewById(R.id.connect);
        textResponse = (TextView) findViewById(R.id.response);

        btnMarkAttendence.setOnClickListener(buttonConnectOnClickListener);
    }

    View.OnClickListener buttonConnectOnClickListener =
            new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    MyClientTask myClientTask = new MyClientTask(
                            editTextAddress.getText().toString(),
                            Integer.parseInt(editTextPort.getText().toString()));
                    myClientTask.execute();
                }
            };

    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";

        MyClientTask(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
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

                if (MAC_ADDRESS != null) {
                    dataOutputStream.writeUTF(MAC_ADDRESS);
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
            if (response == "404") {
                textResponse.setText(response + "Not Marked");
            } else if(response == "200") {
                textResponse.setText("Your attendence is Marked :D");
                stopWifi();
            }else {
                textResponse.setText(response);
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

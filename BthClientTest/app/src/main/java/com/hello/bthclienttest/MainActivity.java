package com.hello.bthclienttest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private static final int ACCESS_LOCATION = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private AtomicBoolean flag = new AtomicBoolean(false);

    //要连接的目标蓝牙设备。
    private final String TARGET_DEVICE_ADDRESS = "F0:18:98:65:D3:44";
    private final String TARGET_DEVICE_NAME = "F0:18:98:65:D3:44";

    private final String TAG = "蓝牙调试";
    private final String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private short rssi = -1;
    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    private final Runnable r = new Runnable() {
        @Override
        public void run() {
            if(mBluetoothAdapter!=null){
                mBluetoothAdapter.startDiscovery();
                flag.set(false);
            }
        }
    };

    // 广播接收发现蓝牙设备。
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0);
                String address = device.getAddress();
                Log.d(TAG, "发现设备:" + device.getName() + " RSSI:" + rssi + " address" + device.getAddress());
                if (address != null && address.equals(TARGET_DEVICE_ADDRESS)) {
                    Log.d(TAG, "发现目标设备，开始线程连接!");
                    try{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Socket socket = new Socket("10.202.33.131", 16666);
                                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                                    short distance = rssi;
                                    Log.d(TAG, "发送的rssi为：" + rssi);
                                    pw.println(Math.abs(distance) + "");
                                    pw.flush();
                                    pw.close();
                                    socket.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // 蓝牙搜索是非常消耗系统资源开销的过程，一旦发现了目标感兴趣的设备，可以考虑关闭扫描。
                    mBluetoothAdapter.cancelDiscovery();
                }
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Toast.makeText(MainActivity.this, "finished", Toast.LENGTH_SHORT).show();
                if(flag.compareAndSet(false, true)){
                    mHandler.postDelayed(r, 2000);
                }
            }
        }
    };

    private class ClientThread extends Thread {
        private BluetoothDevice device;

        public ClientThread(BluetoothDevice device) {
            this.device = device;
        }

        @Override
        public void run() {
            BluetoothSocket socket;

            try {
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));

                Log.d(TAG, "连接服务端...");
                socket.connect();
                Log.d(TAG, "连接建立.");

                // 开始往服务器端发送数据。
                sendDataToServer(socket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sendDataToServer(BluetoothSocket socket) {
            try {
                OutputStream os = socket.getOutputStream();
                os.write(new String("hello,world!").getBytes());
                os.flush();
                os.close();
                socket.close();
                Log.d(TAG, "发送成功");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private BluetoothDevice getPairedDevices() {
        // 获得和当前Android已经配对的蓝牙设备。
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices != null && pairedDevices.size() > 0) {
            // 遍历
            for (BluetoothDevice device : pairedDevices) {
                // 把已经取得配对的蓝牙设备名字和地址打印出来。
                Log.d(TAG, device.getName() + " : " + device.getAddress());
                if (TextUtils.equals(TARGET_DEVICE_NAME, device.getName())) {
                    Log.d(TAG, "已配对目标设备 -> " + TARGET_DEVICE_NAME);
                    return device;
                }
            }
        }

        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPermission();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = getPairedDevices();
        if (device == null) {
            // 注册广播接收器。
            // 接收蓝牙发现讯息。
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mBroadcastReceiver, filter);

            if (mBluetoothAdapter.startDiscovery()) {
                Log.d(TAG, "启动蓝牙扫描设备...");
            }
        } else {
            new ClientThread(device).start();
        }
    }

    private void getPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            int permissionCheck = 0;
            permissionCheck = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck += this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions( // 请求授权
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        ACCESS_LOCATION);// 自定义常量,任意整型
            } else {
                // 已经获得权限
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}


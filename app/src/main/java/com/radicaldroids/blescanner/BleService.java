package com.radicaldroids.blescanner;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BleService extends Service {
    private BluetoothAdapter mBluetoothAdapter;
    public static final String NOTIFICATION = "com.radicaldroids.blescanner";
    List<BluetoothDevice> mList;    //device holder ArrayList

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mList = new ArrayList<>();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        //if bluetooth is turned off or not available, this will ask user for permission to start bluetooth
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
        }

        //there is a slight UI delay in running startLeScan so wrapping it with a thread seems to help
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        }).start();
    }

    //broadcasts the parcelable Device class that contains name, rssi, and record
    private void broadcastUpdate(Device device) {
        final Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("device", device);
        sendBroadcast(intent);
    }

    //callback from the bluetooth adapter
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {

            Log.e("service", "service: " + device + ", rssi: " + rssi);

            if(!mList.contains(device)) {
                mList.add(device);
                Device bleDevice = new Device();
                bleDevice.setName(device);
                bleDevice.setRssi(String.valueOf(rssi));
                bleDevice.setRecord(scanRecord.toString());

                broadcastUpdate(bleDevice);
            }
        }
    };

    //releasing the adapter resource. Must be released or Service runs indefinitely
    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
}

package com.radicaldroids.blescanner;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;
import java.util.List;

public class BleService extends Service {
    private BluetoothAdapter mBluetoothAdapter;
    public static final String NOTIFICATION = "com.radicaldroids.blescanner";
    List<BluetoothDevice> mList;    //ArrayList holds unique devices

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

        //there is a slight UI delay in running startLeScan so wrapping it in a thread seems to help
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

    //callback from the bluetooth adapter, this calls broadcastUpdate if new device is detected
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {

            //mList contains a list of unique devices. Without this if statement, every beacon signal would appear to be a new device
            if(!mList.contains(device)) {
                mList.add(device);

                Device bleDevice = new Device();
                bleDevice.setName(device);
                bleDevice.setRssi(String.valueOf(rssi));
                bleDevice.setRecord(bytesToHexString(scanRecord));

                broadcastUpdate(bleDevice);
            }
        }
    };

    //function for converting scan record to hex
    //from: http://javarevisited.blogspot.com/2013/03/convert-and-print-byte-array-to-hex-string-java-example-tutorial.html
    public static String bytesToHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes){
            sb.append(String.format("%02x", b&0xff));
        }
        return sb.toString();
    }

    //releasing the adapter resource. Must be released or Service runs indefinitely
    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
}

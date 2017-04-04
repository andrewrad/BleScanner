package com.radicaldroids.blescanner;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.scan_button) Button mScanButton;
    @BindView(R.id.scan_data_list) ListView mList;

    private BluetoothAdapter mBluetoothAdapter;
    private DeviceAdapter mDeviceAdapter;
    private int mScanTime = 15000;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Device device = (Device) bundle.get("device");
            mDeviceAdapter.addDevice(device);
            mDeviceAdapter.notifyDataSetChanged();
            Log.e("broadcast", "received: " + bundle);
        }
    };

    public ScanFragment() {
        // Required empty public constructor
    }

    public static ScanFragment newInstance() {
        return new ScanFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        mDeviceAdapter = new DeviceAdapter((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        mList.setAdapter(mDeviceAdapter);

        getActivity().registerReceiver(receiver, new IntentFilter(BleService.NOTIFICATION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        ButterKnife.bind(this, view);
        mScanButton.setOnClickListener(this);

        final BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        //clears the listview
        mDeviceAdapter.initiateList();
        mDeviceAdapter.notifyDataSetChanged();

        //start BleService
        final Intent bleIntent = new Intent(getActivity(), BleService.class);
        getActivity().startService(bleIntent);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().stopService(bleIntent);
                //changes button UI elements to "start scan"
                mScanButton.setBackgroundResource(R.drawable.start_scan_button);
                mScanButton.setText(R.string.start_scan);
                mScanButton.setTextColor(ContextCompat.getColor(getContext(), R.color.scan_button_color));
                mScanButton.setClickable(true);
            }
        }, mScanTime);

        //changes button UI elements to "scanning"
        mScanButton.setBackgroundResource(R.drawable.scanning_button);
        mScanButton.setText(R.string.scanning_button_text);
        mScanButton.setTextColor(ContextCompat.getColor(getContext(), R.color.scanning_button_color));
        mScanButton.setClickable(false);
    }
}

package com.radicaldroids.blescanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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

    private OnFragmentInteractionListener mListener;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private DeviceAdapter mDeviceAdapter;
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

    // Connection to Service
//    private final ServiceConnection mServiceConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
//            if (!mBluetoothLeService.initialize()) {
//                Log.e("ScanFragment", "Unable to initialize Bluetooth");
////                finish();
//            }
//            // Automatically connects to the device upon successful start-up initialization.
////            mBluetoothLeService.connect(mDeviceAddress);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mBluetoothLeService = null;
//        }
//    };

    public ScanFragment() {
        // Required empty public constructor
    }

    public static ScanFragment newInstance() {
        return new ScanFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();

//        final BluetoothManager bluetoothManager =
//                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//
//        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, 1);
//        }
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
//            finish();
//            return;
        }

        return view;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        mDeviceAdapter.initiateList();
        mDeviceAdapter.notifyDataSetChanged();

        final Intent bleIntent = new Intent(getActivity(), BleService.class);
        getActivity().startService(bleIntent);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().stopService(bleIntent);
                mScanButton.setBackgroundResource(R.drawable.start_scan_button);
                mScanButton.setText(R.string.start_scan);
                mScanButton.setTextColor(ContextCompat.getColor(getContext(), R.color.scan_button_text));
                mScanButton.setClickable(true);
            }
        }, 5000);

        mScanButton.setBackgroundResource(R.drawable.scanning_button);
        mScanButton.setText(R.string.scanning_button_text);
        mScanButton.setTextColor(ContextCompat.getColor(getContext(), R.color.scanning_button_text));
        mScanButton.setClickable(false);

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

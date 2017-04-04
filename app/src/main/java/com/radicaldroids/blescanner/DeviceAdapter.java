package com.radicaldroids.blescanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


class DeviceAdapter extends BaseAdapter {

    private ArrayList<Device> mLeDevices;

    LayoutInflater inflater;

    DeviceAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
        initiateList();
    }

    void addDevice(Device bleDevice) {
        if(!mLeDevices.contains(bleDevice)) {
            mLeDevices.add(bleDevice);
        }
    }

    void initiateList() {
        mLeDevices = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return mLeDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = inflater.inflate(R.layout.list_item, parent, false);
        viewHolder = new ViewHolder(view);

        Device device = mLeDevices.get(position);
        viewHolder.name.setText("Name: " + device.getName());
        viewHolder.rssi.setText("RSSI: " + device.getRssi());
        viewHolder.hex.setText("Scan Record: " + device.getRecord());

        return view;
    }

    static class ViewHolder {
        @BindView(R.id.name) TextView name;
        @BindView(R.id.rssi) TextView rssi;
        @BindView(R.id.hex) TextView hex;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

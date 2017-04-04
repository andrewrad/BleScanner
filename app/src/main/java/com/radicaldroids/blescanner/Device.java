package com.radicaldroids.blescanner;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

class Device implements Parcelable {
    private BluetoothDevice name;
    private String rssi;
    private String record;

    private Device(Parcel in) {
        name = in.readParcelable(BluetoothDevice.class.getClassLoader());
        rssi = in.readString();
        record = in.readString();
    }

    public Device() {
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    public BluetoothDevice getName() {
        return name;
    }

    public void setName(BluetoothDevice name) {
        this.name = name;
    }

    String getRssi() {
        return rssi;
    }

    void setRssi(String rssi) {
        this.rssi = rssi;
    }

    String getRecord() {
        return record;
    }

    void setRecord(String record) {
        this.record = record;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(name, flags);
        dest.writeString(rssi);
        dest.writeString(record);
    }
}

package com.radicaldroids.blescanner;

import android.bluetooth.BluetoothDevice;

class Device {
    private BluetoothDevice name;
    private String rssi;
    private String record;

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
}

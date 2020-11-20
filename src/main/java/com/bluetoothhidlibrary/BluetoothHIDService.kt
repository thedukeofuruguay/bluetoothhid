package com.bluetoothhidlibrary

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothProfile
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.bluetoothhidlibrary.reports.MouseReport

class BluetoothHIDService: BluetoothHidDevice.Callback() {

    private var hostDevice: BluetoothDevice? = null

    var hidDevice: BluetoothHidDevice? = null

    // Get the default adapter
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val profileListener = object : BluetoothProfile.ServiceListener {

        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile == BluetoothProfile.HID_DEVICE) {
                hidDevice = proxy as BluetoothHidDevice
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HID_DEVICE) {
                hidDevice = null
            }
        }
    }

    fun establishBluetoothConnection(context: Context) {
        bluetoothAdapter?.getProfileProxy(context, profileListener, BluetoothProfile.HID_DEVICE)
    }

    override fun onConnectionStateChanged(device: BluetoothDevice?, state: Int) {
        super.onConnectionStateChanged(device, state)
        if (state == BluetoothProfile.STATE_CONNECTED) {
            if (device != null) {
                hostDevice = device
            } else {
                Log.e(TAG, "Device not connected")
            }
        } else {
            hostDevice = null
            if(state == BluetoothProfile.STATE_DISCONNECTED)
            {
                // Callback on device disconnected
            }
        }
    }

    fun sendMouseReport(leftButton: Boolean, x: Byte, y: Byte) {
        val mouseReport = MouseReport()

        mouseReport.leftButton = leftButton
        mouseReport.dx = x
        mouseReport.dy = y

        if (!hidDevice!!.sendReport(hostDevice, MouseReport.ID, mouseReport.bytes)) {
            Log.e(TAG, "Mouse report wasn't sent")
        }
    }

    fun closeBluetoothConnection() {
        bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HID_DEVICE, hidDevice)
    }

}

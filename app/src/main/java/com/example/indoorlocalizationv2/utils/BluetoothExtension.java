package com.example.indoorlocalizationv2.utils;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class BluetoothExtension {
    public static void toast(Context context, String string) {
        Toast toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static String getCharacteristicValue(BluetoothGattCharacteristic characteristic) {
        StringBuilder strB = new StringBuilder();
        for (byte asciiCode : characteristic.getValue()) {
            strB.append((char)asciiCode);
        }
        return strB.toString();
    }

    public static String byteArrayToString(byte[] ba)
    {
        StringBuilder hex = new StringBuilder(ba.length * 2);
        for (byte b : ba)
            hex.append(b + " ");

        return hex.toString();
    }

    public static int asciiToCrc8(byte[] macAddress) {
        byte len = 12;
        int crc = 0xff;
        int i, j;
        for (i = 0; i < len; i++) {
            crc ^= macAddress[i];
            for (j = 0; j < 8; j++) {
                if ((crc & 0x80) != 0)
                    crc = (byte) ((crc << 1) ^ 0x31);
                else
                    crc <<= 1;
            }
        }
        return crc;
    }

    public static String removeColonsFromAddress(String address) {
        StringBuilder strB = new StringBuilder(address);
        for (byte i = 2, d = 0 ; d < 5; i += 3, d++) {
            strB.deleteCharAt(i - d);
        }
        return strB.toString();
    }

    public static String getSourceDeviceAddress(String notification) {
        String[] parts = notification.split("_");
        return parts[0];
    }

    public static String getTargetDeviceAddress(String notification) {
        String[] parts = notification.split("_");
        return parts[1];
    }

    public static int getRssi(String notification) {
        String[] parts = notification.split("_");
        return Integer.parseInt(parts[2]);
    }
}

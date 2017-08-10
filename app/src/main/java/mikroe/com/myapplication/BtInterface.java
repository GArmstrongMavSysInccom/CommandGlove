package mikroe.com.myapplication;

/**
 * Created by Rega on 22.6.2014.
 */


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class BtInterface {

        private BluetoothDevice device = null;
        private BluetoothSocket socket = null;
        private InputStream receiveStream = null;
        private OutputStream sendStream = null;
        private ReceiverThread receiverThread;

        Handler handler;

        public BtInterface(Handler hstatus, Handler h) {
            Set<BluetoothDevice> setpairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
            BluetoothDevice[] pairedDevices = (BluetoothDevice[]) setpairedDevices.toArray(new BluetoothDevice[setpairedDevices.size()]);

            //Search RN41 bluetooth
            for(int i=0;i<pairedDevices.length;i++) {

                //if RN41 founded
                if((pairedDevices[i].getName().contains(RN41.NAME))){

                    device = pairedDevices[i];

                    RN41.FOUND = true;
                    Log.v("N", "RN41 found!");

                    try {

                        Log.v("N", "Try to create socket!");
                        socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                        receiveStream = socket.getInputStream();
                        sendStream = socket.getOutputStream();

                        Log.v("N", "Socket created!");

                        RN41.SOCKET = RN41.SOCKET_STATE.CREATED;

                        if (socket != null){

                            connect();
                        }

                    } catch (IOException e) {
                        RN41.SOCKET = RN41.SOCKET_STATE.NOTHING;
                        e.printStackTrace();
                    }

                    break;
                }
            }

            handler = hstatus;

            receiverThread = new ReceiverThread(h);
        }

    public void SendData() {

        try {
            sendStream.write(RN41.writeBuff);
            sendStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void SendData(String data) {
        try {
            sendStream.write(data.getBytes());
            Log.v("Sending data: ", data);
            sendStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



        public void connect() {

            new Thread() {
                @Override public void run() {
                    try {
                        socket.connect();

                        Message msg = handler.obtainMessage();
                        msg.arg1 = 1;
                        handler.sendMessage(msg);

                        receiverThread.start();

                        RN41.SOCKET = RN41.SOCKET_STATE.CONNECTED;

                        Log.v("N", "Connection done!");

                    }
                    catch (IOException e) {
                        Message msg = handler.obtainMessage();
                        msg.arg1 = 2;
                        handler.sendMessage(msg);
                        Log.v("N", "Connection Failed : "+e.getMessage());
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        public void close() {
            try {
                if(socket.isConnected()) {

                    //if (RN41.SOCKET == RN41.SOCKET_STATE.CONNECTED) {

                        RN41.SOCKET = RN41.SOCKET_STATE.NOTHING;
                    //}

                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public boolean isSocketAlive() {
        return socket.isConnected();        //return socket.isConnected();
    }

        public BluetoothDevice getDevice() {
            return device;
        }

        private class ReceiverThread extends Thread {
            Handler handler;

            ReceiverThread(Handler h) {
                handler = h;
            }

            @Override public void run() {
                while(true) {
                    try {
                        if(receiveStream.available() > 9) {     //  if(receiveStream.available() > 0) { 8

                            byte buffer[] = new byte[10];
                            int k = receiveStream.read(buffer, 0, 10);

                            if(k > 0) {
                                byte rawdata[] = new byte[k];
                                for(int i=0;i<k;i++)
                                    rawdata[i] = buffer[i];

                                String data = new String(rawdata);

                                Message msg = handler.obtainMessage();
                                Bundle b = new Bundle();
                                b.putString("receivedData", data);
                                msg.setData(b);
                                handler.sendMessage(msg);
                                Log.v("Received data: " , data);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    SystemClock.sleep(300);                            // GAA
                    if(RN41.SOCKET == RN41.SOCKET_STATE.CONNECTED) {
                        String wr_data = "GET" + (char) 0x0A;
                        SendData(wr_data);
                    }
                }
            }
        }

}



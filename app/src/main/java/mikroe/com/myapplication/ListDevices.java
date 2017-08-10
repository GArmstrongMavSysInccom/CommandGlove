package mikroe.com.myapplication;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.util.Set;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ListDevices extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter = null;
    private ArrayAdapter<String> mDevicesArrayAdapter;
    private boolean mScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_devices);


        mDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_item);
        final ListView DevicesListView = (ListView) findViewById(R.id.devices_list_view);
        DevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String deviceName = mDevicesArrayAdapter.getItem(position).toString();
                RN41.NAME = deviceName;
                final Intent intent = new Intent(ListDevices.this, RelaysControl.class);
                startActivity(intent);
            }
        });
        DevicesListView.setAdapter(mDevicesArrayAdapter);


        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);


        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_devices, menu);

        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_about).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_refresh) {
            RefreshList();
            return true;
        }

        if (id == R.id.menu_stop) {
            mBluetoothAdapter.cancelDiscovery();
            mScanning = false;
            invalidateOptionsMenu();
            return true;
        }

        if (id == R.id.menu_about) {
            final Intent intent = new Intent(ListDevices.this, About.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        RefreshList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }


    private void RefreshList() {
        if (!mBluetoothAdapter.isDiscovering()) {
            if (!mDevicesArrayAdapter.isEmpty()) {
                mDevicesArrayAdapter.clear();
            }
            mScanning = true;
            invalidateOptionsMenu();
            mBluetoothAdapter.startDiscovery();
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a paired device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //update list view
                if (device.getBondState() == BluetoothDevice.BOND_BONDED)
                {
                    Log.v("Found: ", device.getName());
                    mDevicesArrayAdapter.add(device.getName());
                    mDevicesArrayAdapter.notifyDataSetChanged();
                }

            }

            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                mScanning = false;
                invalidateOptionsMenu();
            }

        }

    };


}

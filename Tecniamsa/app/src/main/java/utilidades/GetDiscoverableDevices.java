package utilidades;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass.Device;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.List;
import java.util.Set;

/**
 * Manages a list of discoverable devices. Bluetooth discovery is done in a
 * separate thread as to not block the UI thread.
 * 
 * Communication is facilitated through the provided handler.
 * 
 * @author jamesgeisler
 */
public class GetDiscoverableDevices extends Activity {

	public static final int DONE_GATHERING_DEVICES = 1;
	public static final int DONE_LOOKING_FOR_DEVICES = 2;

	private static final String TAG = "BluetoothChat";

	private Exception ex;
	public static List<Device> devices;
	private boolean unregistered;

	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();

	/**
	 * Get any bonded devices and add them to the list.
	 */
	private void gatherExistingDevices() {

		Log.e(TAG, "+++ Gathering devices +++");

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// ListView
				// devices.add(new Device(device.getName(),
				// device.getAddress()));
				Log.d("Dispo->", "[" + device.getName() + "]");
			}
		}

		Log.e(TAG, "+++ Done gathering +++");
	}

	/**
	 * Creates intent filter and registers receiver for discovering devices.
	 * Begins device discovery.
	 */
	public void startLookingForDevices() {
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		// _activity.ctx.registerReceiver(mReceiver, filter); // Don't forget to
		// unregister during onDestroy

		mBluetoothAdapter.startDiscovery();
	}

	// Create a BroadcastReceiver for ACTION_FOUND and ACTION_DISCOVERY_FINISHED
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Add the name and address to an array adapter to show in a
				// ListView
				if (device != null && device.getName() != null
						&& device.getAddress() != null)
					// devices.add(new Device(device.getName(),
					// device.getAddress()));
					Log.d("Dispo->", "[" + device.getName() + "]");
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				if (!unregistered) {
					mBluetoothAdapter.cancelDiscovery();
					// _activity.ctx.unregisterReceiver(mReceiver);
					// mHandler.sendEmptyMessage(DONE_LOOKING_FOR_DEVICES);
					unregistered = true;
				}
			}
		}
	};

}

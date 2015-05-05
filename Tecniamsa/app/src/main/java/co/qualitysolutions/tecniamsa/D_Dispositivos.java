package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import Adaptadores.Item;
import Adaptadores.ItemAdapter;

/**
 * Created by Andres on 27/03/2015.
 */
public class D_Dispositivos extends Activity {



    private BluetoothAdapter mBluetoothAdapter;
    private ListView listView;
    private ArrayList<Item> view_devices;
    private ItemAdapter adapter;
    private ArrayList<String> mac;
    private int cont = 1;


    private ArrayList<BluetoothDevice> listdisp;
    private static final int REQUEST_ENABLE_BT = 2;
    private boolean unregister;


    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final String DEVICE_NAME = "Divice_Name";
    public static final String TOAST = "Toast";
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_READ = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_dispositivos);
        inicializarComponenetes();


    }

    public void inicializarComponenetes(){

        mBluetoothAdapter= L_RegistrarPeso.mBluetoothAdapter;
        listView = (ListView) findViewById(R.id.listView);
        view_devices = new ArrayList<Item>();
        listdisp = new ArrayList<BluetoothDevice>();
        mac = new ArrayList<String>();
        unregister=false;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mBluetoothAdapter.cancelDiscovery();
                if(unregister) {
                    unregister = false;
                    unregisterReceiver(mReceiver);
                }
                Item model = (Item) (parent.getItemAtPosition(position));
                Intent intent = new Intent();
                intent.putExtra("address", model.getAddress());
                setResult(24, intent);
                finish();
            }
        });
    }

    public void buscar_dispositivo(View view)
    {
        cont=0;
        if (mBluetoothAdapter.isEnabled()) {
            if (!view_devices.isEmpty()) {
                view_devices.clear();
                listdisp.clear();
                actionAdapter();
            }
            if(mBluetoothAdapter.isDiscovering()){
                mBluetoothAdapter.cancelDiscovery();
                Log.d("DISPOSITIVOS->","DiscoveryCancel");
            }
            Toast alert = Toast.makeText(getApplicationContext(),
                    "Buscando, por favor espere...", Toast.LENGTH_LONG);
            alert.show();
            // paried();
            startLookingForDevices();
        } else {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }


    public boolean permitir(String mac) {
        int i = 0;
        while (i < this.mac.size()) {
            String aux = this.mac.get(i);
            if (aux.equals(mac)) {
                return true;
            }
            i++;
        }

        return false;
    }

    public boolean buscarMac(String mac)
    {
        int i=0;
        while (i<this.listdisp.size())
        {
            String aux=listdisp.get(i).getAddress();
            if(aux.equals(mac))
            {
                return true;
            }
            i++;
        }
        return false;
    }

    public void startLookingForDevices() {
        unregister=true;
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter); // Don't forget to unregister
        mBluetoothAdapter.startDiscovery();
    }

    public void actionAdapter() {
        adapter = new ItemAdapter(this, view_devices);
        listView.setAdapter(adapter);
    }

    public void paried()
    {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
// If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Item i = new Item(0, device.getName(),device.getAddress(),device.getAddress(),"mipmap/ic_action_secure");
                view_devices.add(i);
                listdisp.add(device);
                actionAdapter();
            }
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && device.getName() != null
                        && device.getAddress() != null) {
                    if (!buscarMac(device.getAddress())) {
                        Item i = new Item(cont, device.getName(),
                                device.getAddress(),device.getAddress(),
                                "mipmap/ic_action_secure");
                        view_devices.add(i);
                        listdisp.add(device);
                        cont++;
                        actionAdapter();
                    }
                    Log.d("DISPOSITIVOS", "[" + device.getName() + "]" + "{" + device.getAddress() + "}");
                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                Log.d("DISPOSITIVOS", "FIN DE BUSQUEDA");
                mBluetoothAdapter.cancelDiscovery();
                if(unregister) {
                    unregister = false;
                    unregisterReceiver(mReceiver);
                }
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("DISPOSITIVOS","DESTRUIDO");
        if(unregister)
            unregisterReceiver(mReceiver);
    }

    private AlertDialog crearDialogoConexion(String titulo, String mensaje, final String direccion)
    {
        // Instanciamos un nuevo AlertDialog Builder y le asociamos titulo y mensaje
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder.setMessage(mensaje);

        // Creamos un nuevo OnClickListener para el boton OK que realice la conexion
        DialogInterface.OnClickListener listenerOk = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //conectarDispositivo(direccion);
            }
        };

        // Creamos un nuevo OnClickListener para el boton Cancelar
        DialogInterface.OnClickListener listenerCancelar = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        };

        // Asignamos los botones positivo y negativo a sus respectivos listeners
        alertDialogBuilder.setPositiveButton("Conectar", listenerOk);
        alertDialogBuilder.setNegativeButton("Cancelar", listenerCancelar);

        return alertDialogBuilder.create();
    }
}
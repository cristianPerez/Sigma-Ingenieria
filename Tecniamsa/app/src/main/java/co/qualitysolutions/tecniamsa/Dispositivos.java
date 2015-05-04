/*package co.qualitysolutions.tecniamsa;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import utilidades.BluetoothChatService;
import utilidades.ItemAdapter;
import utilidades.SaveInformation;
import utilidades.Utilities;
/**
 * Created by Andres on 27/03/2015.
 */
/*public class Dispositivos extends Activity implements View.OnClickListener {
    public static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ListView listView;
    private ArrayList<Item> view_devices;
    private ItemAdapter adapter;
    private ArrayList<String> mac;
    private int cont = 1;
    private boolean unregistered;
    public static ArrayList<BluetoothDevice> listdisp;
    private static final int REQUEST_ENABLE_BT = 2;
    private boolean bandera = true;
    private Activity myself;
    private BluetoothDevice device;
    private BluetoothChatService mBlueService = null;
    private String mConnectedDeviceName = null;
    private static final String TAG = "Tecniamsa";
    private static final boolean D = true;
    private String Address;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final String DEVICE_NAME = "Divice_Name";
    public static final String TOAST = "Toast";
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_READ = 2;
    //variables gordo
    private JSONArray clientesPlaneados,listaTrazas,listaEmbalajes,listaPesosPorEmbalaje,send_data_json;
    private JSONObject clienteSeleccionado,trazaSeleccionada,embalajeSeleccionado;
    private SharedPreferences sharedpreferences;
    private String method;
    private String methodInt;
    private TextView date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dispositivos);
        this.myself=this;
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        inicializarComponenetes();
    }
    public void inicializarComponenetes(){
        Button btn = (Button) findViewById(R.id.buscar);
        unregistered = false;
        listView = (ListView) findViewById(R.id.listView);
        this.date = (TextView)findViewById(R.id.dateNow);
        this.date.setText(this.sharedpreferences.getString("FECHA_SERVER", Utilities.getDate().split(" ")[0]));
        view_devices = new ArrayList<Item>();
        listdisp = new ArrayList<BluetoothDevice>();
        mac = new ArrayList<String>();
        btn.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mBluetoothAdapter.cancelDiscovery();
                Item model = (Item) (parent.getItemAtPosition(position));
                leerPeso(model.getAddress());
            }
        });
        try {
            this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
            this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
            this.listaTrazas = this.clienteSeleccionado.getJSONArray("lsttrazas");
            this.trazaSeleccionada = listaTrazas.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA", 0));
            this.listaEmbalajes = this.trazaSeleccionada.getJSONArray("lstembalaje");
            this.embalajeSeleccionado = this.listaEmbalajes.getJSONObject(this.sharedpreferences.getInt("SELECT_EMBALAJE", 0));
            this.listaPesosPorEmbalaje = this.embalajeSeleccionado.getJSONArray("pesos_embalaje");
        }catch (JSONException e) {
            this.listaPesosPorEmbalaje = new JSONArray();
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View view) {
        if (mBluetoothAdapter.isEnabled()) {
            if (!view_devices.isEmpty()) {
                view_devices.clear();
                listdisp.clear();
                actionAdapter();
            }
            Toast alert = Toast.makeText(getApplicationContext(),"Buscando, por favor espere...", Toast.LENGTH_LONG);
            alert.show();
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
    public void startLookingForDevices() {
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        this.registerReceiver(mReceiver, filter); // Don't forget to unregister
        // during onDestroy
        mBluetoothAdapter.startDiscovery();
    }
    public void actionAdapter() {
        adapter = new ItemAdapter(this, view_devices);
        listView.setAdapter(adapter);
    }
    public void leerPeso(String address) {
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth no esta activo", Toast.LENGTH_LONG)
                    .show();
        }
        else {
            Address=address;
            setup();
        }
    }
    public void conectar() {
        device = mBluetoothAdapter.getRemoteDevice(Address);
        mBlueService.connect(device);
    }
/*
    @Override
    public void onStart() {
        super.onStart();
        if (D)
            Log.e(TAG, "++ INICIO ++");
        // If BT is not on, request that it be enabled.
        // setup() will then be called during onActivityResult
        if (start) {
            if (mBlueService == null)
                setup();
        }
    }*/

    /*@Override
    public synchronized void onResume() {
        super.onResume();
        if (D)
            Log.e(TAG, "+ REANUDAR +");
        if (mBlueService != null) {
            if (mBlueService.getState() == BluetoothChatService.STATE_NONE) {
                mBlueService.start();
            }
        }
    }*/
/*
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import co.qualitysolutions.tecniamsa.Item;
import co.qualitysolutions.tecniamsa.R;
import utilidades.ItemAdapter;
private void setup() {
        Log.d(TAG, "MOSTRAR RESPUESTA");
        ensureDiscoverable();
        mBlueService = new BluetoothChatService(this, mHandler);
        conectar();
    }
   /* @Override
    public synchronized void onPause() {
        super.onPause();
        if (bandera)
            Log.d("ATENCION", "- PAUSADO -");
    }
    @Override
    public void onStop() {
        super.onStop();
        if (bandera)
            Log.e("ATENCION", "-- DETENIDO --");
    }*/

    /*@Override
    public void onDestroy() {
        super.onDestroy();
        try {
           this.unregisterReceiver(mReceiver);
        }
        catch (Exception e)
        {
            Log.d("Reciver->",e.toString());
        }
        // Stop the Bluetooth  services
        if (mBlueService != null)
            mBlueService.stop();
        if (bandera)
            Log.d("ATENCION", "--- DESTRUIDO ---");
    }
    private void ensureDiscoverable() {
        if (bandera)
            Log.d("ATENCION", "Garantizando Visibilidad");
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
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
                         && device.getAddress() != null) {
                     // if (permitir(device.getAddress())) {
                     Item i = new Item(cont, device.getName(),
                             "Tecniamsa",device.getAddress(),
                             "mipmap/ic_action_secure");
                     view_devices.add(i);
                     listdisp.add(device);
                     cont++;
                     //}
                     Log.d("DISPOSITIVO", "[" + device.getName() + "]" + "{" + device.getAddress() + "}");
                 } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                         .equals(action)) {
                     if (!unregistered) {
                         mBluetoothAdapter.cancelDiscovery();
                         unregisterReceiver(mReceiver);
                         // mHandler.sendEmptyMessage(DONE_LOOKING_FOR_DEVICES);
                         unregistered = true;
                     }
                 }
                 actionAdapter();
             }
         }
     };
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            Log.d("Handler->","conectado");
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Toast.makeText(getApplicationContext(),
                                    "Conectando a dispositivo: " + device.getName(),
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            Log.d("Handler->","Escuchando");
                            break;
                        case BluetoothChatService.STATE_NONE:
                            Log.d("Handler->","No conectado");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    //	byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    //	String writeMessage = new String(writeBuf);
                    // mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    String line = (String) msg.obj;
                    Log.d("RECIBIDO->",line);
                    if(line.substring(0,2).equals("ST")&&line.substring(6,7).equals("+")) {
                        JSONObject nuevoPeso= new JSONObject();
                        try {
                            nuevoPeso.put("peso_asignado",Double.parseDouble(line.substring(7,line.length() - 2)));
                            listaPesosPorEmbalaje.put(nuevoPeso);
                            embalajeSeleccionado.put("pesos_embalaje",listaPesosPorEmbalaje);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("PLANNED_CLIENTS",clientesPlaneados.toString());
                        editor.commit();
                        //mBlueService.stop();
                        onDestroy();
                        Intent intent = new Intent();
                        setResult(24, intent);
                        finish();
                    }
                    else
                    {
                        //mBlueService.stop();
                        onDestroy();
                        Intent intent = new Intent();
                        setResult(25, intent);
                        finish();
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    // Toast.makeText(getApplicationContext(),
                    //     "Conectado a " + mConnectedDeviceName,
                    //       Toast.LENGTH_SHORT).show();
                    Log.d("Handler->","conectado a: "+mConnectedDeviceName);
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    };
    /**
     * Method to close the session
     *
     * @param v
    public void logOut(View v) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getResources().getString(R.string.cerrarSesion));
        adb.setPositiveButton(
                getResources().getString(R.string.confirm_button_1),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject auxobject= new JSONObject();
                        JSONArray auxjson;
                        try {
                            auxjson =  new JSONArray(sharedpreferences.getString("TRUCK_INFO",null));
                            send_data_json = new JSONArray();
                            auxobject.put("fecha_hora_evento",Utilities.getDate());
                            auxobject.put("metodo","json_tecni_cerrarsesion");
                            auxobject.put("usuario",sharedpreferences.getString("USER_ID", "14880479"));
                            send_data_json.put(auxobject);
                            send_data_json.put(auxjson.get(0));
                            methodInt="51";
                            method="json_tecni_cerrarsesion";
                            Toast.makeText(getApplicationContext(), "Cerrando sesión, espera unos segundos", Toast.LENGTH_LONG).show();
                            Utilities.sendInformation(myself,methodInt,method,send_data_json.toString());
                            //sendInformation();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
        adb.setNegativeButton(
                getResources().getString(R.string.confirm_button_2),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
        adb.show();
    }
}*/

package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import utilidades.BluetoothChatService;
import utilidades.ItemAdapter;

/**
 * Created by Andres on 27/03/2015.
 */
public class Dispositivos extends Activity implements View.OnClickListener {



    public static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ListView listView;
    private ArrayList<Item> view_devices;
    private ItemAdapter adapter;
    private ArrayList<String> mac;
    private int cont = 1;

    private boolean unregistered;
    public static ArrayList<BluetoothDevice> listdisp;
    private static final int REQUEST_ENABLE_BT = 2;
    private boolean bandera = true;


    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final String DEVICE_NAME = "Divice_Name";
    public static final String TOAST = "Toast";
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_READ = 2;

    /*//variables gordo
    private JSONArray clientesPlaneados,listaTrazas,listaEmbalajes,listaPesosPorEmbalaje;
    private JSONObject clienteSeleccionado,trazaSeleccionada,embalajeSeleccionado;
    private SharedPreferences sharedpreferences;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dispositivos);
        //this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        inicializarComponenetes();


    }

    public void inicializarComponenetes(){
        Button btn = (Button) findViewById(R.id.buscar);
        unregistered = false;
        listView = (ListView) findViewById(R.id.listView);
        view_devices = new ArrayList<Item>();
        listdisp = new ArrayList<BluetoothDevice>();
        mac = new ArrayList<String>();
        btn.setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                unregisterReceiver(mReceiver);
                mBluetoothAdapter.cancelDiscovery();
                Item model = (Item) (parent.getItemAtPosition(position));
                Intent intent = new Intent();
                intent.putExtra("address", model.getAddress());
                setResult(24, intent);
                finish();
            }
        });
/*
        try {
            this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
            this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
            this.listaTrazas = this.clienteSeleccionado.getJSONArray("lsttrazas");
            this.trazaSeleccionada = listaTrazas.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA", 0));
            this.listaEmbalajes = this.trazaSeleccionada.getJSONArray("lstembalaje");
            this.embalajeSeleccionado = this.listaEmbalajes.getJSONObject(this.sharedpreferences.getInt("SELECT_EMBALAJE", 0));
            this.listaPesosPorEmbalaje = this.embalajeSeleccionado.getJSONArray("pesos_embalaje");
        }catch (JSONException e) {
            this.listaPesosPorEmbalaje = new JSONArray();
            e.printStackTrace();
        }*/
    }


    @Override
    public void onClick(View view) {
        if (mBluetoothAdapter.isEnabled()) {
            if (!view_devices.isEmpty()) {
                view_devices.clear();
                listdisp.clear();
                actionAdapter();
            }

            Toast alert = Toast.makeText(getApplicationContext(),
                    "Buscando, por favor espere...", Toast.LENGTH_LONG);
            alert.show();
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

    public void startLookingForDevices() {
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        this.registerReceiver(mReceiver, filter); // Don't forget to unregister
        // during onDestroy

        mBluetoothAdapter.startDiscovery();
    }

    public void actionAdapter() {
        adapter = new ItemAdapter(this, view_devices);
        listView.setAdapter(adapter);
    }

    private void ensureDiscoverable() {
        if (bandera)
            Log.d("ATENCION", "Garantizando Visibilidad");
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
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
                        && device.getAddress() != null) {

                    // if (permitir(device.getAddress())) {
                    Item i = new Item(cont, device.getName(),
                            device.getAddress(),device.getAddress(),
                            "mipmap/ic_action_secure");
                    view_devices.add(i);
                    listdisp.add(device);
                    cont++;
                    //}

                    Log.d("DISPOSITIVO", "[" + device.getName() + "]" + "{" + device.getAddress() + "}");
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                        .equals(action)) {
                    if (!unregistered) {
                        mBluetoothAdapter.cancelDiscovery();
                        unregisterReceiver(mReceiver);
                        // mHandler.sendEmptyMessage(DONE_LOOKING_FOR_DEVICES);
                        unregistered = true;
                    }
                }
                actionAdapter();
            }

        }
    };

}
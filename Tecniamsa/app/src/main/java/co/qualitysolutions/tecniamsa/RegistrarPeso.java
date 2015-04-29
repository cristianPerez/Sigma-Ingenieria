/*package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utilidades.BluetoothChatService;
import utilidades.ItemAdapterJsonPesos;
import utilidades.SaveInformation;
import utilidades.Utilities;

public class RegistrarPeso extends Activity{

    private ListView listView;

    //public static ArrayList<Item> view_pesos;
    public static JSONArray view_pesos_json;

    //private ItemAdapter adapter;
    private ItemAdapterJsonPesos adapterJson;
    private TextView tipoEmbalaje,pesoTotal;
    private JSONArray clientesPlaneados,listaTrazas,listaEmbalajes;
    private JSONObject clienteSeleccionado,trazaSeleccionada,embalajeSeleccionado;
    private SharedPreferences sharedpreferences;
    private JSONArray send_data_json;
    private String method;
    private String methodInt;
    private TextView date;
    private Activity myself;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_peso);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.myself=this;
        this.inicializarComponentes();
    }

    public void inicializarComponentes(){
        this.date = (TextView)findViewById(R.id.dateNow);
        this.date.setText(this.sharedpreferences.getString("FECHA_SERVER", Utilities.getDate().split(" ")[0]));

        listView = (ListView) findViewById(R.id.list_pesos);
        this.tipoEmbalaje = (TextView) findViewById(R.id.tipoEmbalaje);
        this.pesoTotal = (TextView)findViewById(R.id.peso_total);
        try {
            this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
            this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
            this.listaTrazas = this.clienteSeleccionado.getJSONArray("lsttrazas");
            this.trazaSeleccionada = listaTrazas.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA", 0));
            this.listaEmbalajes = this.trazaSeleccionada.getJSONArray("lstembalaje");
            this.embalajeSeleccionado = this.listaEmbalajes.getJSONObject(this.sharedpreferences.getInt("SELECT_EMBALAJE", 0));
            this.tipoEmbalaje.setText(this.embalajeSeleccionado.getString("nombre"));
            this.pesoTotal.setText(this.embalajeSeleccionado.getDouble("pesoTotal")+" KG");
            this.actionAdapter();
        }catch (JSONException e) {
            this.pesoTotal.setText("0.0 KG");
            e.printStackTrace();
        }
    }


    public void buscar_dispositivo(View view){
        startActivityForResult(new Intent(getApplicationContext(), Dispositivos.class), 20);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==24) {
            actionAdapter();
            sumarPeso();
        }
        else if(resultCode==25)
        {
            Utilities.showAlert(this,"Verifique el peso");
        }
    }


    public void actualizarEmbalaje(){

        try {
            this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
            this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
            this.listaTrazas = this.clienteSeleccionado.getJSONArray("lsttrazas");
            this.trazaSeleccionada = listaTrazas.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA", 0));
            this.listaEmbalajes = this.trazaSeleccionada.getJSONArray("lstembalaje");
            this.embalajeSeleccionado = this.listaEmbalajes.getJSONObject(this.sharedpreferences.getInt("SELECT_EMBALAJE", 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void actionAdapter(){
        try {
            actualizarEmbalaje();
            this.view_pesos_json = this.embalajeSeleccionado.getJSONArray("pesos_embalaje");
        } catch (JSONException e) {
            this.view_pesos_json = new JSONArray();
            e.printStackTrace();
        }
        adapterJson = new ItemAdapterJsonPesos(this, view_pesos_json);
        listView.setAdapter(adapterJson);
    }

    public  void sumarPeso()
    {
        double cont=0;
        try {
        for(int i=0;i<view_pesos_json.length();i++)
        {
            double peso;

                peso = view_pesos_json.getJSONObject(i).getDouble("peso_asignado");
                cont += peso;

        }
            this.embalajeSeleccionado.put("pesoTotal",cont);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("PLANNED_CLIENTS",this.clientesPlaneados.toString());
            editor.commit();
            pesoTotal.setText(String.valueOf(cont)+ "KG");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void back(View view){

        finish();

    }


    public void restarPeso(String peso)
    {
        this.pesoTotal.setText(peso);
    }




}*/

package co.qualitysolutions.tecniamsa;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.util.Log;
        import android.view.View;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import utilidades.BluetoothChatService;
        import utilidades.ItemAdapterJsonPesos;
        import utilidades.SaveInformation;
        import utilidades.Utilities;

public class RegistrarPeso extends Activity{

    private ListView listView;
    private boolean adicionar;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final String DEVICE_NAME = "Divice_Name";
    public static final String TOAST = "Toast";
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_READ = 2;
    private static final int REQUEST_ENABLE_BT = 2;

    private boolean bandera = true;
    private String mConnectedDeviceName = null;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mBluetoothService = null;
    private String Address;
    private BluetoothDevice device;
    private static final String TAG = "Tecniamsa";
    private static final boolean D = true;


    public static JSONArray view_pesos_json;
    private ItemAdapterJsonPesos adapterJson;
    private TextView tipoEmbalaje,pesoTotal;
    private JSONArray clientesPlaneados,listaTrazas,listaEmbalajes,listaPesosPorEmbalaje;
    private JSONObject clienteSeleccionado,trazaSeleccionada,embalajeSeleccionado;
    private SharedPreferences sharedpreferences;
    private JSONArray send_data_json;
    private String method;
    private String methodInt;
    private TextView date;
    private Activity myself;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_peso);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.inicializarComponentes();
        this.myself=this;
    }

    public void inicializarComponentes(){
        adicionar=false;
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        listView = (ListView) findViewById(R.id.list_pesos);
        this.tipoEmbalaje = (TextView) findViewById(R.id.tipoEmbalaje);
        this.pesoTotal = (TextView)findViewById(R.id.peso_total);
        try {
            this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
            this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
            this.listaTrazas = this.clienteSeleccionado.getJSONArray("lsttrazas");
            this.trazaSeleccionada = listaTrazas.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA", 0));
            this.listaEmbalajes = this.trazaSeleccionada.getJSONArray("lstembalaje");
            this.embalajeSeleccionado = this.listaEmbalajes.getJSONObject(this.sharedpreferences.getInt("SELECT_EMBALAJE", 0));
            this.tipoEmbalaje.setText(this.embalajeSeleccionado.getString("nombre"));
            this.listaPesosPorEmbalaje = this.embalajeSeleccionado.getJSONArray("pesos_embalaje");
            this.pesoTotal.setText(this.embalajeSeleccionado.getDouble("pesoTotal")+" KG");
            this.actionAdapter();
        }catch (JSONException e) {
            this.listaPesosPorEmbalaje = new JSONArray();
            this.pesoTotal.setText("0.0 KG");
            e.printStackTrace();
        }
    }

    public void cambiar_dispositivo(View view)
    {
        startActivityForResult(new Intent(getApplicationContext(), Dispositivos.class), 20);
    }
    public void optener_peso(View view){
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if(mBluetoothService ==null)
        {
            startActivityForResult(new Intent(getApplicationContext(), Dispositivos.class), 20);
        }
        else {
            conectar();
            adicionar = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==24) {
            Address= data.getStringExtra("address");
            setup();
        }
    }


    public void actualizarEmbalaje(){

        try {
            this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
            this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
            this.listaTrazas = this.clienteSeleccionado.getJSONArray("lsttrazas");
            this.trazaSeleccionada = listaTrazas.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA", 0));
            this.listaEmbalajes = this.trazaSeleccionada.getJSONArray("lstembalaje");
            this.embalajeSeleccionado = this.listaEmbalajes.getJSONObject(this.sharedpreferences.getInt("SELECT_EMBALAJE", 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void actionAdapter(){
        try {
            actualizarEmbalaje();
            this.view_pesos_json = this.embalajeSeleccionado.getJSONArray("pesos_embalaje");
        } catch (JSONException e) {
            this.view_pesos_json = new JSONArray();
            e.printStackTrace();
        }
        adapterJson = new ItemAdapterJsonPesos(this, view_pesos_json);
        listView.setAdapter(adapterJson);
    }

    public  void sumarPeso()
    {
        double cont=0;
        try {
            for(int i=0;i<view_pesos_json.length();i++)
            {
                double peso;

                peso = view_pesos_json.getJSONObject(i).getDouble("peso_asignado");
                cont += peso;

            }
            this.embalajeSeleccionado.put("pesoTotal",cont);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("PLANNED_CLIENTS",this.clientesPlaneados.toString());
            editor.commit();
            pesoTotal.setText(String.valueOf(cont)+ "KG");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void back(View view){
        onDestroy();
        finish();

    }


    public void restarPeso(String peso)
    {
        this.pesoTotal.setText(peso);
    }


    /**
     * Method to close the session
     *
     * @param v
     */
    public void logOut(View v) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getResources().getString(R.string.logout_confirm));
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

                            auxobject.put("fecha_hora_evento", Utilities.getDate());
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

    /**
     *Method that send the information to server, from whatever method
     */
    public void sendInformation(){

        try {
            new SaveInformation(this).execute("http://www.concesionesdeaseo.com/gruposala/FUNEventosMovil/Eventos",
                    this.methodInt,
                    this.method,
                    this.send_data_json.toString());
        } catch (Exception e) {
        }
    }


    public void conectar() {

        if(Address!=null) {
            device = mBluetoothAdapter.getRemoteDevice(Address);
            mBluetoothService.connect(device);
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        if (D)
            Log.e(TAG, "++ INICIO ++");
        // If BT is not on, request that it be enabled.
        // setup() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mBluetoothService == null && adicionar)
                setup();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (D)
            Log.e(TAG, "+ REANUDAR +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity
        // returns.
        if (mBluetoothService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't
            // started already
            if (mBluetoothService.getState() == BluetoothChatService.STATE_NONE) {
                mBluetoothService.start();
            }
        }
    }

    private void setup() {
        Log.d(TAG, "MOSTRAR RESPUESTA");
        ensureDiscoverable();
        mBluetoothService = new BluetoothChatService(this, mHandler);
        conectar();

    }

    @Override
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null)
            mBluetoothService.stop();
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
                    Log.d("RECIBIDO->", line);
                    if(adicionar) {
                        adicionar=false;
                        if (line.substring(0, 2).equals("ST") && line.substring(6, 7).equals("+")) {
                            JSONObject nuevoPeso = new JSONObject();
                            try {
                                nuevoPeso.put("peso_asignado", Double.parseDouble(line.substring(7, line.length() - 2)));
                                listaPesosPorEmbalaje.put(nuevoPeso);
                                embalajeSeleccionado.put("pesos_embalaje", listaPesosPorEmbalaje);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("PLANNED_CLIENTS", clientesPlaneados.toString());
                            editor.commit();
                            actionAdapter();
                            sumarPeso();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Verifique el peso",
                                    Toast.LENGTH_LONG).show();
                        }
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

}


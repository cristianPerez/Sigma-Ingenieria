package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_peso);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
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
                            sendInformation();
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
            new SaveInformation(this).execute(getResources().getString(R.string.urlPruebas),
                    this.methodInt,
                    this.method,
                    this.send_data_json.toString());
        } catch (Exception e) {
        }
    }

}

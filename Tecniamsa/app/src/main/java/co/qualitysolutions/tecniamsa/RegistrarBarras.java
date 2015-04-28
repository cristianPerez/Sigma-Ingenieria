package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utilidades.ItemAdapterJsonCodigos;
import utilidades.SaveInformation;
import utilidades.Utilities;

/**
 * Created by teresa on 31/03/15.
 */
public class RegistrarBarras extends Activity{
    public static JSONArray view_barras_json;
    private ListView listView;
    private ItemAdapterJsonCodigos adapterJson;

    private TextView tipoEmbalaje,cantTotal;
    private JSONArray clientesPlaneados,listaTrazas,listaEmbalajes,listaBarrasPorEmbalaje;
    private JSONObject clienteSeleccionado,trazaSeleccionada,embalajeSeleccionado;
    private SharedPreferences sharedpreferences;
    private Activity myself;
    private JSONArray send_data_json;
    private String method;
    private String methodInt;
    private TextView date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_barras);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.myself=this;
        this.inicializarComponentes();
    }

    public void inicializarComponentes(){

        this.date = (TextView)findViewById(R.id.dateNow);
        this.date.setText(this.sharedpreferences.getString("FECHA_SERVER", Utilities.getDate().split(" ")[0]));
        listView = (ListView) findViewById(R.id.list_barras);

        this.tipoEmbalaje = (TextView) findViewById(R.id.tipoEmbalaje);
        this.cantTotal = (TextView)findViewById(R.id.cant_total);
        try {
            this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
            this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
            this.listaTrazas = this.clienteSeleccionado.getJSONArray("lsttrazas");
            this.trazaSeleccionada = listaTrazas.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA", 0));
            this.listaEmbalajes = this.trazaSeleccionada.getJSONArray("lstembalaje");
            this.embalajeSeleccionado = this.listaEmbalajes.getJSONObject(this.sharedpreferences.getInt("SELECT_EMBALAJE", 0));
            this.tipoEmbalaje.setText(this.embalajeSeleccionado.getString("nombre"));
            this.listaBarrasPorEmbalaje = this.embalajeSeleccionado.getJSONArray("barras_embalaje");
            this.actionAdapter();
            this.cantTotal.setText(String.valueOf(this.embalajeSeleccionado.getInt("cantTotal")));

        }catch (JSONException e) {
            this.cantTotal.setText("0");
            this.listaBarrasPorEmbalaje = new JSONArray();
            e.printStackTrace();
        }
    }

    public void back(View view){

        finish();

    }

    public void actionAdapter(){
        try {
            actualizarEmbalaje();
            this.view_barras_json = this.embalajeSeleccionado.getJSONArray("barras_embalaje");
            this.cantTotal.setText(String.valueOf(this.embalajeSeleccionado.getInt("cantTotal")));
        } catch (JSONException e) {
            this.view_barras_json = new JSONArray();
            e.printStackTrace();
        }
        adapterJson = new ItemAdapterJsonCodigos(this, view_barras_json);
        listView.setAdapter(adapterJson);
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

    public void registrarBarras(View view)
    {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "ONE_D_MODE");
        startActivityForResult(intent, 0);

    }

    public void restarBarras(String barras)
    {
        this.cantTotal.setText(barras);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                Log.d("SCAN->", contents + "|" + format);
                int cont=Integer.parseInt(cantTotal.getText().toString());
                cont++;
                JSONObject nuevaBarra= new JSONObject();
                try {
                    nuevaBarra.put("barra_asignada",contents);
                    listaBarrasPorEmbalaje.put(nuevaBarra);
                    embalajeSeleccionado.put("barras_embalaje",listaBarrasPorEmbalaje);
                    embalajeSeleccionado.put("cantTotal",cont);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("PLANNED_CLIENTS",clientesPlaneados.toString());
                editor.commit();
                actionAdapter();
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("SCAN->","Cancelado");
            }
        }

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


}

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utilidades.ItemAdapterJsonPesos;
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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_peso);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.inicializarComponentes();
    }

    public void inicializarComponentes(){
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

}

package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utilidades.ItemAdapterJsonCodigos;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_barras);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.inicializarComponentes();
    }

    public void inicializarComponentes(){
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

    public void actionAdapter(){
        try {
            this.view_barras_json = this.embalajeSeleccionado.getJSONArray("barras_embalaje");
            this.cantTotal.setText(String.valueOf(this.embalajeSeleccionado.getInt("cantTotal")));
        } catch (JSONException e) {
            this.view_barras_json = new JSONArray();
            e.printStackTrace();
        }
        adapterJson = new ItemAdapterJsonCodigos(this, view_barras_json);
        listView.setAdapter(adapterJson);
    }

    public void registrarBarras(View view)
    {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "ONE_D_MODE");
        startActivityForResult(intent, 0);

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

}

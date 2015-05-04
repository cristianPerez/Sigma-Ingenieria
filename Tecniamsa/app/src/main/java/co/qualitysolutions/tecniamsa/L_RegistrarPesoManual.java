package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utilidades.ItemAdapterJsonPesos;


public class L_RegistrarPesoManual extends Activity {


    private JSONArray clientesPlaneados,listaTrazas,listaEmbalajes,listaPesos;
    private JSONObject clienteSeleccionado,trazaSeleccionada,embalajeSeleccionado,pesoNuevo;
    private SharedPreferences sharedpreferences;
    private EditText edtPesoNuevo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_registrar_peso_manual);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.edtPesoNuevo = (EditText) findViewById(R.id.edtPesoNuevo);
    }

    public void adicionarPeso(View view){

        actualizarEmbalaje();
        try {

            this.listaPesos = this.embalajeSeleccionado.getJSONArray("pesos_embalaje");
            this.pesoNuevo = new JSONObject();
            this.pesoNuevo.put("peso_asignado",this.edtPesoNuevo.getText());
            this.listaPesos.put(this.pesoNuevo);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("PLANNED_CLIENTS",this.clientesPlaneados.toString());
            editor.commit();

            Intent intent = new Intent();
            setResult(20, intent);
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
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

}

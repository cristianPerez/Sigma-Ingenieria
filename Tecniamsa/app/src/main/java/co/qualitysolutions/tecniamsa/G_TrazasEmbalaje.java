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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class G_TrazasEmbalaje extends Activity implements AdapterView.OnItemSelectedListener{

    private ArrayList<String> lstItemsCategoria1,lstItemsCategoria2;
    private ArrayAdapter<String> adapterItemsCategoria1,adapterItemsCategoria2;
    private Spinner spinner_trazas,spinnerEmbalajes;
    private JSONArray clientesPlaneados,trazasSelect,embalajeSelect;
    private JSONObject clienteSeleccionado;
    private SharedPreferences sharedpreferences;
    private TextView codigoCliente,nombreCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.g_trazas_embalaje);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        inicializarComponentes();
    }

    public void regitrarCodigoDeBarras(View view){

        Intent intent = new Intent(this, RegistrarBarras.class);
        startActivity(intent);

    }

    public void inicializarComponentes() {

        this.codigoCliente = (TextView) findViewById(R.id.codigoCliente);
        this.nombreCliente = (TextView) findViewById(R.id.nombreCliente);
        this.spinner_trazas = (Spinner) findViewById(R.id.spinnerTrazas);
        this.spinner_trazas.setOnItemSelectedListener(this);
        this.spinnerEmbalajes = (Spinner) findViewById(R.id.spinnerEmbalajes);
        this.spinnerEmbalajes.setOnItemSelectedListener(this);

        try {
            this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
            this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
            this.codigoCliente.setText(this.clienteSeleccionado.getString("codigo_cliente"));
            this.nombreCliente.setText(this.clienteSeleccionado.getString("nombre_cliente"));
            llenarSpinnerTrazas();
            llenarSpinnerEmbalaje();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void llenarSpinnerTrazas(){

        try {
            trazasSelect = this.clienteSeleccionado.getJSONArray("lsttrazas");
            if(trazasSelect.length()>0){
                String lstItemStringCategoria1 = getStringArrayList(trazasSelect);
                lstItemsCategoria1 = new ArrayList<String>(Arrays.asList(lstItemStringCategoria1.substring(1, lstItemStringCategoria1.length() - 1).split(",")));
                lstItemsCategoria1 = cleanEmptyCharacter(lstItemsCategoria1);
                adapterItemsCategoria1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstItemsCategoria1);
                adapterItemsCategoria1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                this.spinner_trazas.setAdapter(adapterItemsCategoria1);
            }
            else {


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void llenarSpinnerEmbalaje(){

        try {
            embalajeSelect = trazasSelect.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA",0)).getJSONArray("lstembalaje");
            if(embalajeSelect.length()>0) {
                String lstItemStringCategoria1 = getStringArrayList(embalajeSelect);
                lstItemsCategoria2 = new ArrayList<String>(Arrays.asList(lstItemStringCategoria1.substring(1, lstItemStringCategoria1.length() - 1).split(",")));
                lstItemsCategoria2 = cleanEmptyCharacter(lstItemsCategoria2);
                adapterItemsCategoria2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstItemsCategoria2);
                adapterItemsCategoria2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                this.spinnerEmbalajes.setAdapter(adapterItemsCategoria2);
            }
            else{

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> cleanEmptyCharacter(ArrayList<String> list){
        for (int i=0;i<list.size();i++)
            list.set(i, list.get(i).trim());
        return list;
    }

    public String getStringArrayList(JSONArray json){
        ArrayList<String> aux = new ArrayList<String>();
        for(int i=0;i<json.length();i++){
            try {
                aux.add(json.getJSONObject(i).getString("nombre"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return aux.toString();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==this.spinner_trazas.getId()){
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("SELECT_TRAZA",position);
            editor.commit();
            llenarSpinnerEmbalaje();
        }

        else if(parent.getId()==this.spinnerEmbalajes.getId()){
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("SELECT_EMBALAJE",position);
            editor.commit();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void agregarPesos(View view){

        Intent intent = new Intent(this, RegistrarPeso.class);
        startActivity(intent);

    }
}

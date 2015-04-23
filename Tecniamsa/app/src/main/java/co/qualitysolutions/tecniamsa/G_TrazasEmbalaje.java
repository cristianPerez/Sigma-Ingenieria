package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import utilidades.SaveInformation;
import utilidades.Utilities;


public class G_TrazasEmbalaje extends Activity implements AdapterView.OnItemSelectedListener{

    private ArrayList<String> lstItemsCategoria1,lstItemsCategoria2;
    private ArrayAdapter<String> adapterItemsCategoria1,adapterItemsCategoria2;
    private Spinner spinner_trazas,spinnerEmbalajes;
    private JSONArray clientesPlaneados,trazasSelect,embalajeSelect,listaEmbalajes;
    private JSONObject clienteSeleccionado;
    private SharedPreferences sharedpreferences;
    private TextView codigoCliente,nombreCliente,peso_total_traza,barras_total_traza;

    private JSONArray send_data_json;
    private String method;
    private String methodInt;
    private TextView date;

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

    @Override
    protected void onResume() {
    super.onResume();
        try {
            cantidadTotalBarrasTraza();
            cantidadTotalPesosTraza();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void cantidadTotalBarrasTraza() throws JSONException {
        int cont=0;

        this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
        this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
        trazasSelect = this.clienteSeleccionado.getJSONArray("lsttrazas");

        if(trazasSelect.length()>0)
            {

                this.listaEmbalajes = trazasSelect.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA",0)).getJSONArray("lstembalaje");
                for (int i=0; i<listaEmbalajes.length();i++)
                {
                    JSONObject aux= listaEmbalajes.getJSONObject(i);
                    try {
                        JSONArray lis = aux.getJSONArray("barras_embalaje");
                        cont+=lis.length();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }


                }

            }
        this.clienteSeleccionado.put("CodigosTotalLeidos",cont);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("PLANNED_CLIENTS",this.clientesPlaneados.toString());
        editor.commit();
        this.barras_total_traza.setText(String.valueOf(cont));
    }

    public void cantidadTotalPesosTraza() throws JSONException {
        float cont=0;

        this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
        this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
        trazasSelect = this.clienteSeleccionado.getJSONArray("lsttrazas");

        if(trazasSelect.length()>0)
            {

                this.listaEmbalajes = trazasSelect.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA",0)).getJSONArray("lstembalaje");
                for (int i=0; i<listaEmbalajes.length();i++)
                {
                    JSONObject aux= listaEmbalajes.getJSONObject(i);
                    try {

                        cont+=aux.getDouble("pesoTotal");
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }


                }

            }

        this.clienteSeleccionado.put("PesoTotalRecogido",cont);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("PLANNED_CLIENTS",this.clientesPlaneados.toString());
        editor.commit();
        this.peso_total_traza.setText(String.valueOf(cont) + "KG");
    }

    public void inicializarComponentes() {

        this.date = (TextView)findViewById(R.id.dateNow);
        this.date.setText(this.sharedpreferences.getString("FECHA_SERVER", Utilities.getDate().split(" ")[0]));
        barras_total_traza=(TextView) findViewById(R.id.cantidad_total_traza);
        this.codigoCliente = (TextView) findViewById(R.id.codigoCliente);
        this.nombreCliente = (TextView) findViewById(R.id.nombreCliente);
        this.peso_total_traza = (TextView) findViewById(R.id.peso_total_traza);
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
            //cantidadTotalBarrasTraza();

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
            try {
                cantidadTotalBarrasTraza();
                cantidadTotalPesosTraza();
                //this.peso_total_traza.setText(String.valueOf(this.trazasSelect.getJSONObject(position).getDouble("pesoTotal"))+" KG");
            } catch (JSONException e) {
                e.printStackTrace();
                //this.peso_total_traza.setText("0 KG");
            }
        }

        else if(parent.getId()==this.spinnerEmbalajes.getId()){
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("SELECT_EMBALAJE",position);
            editor.commit();
            try {
                cantidadTotalBarrasTraza();
                cantidadTotalPesosTraza();
                //this.peso_total_traza.setText(String.valueOf(this.trazasSelect.getJSONObject(position).getDouble("pesoTotal"))+" KG");
            } catch (JSONException e) {
                e.printStackTrace();
                //this.peso_total_traza.setText("0 KG");
            }
        }

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void agregarPesos(View view){

        Intent intent = new Intent(this, RegistrarPeso.class);
        startActivity(intent);

    }

    public void mainMenu(View view){

        Intent intent = new Intent(this, E_MenuCiclo.class);
        startActivity(intent);

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

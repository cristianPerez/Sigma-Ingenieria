package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import utilidades.Utilities;


public class G_TrazasEmbalaje extends Activity implements AdapterView.OnItemSelectedListener,CompoundButton.OnCheckedChangeListener,View.OnClickListener {

    private ArrayList<String> lstItemsCategoria1,lstItemsCategoria2;
    private ArrayAdapter<String> adapterItemsCategoria1,adapterItemsCategoria2;
    private Spinner spinnerTrazas,spinnerEmbalajes,spinnerPuntoPesaje,spinnerPesoRecoleccion,
            spinnerCantidadRecoleccion,spinnerAptoCargue,spinnerCausalesAptoCargue;
    private CheckBox checkBoxRecoleccion,checkBoxTirillas;
    private JSONArray clientesPlaneados,lstTrazasCliente,lstEmbalajesTraza;
    private JSONObject clienteSeleccionado,trazaSeleccionada,embalajeSeleccionado;
    private SharedPreferences sharedpreferences;
    private TextView codigoCliente,nombreCliente,peso_total_traza,barras_total_traza,codigoSolicitud;
    private Activity myself;
    private JSONArray send_data_json;
    private String method;
    private String methodInt;
    private TextView date;
    private LinearLayout causalesDeNoCargue;
    private Button btnPregunta,btnMenuPrincipal;
    private ArrayList<String> lstItemsCausalesNoCargue;
    private ArrayAdapter<String> adapterItemsCausalesNoCargue;
    private String Metodo;
    private AlertDialog.Builder adb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.g_trazas_embalaje);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.myself=this;
        this.Metodo = getIntent().getExtras().getString("Metodo");
        inicializarComponentes();
    }



    public void inicializarComponentes() {

        this.adb = new AlertDialog.Builder(this);
        this.date = (TextView)findViewById(R.id.dateNow);
        this.date.setText(this.sharedpreferences.getString("FECHA_SERVER", Utilities.getDate().split(" ")[0]));
        this.barras_total_traza=(TextView) findViewById(R.id.cantidad_total_traza);
        this.codigoCliente = (TextView) findViewById(R.id.codigoCliente);
        this.nombreCliente = (TextView) findViewById(R.id.nombreCliente);
        this.peso_total_traza = (TextView) findViewById(R.id.peso_total_traza);
        this.codigoSolicitud = (TextView) findViewById(R.id.codigoSolicitud);

        this.spinnerPuntoPesaje = (Spinner) findViewById(R.id.spinnerPuntoPesaje);
        this.spinnerPuntoPesaje.setOnItemSelectedListener(this);
        this.spinnerTrazas = (Spinner) findViewById(R.id.spinnerTrazas);
        this.spinnerTrazas.setOnItemSelectedListener(this);
        this.spinnerEmbalajes = (Spinner) findViewById(R.id.spinnerEmbalajes);
        this.spinnerEmbalajes.setOnItemSelectedListener(this);

        this.spinnerPesoRecoleccion = (Spinner) findViewById(R.id.spinnerPesoRecoleccion);
        this.spinnerPesoRecoleccion.setOnItemSelectedListener(this);

        this.spinnerCantidadRecoleccion = (Spinner) findViewById(R.id.spinnerCantidadRecoleccion);
        this.spinnerCantidadRecoleccion.setOnItemSelectedListener(this);

        this.spinnerAptoCargue = (Spinner) findViewById(R.id.spinnerAptoCargue);
        this.spinnerAptoCargue.setOnItemSelectedListener(this);

        this.spinnerCausalesAptoCargue = (Spinner) findViewById(R.id.spinnerCausalesAptoCargue);
        this.spinnerCausalesAptoCargue.setOnItemSelectedListener(this);

        this.causalesDeNoCargue = (LinearLayout)findViewById(R.id.causalesDeNoCargue);

        this.checkBoxRecoleccion = (CheckBox)findViewById(R.id.checkBoxRecoleccion);
        this.checkBoxRecoleccion.setOnCheckedChangeListener(this);
        this.checkBoxTirillas = (CheckBox)findViewById(R.id.checkBoxTirillas);
        this.checkBoxTirillas.setOnCheckedChangeListener(this);

        this.btnPregunta = (Button) findViewById(R.id.btnPregunta);
        this.btnMenuPrincipal = (Button) findViewById(R.id.btnMenuPrincipal);
        this.btnMenuPrincipal.setOnClickListener(this);

        try {
            this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
            this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));

            if(clienteSeleccionado.getString("hora_llegada_sitio_entrega").equals("0")) {
                btnPregunta.setEnabled(true);
                btnPregunta.setBackgroundColor(getResources().getColor(R.color.btn4));
            }
            else{
                btnPregunta.setEnabled(false);
                btnPregunta.setBackgroundColor(getResources().getColor(R.color.gray));
            }


            this.codigoSolicitud.setText(this.clienteSeleccionado.getString("solicitud"));
            this.codigoCliente.setText(this.clienteSeleccionado.getString("codigo_cliente"));
            this.nombreCliente.setText(this.clienteSeleccionado.getString("nombre_cliente"));
            llenarSpinnerTrazas();
            llenarSpinnerEmbalaje();
            llenarOtrosSpinner();
            cantidadTotalBarrasTraza();
            cantidadTotalPesosTraza();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(Metodo.equals("actualizarTrazas")){

            this.btnMenuPrincipal.setText("Guardar Traza");
            this.spinnerCantidadRecoleccion.setEnabled(false);

            this.spinnerPesoRecoleccion.setEnabled(false);

            this.spinnerPuntoPesaje.setEnabled(false);

            this.spinnerAptoCargue.setEnabled(false);

            this.spinnerCausalesAptoCargue.setEnabled(false);

            this.checkBoxRecoleccion.setEnabled(false);

            this.checkBoxTirillas.setEnabled(false);

        }

    }

    public void llenarSpinnerNoCargue (){

        String lstItemStringNoCargue=sharedpreferences.getString("CAUSALES_NO_CARGUE","[]");
        lstItemsCausalesNoCargue = new ArrayList<String>(Arrays.asList(lstItemStringNoCargue.substring(1, lstItemStringNoCargue.length() - 1).split(",")));
        lstItemsCausalesNoCargue = cleanEmptyCharacter(lstItemsCausalesNoCargue);
        adapterItemsCausalesNoCargue = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,lstItemsCausalesNoCargue);
        adapterItemsCausalesNoCargue.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCausalesAptoCargue.setAdapter(adapterItemsCausalesNoCargue);

    }




    public void regitrarCodigoDeBarras(View view){

        Intent intent = new Intent(this, L_RegistrarBarras.class);
        startActivity(intent);

    }

    public void llegoSitioEntrega(View view){


        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("!ALerta");
        adb.setMessage("¿ Esta seguro de que esta en el sitio de entrega ?");
        adb.setPositiveButton(
                getResources().getString(R.string.confirm_button_1),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            clientesPlaneados = new JSONArray(sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
                            clienteSeleccionado = clientesPlaneados.getJSONObject(sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
                            if(clienteSeleccionado.getString("hora_llegada_sitio_entrega").equals("0")){
                                clienteSeleccionado.put("hora_llegada_sitio_entrega",Utilities.getDate());
                                btnPregunta.setEnabled(false);
                                btnPregunta.setBackgroundColor(getResources().getColor(R.color.gray));
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("PLANNED_CLIENTS",clientesPlaneados.toString());
                                editor.commit();
                            }
                        } catch (JSONException e) {
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


    @Override
    protected void onResume() {
    super.onResume();
     cantidadTotalBarrasTraza();
     cantidadTotalPesosTraza();
        try {
            if(clienteSeleccionado.getString("hora_llegada_sitio_entrega").equals("0")) {
                btnPregunta.setEnabled(true);
                btnPregunta.setBackgroundColor(getResources().getColor(R.color.btn4));
            }
            else{
                btnPregunta.setEnabled(false);
                btnPregunta.setBackgroundColor(getResources().getColor(R.color.gray));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(Metodo.equals("actualizarTrazas")){

            this.btnMenuPrincipal.setText("Guardar Traza");
            this.spinnerCantidadRecoleccion.setEnabled(false);

            this.spinnerPesoRecoleccion.setEnabled(false);

            this.spinnerPuntoPesaje.setEnabled(false);

            this.spinnerAptoCargue.setEnabled(false);

            this.spinnerCausalesAptoCargue.setEnabled(false);

            this.checkBoxRecoleccion.setEnabled(false);

            this.checkBoxTirillas.setEnabled(false);


        }
    }


    public void cantidadTotalBarrasTraza() {
        int cont=0;
        try {
        this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
        this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
        this.lstTrazasCliente = this.clienteSeleccionado.getJSONArray("lsttrazas");

        if(this.lstTrazasCliente.length()>0)
            {

                this.trazaSeleccionada =  this.lstTrazasCliente.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA",0));
                this.lstEmbalajesTraza =this.trazaSeleccionada.getJSONArray("lstembalaje");

                for (int i=0; i<this.lstEmbalajesTraza.length();i++)
                {
                    JSONObject aux= this.lstEmbalajesTraza.getJSONObject(i);
                    try {
                        JSONArray lis = aux.getJSONArray("barras_embalaje");
                        cont+=lis.length();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                this.trazaSeleccionada.put("cantTotal",cont);
            }

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("PLANNED_CLIENTS",this.clientesPlaneados.toString());
        editor.commit();

        this.barras_total_traza.setText(String.valueOf(cont));
        this.spinnerPuntoPesaje.setSelection(this.trazaSeleccionada.getInt("punto_pesaje"));
        this.spinnerCantidadRecoleccion.setSelection(this.trazaSeleccionada.getInt("cantidad_en_recoleccion"));
        this.spinnerPesoRecoleccion.setSelection(this.trazaSeleccionada.getInt("peso_en_recoleccion"));
        /*this.embalajeSeleccionado = this.lstEmbalajesTraza.getJSONObject(sharedpreferences.getInt("SELECT_EMBALAJE",0));
        */
        this.spinnerAptoCargue.setSelection(this.trazaSeleccionada.getInt("apto_cargue"));
        this.checkBoxTirillas.setChecked(this.trazaSeleccionada.getBoolean("check_tirillas"));
        this.checkBoxRecoleccion.setChecked(this.trazaSeleccionada.getBoolean("check_recoleccion"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void cantidadTotalPesosTraza(){
        float cont=0;
        try {

        this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
        this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
        this.lstTrazasCliente = this.clienteSeleccionado.getJSONArray("lsttrazas");

            if(this.lstTrazasCliente.length()>0)
            {
                this.trazaSeleccionada =  this.lstTrazasCliente.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA",0));
                this.lstEmbalajesTraza =this.trazaSeleccionada.getJSONArray("lstembalaje");

                for (int i=0; i<lstEmbalajesTraza.length();i++)
                {
                    JSONObject aux= lstEmbalajesTraza.getJSONObject(i);
                        cont+=aux.getDouble("pesoTotal");
                }
                this.trazaSeleccionada.put("pesoTotal",cont);
            }

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("PLANNED_CLIENTS",this.clientesPlaneados.toString());
        editor.commit();

        this.peso_total_traza.setText(String.valueOf(cont) + "KG");
        this.spinnerPuntoPesaje.setSelection(this.trazaSeleccionada.getInt("punto_pesaje"));
        this.spinnerCantidadRecoleccion.setSelection(this.trazaSeleccionada.getInt("cantidad_en_recoleccion"));
        this.spinnerPesoRecoleccion.setSelection(this.trazaSeleccionada.getInt("peso_en_recoleccion"));
        /*this.embalajeSeleccionado = this.lstEmbalajesTraza.getJSONObject(sharedpreferences.getInt("SELECT_EMBALAJE",0));
        */
        this.spinnerAptoCargue.setSelection(this.trazaSeleccionada.getInt("apto_cargue"));
        this.checkBoxTirillas.setChecked(this.trazaSeleccionada.getBoolean("check_tirillas"));
        this.checkBoxRecoleccion.setChecked(this.trazaSeleccionada.getBoolean("check_recoleccion"));

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }



    public void llenarSpinnerTrazas(){

        try {
            this.lstTrazasCliente = this.clienteSeleccionado.getJSONArray("lsttrazas");
            if(this.lstTrazasCliente.length()>0){
                String lstItemStringCategoria1 = getStringArrayList(this.lstTrazasCliente,1);
                lstItemsCategoria1 = new ArrayList<String>(Arrays.asList(lstItemStringCategoria1.substring(1, lstItemStringCategoria1.length() - 1).split(",")));
                lstItemsCategoria1 = cleanEmptyCharacter(lstItemsCategoria1);
                adapterItemsCategoria1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstItemsCategoria1);
                adapterItemsCategoria1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                this.spinnerTrazas.setAdapter(adapterItemsCategoria1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void llenarOtrosSpinner(){

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.puntoPesaje, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerPuntoPesaje.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(
                this, R.array.arraySiNo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerPesoRecoleccion.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(
                this, R.array.arraySiNo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerCantidadRecoleccion.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(
                this, R.array.arraySiNo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerAptoCargue.setAdapter(adapter);

        llenarSpinnerNoCargue ();

    }

    public void llenarSpinnerEmbalaje(){

        try {
            this.lstTrazasCliente = this.clienteSeleccionado.getJSONArray("lsttrazas");
            this.trazaSeleccionada =  this.lstTrazasCliente.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA",0));
            this.lstEmbalajesTraza =this.trazaSeleccionada.getJSONArray("lstembalaje");

            if(this.lstEmbalajesTraza.length()>0) {
                String lstItemStringCategoria1 = getStringArrayList(this.lstEmbalajesTraza,2);
                lstItemsCategoria2 = new ArrayList<String>(Arrays.asList(lstItemStringCategoria1.substring(1, lstItemStringCategoria1.length() - 1).split(",")));
                lstItemsCategoria2 = cleanEmptyCharacter(lstItemsCategoria2);
                adapterItemsCategoria2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstItemsCategoria2);
                adapterItemsCategoria2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                this.spinnerEmbalajes.setAdapter(adapterItemsCategoria2);
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

    public String getStringArrayList(JSONArray json, int tipo){
        ArrayList<String> aux = new ArrayList<String>();
        for(int i=0;i<json.length();i++){
            try {

                if(tipo==1)
                    aux.add(json.getJSONObject(i).getString("nombre")+"--"+json.getJSONObject(i).getString("traza"));
                else if(tipo==2)
                    aux.add(json.getJSONObject(i).getString("nombre"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return aux.toString();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        if(parent.getId()==this.spinnerTrazas.getId()){
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("SELECT_TRAZA",position);
            editor.commit();
            llenarSpinnerEmbalaje();
            cantidadTotalBarrasTraza();
            cantidadTotalPesosTraza();
        }

        else if(parent.getId()==this.spinnerPuntoPesaje.getId()){
            try{
                SharedPreferences.Editor editor = sharedpreferences.edit();
                this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
                this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
                this.lstTrazasCliente = this.clienteSeleccionado.getJSONArray("lsttrazas");
                this.trazaSeleccionada =  this.lstTrazasCliente.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA",0));
                this.trazaSeleccionada.put("punto_pesaje",this.spinnerPuntoPesaje.getSelectedItemPosition());
                editor.putString("PLANNED_CLIENTS",this.clientesPlaneados.toString());
                editor.commit();

            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        else if(parent.getId()==this.spinnerEmbalajes.getId()){
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("SELECT_EMBALAJE",position);
            editor.commit();
            cantidadTotalBarrasTraza();
            cantidadTotalPesosTraza();
        }
        else if(parent.getId()==this.spinnerCantidadRecoleccion.getId()){

            try{
                SharedPreferences.Editor editor = sharedpreferences.edit();
                this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
                this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
                this.lstTrazasCliente = this.clienteSeleccionado.getJSONArray("lsttrazas");
                this.trazaSeleccionada =  this.lstTrazasCliente.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA",0));
                this.trazaSeleccionada.put("cantidad_en_recoleccion",this.spinnerCantidadRecoleccion.getSelectedItemPosition());
                editor.putString("PLANNED_CLIENTS",this.clientesPlaneados.toString());
                editor.commit();

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        else if(parent.getId()==this.spinnerPesoRecoleccion.getId()){

            try{

                SharedPreferences.Editor editor = sharedpreferences.edit();
                this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
                this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
                this.lstTrazasCliente = this.clienteSeleccionado.getJSONArray("lsttrazas");
                this.trazaSeleccionada =  this.lstTrazasCliente.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA",0));
                this.trazaSeleccionada.put("peso_en_recoleccion",this.spinnerPesoRecoleccion.getSelectedItemPosition());
                editor.putString("PLANNED_CLIENTS",this.clientesPlaneados.toString());
                editor.commit();

            }catch (JSONException e){
                e.printStackTrace();
            }

        }

        else if(parent.getId()==this.spinnerCausalesAptoCargue.getId()){

            try{

                SharedPreferences.Editor editor = sharedpreferences.edit();
                this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
                this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
                this.lstTrazasCliente = this.clienteSeleccionado.getJSONArray("lsttrazas");
                this.trazaSeleccionada =  this.lstTrazasCliente.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA",0));
                this.trazaSeleccionada.put("causales_no_cargue",this.spinnerCausalesAptoCargue.getSelectedItem().toString());
                editor.putString("PLANNED_CLIENTS",this.clientesPlaneados.toString());
                editor.commit();

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        else if(parent.getId()==this.spinnerAptoCargue.getId()){
            try{
                SharedPreferences.Editor editor = sharedpreferences.edit();
                this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
                this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
                this.lstTrazasCliente = this.clienteSeleccionado.getJSONArray("lsttrazas");
                this.trazaSeleccionada =  this.lstTrazasCliente.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA", 0));
                this.trazaSeleccionada.put("apto_cargue", this.spinnerAptoCargue.getSelectedItemPosition());

                /* this.lstEmbalajesTraza =this.trazaSeleccionada.getJSONArray("lstembalaje");
                this.embalajeSeleccionado = this.lstEmbalajesTraza.getJSONObject(sharedpreferences.getInt("SELECT_EMBALAJE",0));
                this.embalajeSeleccionado.put("apto_cargue", this.spinnerAptoCargue.getSelectedItemPosition());
                ;*/

                editor.putString("PLANNED_CLIENTS",this.clientesPlaneados.toString());
                editor.commit();

                if(this.spinnerAptoCargue.getSelectedItemPosition()==0){
                    this.causalesDeNoCargue.setVisibility(View.GONE);
                }
                else if(this.spinnerAptoCargue.getSelectedItemPosition()==1){
                    this.causalesDeNoCargue.setVisibility(View.VISIBLE);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }



    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void agregarPesos(View view){
        Intent intent = new Intent(this, L_RegistrarPeso.class);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

           if(buttonView.getId()==R.id.checkBoxRecoleccion){

               SharedPreferences.Editor editor = sharedpreferences.edit();
               try {
                   this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
                   this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
                   this.lstTrazasCliente = this.clienteSeleccionado.getJSONArray("lsttrazas");
                   this.trazaSeleccionada =  this.lstTrazasCliente.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA", 0));
                   /*this.lstEmbalajesTraza =this.trazaSeleccionada.getJSONArray("lstembalaje");
                   this.embalajeSeleccionado = this.lstEmbalajesTraza.getJSONObject(sharedpreferences.getInt("SELECT_EMBALAJE",0));
                   */
                   this.trazaSeleccionada.put("check_recoleccion", isChecked);
                   editor.putString("PLANNED_CLIENTS",this.clientesPlaneados.toString());
                   editor.commit();
               } catch (JSONException e) {
                   e.printStackTrace();
               }


           }

        else if(buttonView.getId()==R.id.checkBoxTirillas){

               SharedPreferences.Editor editor = sharedpreferences.edit();
               try {
               this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
               this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
               this.lstTrazasCliente = this.clienteSeleccionado.getJSONArray("lsttrazas");
               this.trazaSeleccionada =  this.lstTrazasCliente.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA", 0));
               /*this.lstEmbalajesTraza =this.trazaSeleccionada.getJSONArray("lstembalaje");
               this.embalajeSeleccionado = this.lstEmbalajesTraza.getJSONObject(sharedpreferences.getInt("SELECT_EMBALAJE",0));
               */
               this.trazaSeleccionada.put("check_tirillas", isChecked);
               editor.putString("PLANNED_CLIENTS",this.clientesPlaneados.toString());
               editor.commit();
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }

    }

    @Override
    public void onClick(View v) {

        if(Metodo.equals("gestionarTrazas")){

            Intent intent = new Intent(this, E_MenuCiclo.class);
            startActivity(intent);

        }
        else {

            this.adb.setTitle(getResources().getString(R.string.alertMensaje));
            adb.setMessage("Desea actualizar la traza seleccionada");
            this.adb.setPositiveButton(
                    getResources().getString(R.string.confirm_button_1),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONArray auxjson;

                            try {
                                auxjson = new JSONArray(sharedpreferences.getString("TRUCK_INFO",null));
                                clientesPlaneados = new JSONArray(sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
                                clienteSeleccionado = clientesPlaneados.getJSONObject(sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));

                                String estadoNuevo = getEstadoSolicitud();
                                if(!estadoNuevo.equals("0")){
                                    clienteSeleccionado =clienteSeleccionado.put("estado",estadoNuevo);

                                    send_data_json = new JSONArray();
                                    JSONObject auxobject = new JSONObject();
                                    auxobject.put("fecha_hora_evento",Utilities.getDate());
                                    auxobject.put("metodo","json_tecni_actualizartraza");
                                    //auxobject.put("compactaciones",sharedpreferences.getInt("COMPACTIONS",0));
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putInt("CURRENT_STATE", 4);
                                    editor.putString("PLANNED_CLIENTS",clientesPlaneados.toString());
                                    editor.commit();
                                    send_data_json.put(auxobject);
                                    auxobject = new JSONObject();
                                    auxobject.put("operarios", new JSONArray(sharedpreferences.getString("SELECT_OPERATORS", "[]")));
                                    send_data_json.put(clienteSeleccionado);
                                    send_data_json.put(auxobject);
                                    send_data_json.put(auxjson.get(0));
                                    method="json_tecni_actualizartraza";
                                    methodInt="53";
                                    Utilities.sendInformation(myself,methodInt,method,send_data_json.toString());
                                    finish();
                                }
                                else
                                    dialog.dismiss();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
            this.adb.setNegativeButton(
                    getResources().getString(R.string.confirm_button_2),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            this.adb.show();

        }



    }

    public String getEstadoSolicitud() {

        boolean bandera = true;
        int bandera1=0;

        JSONArray lstResultados = new JSONArray();
        try {
            this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
            this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
            this.lstTrazasCliente = this.clienteSeleccionado.getJSONArray("lsttrazas");

            for (int i = 0; i < lstTrazasCliente.length(); i++) {

                this.trazaSeleccionada = this.lstTrazasCliente.getJSONObject(i);
                JSONObject aux = new JSONObject();
                aux.put("traza",this.trazaSeleccionada.getString("traza"));
                aux.put("peso_en_recoleccion",this.trazaSeleccionada.getInt("peso_en_recoleccion"));
                aux.put("cantidad_en_recoleccion",this.trazaSeleccionada.getInt("cantidad_en_recoleccion"));
                aux.put("resultado",revisionEmbalajes(this.trazaSeleccionada.getInt("peso_en_recoleccion"),
                        this.trazaSeleccionada.getInt("cantidad_en_recoleccion"),
                        this.trazaSeleccionada.getJSONArray("lstembalaje")));
                lstResultados.put(aux);
            }

            while(bandera && bandera1<lstResultados.length()){
                if(lstResultados.getJSONObject(bandera1).getBoolean("resultado"))
                    bandera1++;
                else
                    bandera= false;
            }

            if(bandera) {

                boolean banderaFinalPeso = true;
                boolean banderaFinalCantidad = true;
                int contadorFinal = 0;

                while (banderaFinalPeso && contadorFinal < lstResultados.length()) {

                    if (lstResultados.getJSONObject(contadorFinal).getInt("peso_en_recoleccion") == 0)
                        contadorFinal++;
                    else
                        banderaFinalPeso = false;
                }

                contadorFinal = 0;
                while (banderaFinalCantidad && contadorFinal < lstResultados.length()) {

                    if (lstResultados.getJSONObject(contadorFinal).getInt("cantidad_en_recoleccion") == 0)
                        contadorFinal++;
                    else
                        banderaFinalCantidad = false;
                }

                if (banderaFinalPeso && banderaFinalCantidad)
                    return "Atendida total";
                else if (banderaFinalPeso && !banderaFinalCantidad)
                    return "Atendida Con Peso (sin cantidad)";
                else if (!banderaFinalPeso && banderaFinalCantidad)
                    return "Atendida con Cantidad (sin peso)";
                else if (!banderaFinalPeso && !banderaFinalCantidad)
                    return "No Peso Ni Cantidad";
            }
            else{
                Utilities.showAlert(myself, "No registró peso ó codigos de barras. Por favor regístre el peso o cambie la opción en cantidad en recolección o peso en recolección. s"+"Verifique la trasa con codigo:"+ lstResultados.getJSONObject(bandera1).getString("traza"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public boolean revisionEmbalajes ( int pesoRecoleccion, int cantidadRecoleccion, JSONArray embalajes ){
        int cont=0;
        try {
            if(pesoRecoleccion == 0 && cantidadRecoleccion == 0)
            {
                cont=0;
                for(int i=0;i<embalajes.length();i++)
                {
                    JSONObject auxSelect = embalajes.getJSONObject(i);
                    if(auxSelect.getJSONArray("pesos_embalaje").length()>0 && auxSelect.getJSONArray("barras_embalaje").length()>0 )
                        cont++;
                }
                if(cont==embalajes.length())
                    return true;
                else
                    return false;
            }
            else if(pesoRecoleccion == 0 && cantidadRecoleccion == 1){
                cont=0;
                for(int i=0;i<embalajes.length();i++)
                {
                    JSONObject auxSelect = embalajes.getJSONObject(i);
                    if(auxSelect.getJSONArray("pesos_embalaje").length()>0)
                        cont++;
                }
                if(cont==embalajes.length())
                    return true;
                else
                    return false;
            }
            else if(pesoRecoleccion == 1 && cantidadRecoleccion == 0)  {
                cont=0;
                for(int i=0;i<embalajes.length();i++)
                {
                    JSONObject auxSelect = embalajes.getJSONObject(i);
                    if(auxSelect.getJSONArray("barras_embalaje").length()>0)
                        cont++;
                }
                if(cont==embalajes.length())
                    return true;
                else
                    return false;
            }
            else if(pesoRecoleccion == 1 && cantidadRecoleccion == 1)
                return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

}
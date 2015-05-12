package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import utilidades.Utilities;


public class M_CancelacionVisita extends Activity {



    private JSONArray clientesPlaneados;
    private SharedPreferences sharedpreferences;
    private Activity myself;
    private Spinner spinnerCausalesNoRecoleccion,spinnerClientes;
    private CheckBox checkBoxNoVisita;
    private EditText edtObservacionLogistico,edtObservacionCancelacion;
    private ArrayList<String> lstItemsCategoria1,lstItemsCategoria2;
    private ArrayAdapter<String> adapterItemsCategoria1,adapterItemsCategoria2;
    private JSONArray send_data_json;
    private JSONObject clienteSeleccionado;
    private String method;
    private String methodInt;
    private TextView date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m_cancelacion_visita);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.inicializarComponentes();
        this.myself=this;
    }

    public void inicializarComponentes(){

        this.date = (TextView)findViewById(R.id.dateNow);
        this.date.setText(this.sharedpreferences.getString("FECHA_SERVER", Utilities.getDate().split(" ")[0]));

        try {
            this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.spinnerCausalesNoRecoleccion= (Spinner)findViewById(R.id.spinnerCausalesNoRecoleccion);
        this.spinnerClientes= (Spinner)findViewById(R.id.spinnerClientes);
        this.checkBoxNoVisita = (CheckBox) findViewById(R.id.checkBoxNoVisita);
        this.checkBoxNoVisita.setChecked(true);
        this.edtObservacionCancelacion = (EditText) findViewById(R.id.edtObservacionCancelacion);
        this.edtObservacionLogistico = (EditText) findViewById(R.id.edtObservacionLogistico);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.arraySalesforce, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerClientes.setAdapter(adapter);

        llenarSpinnerClientes();
        llenarSpinnerCausalesDeNoRedoleccion();
    }


    public void llenarSpinnerClientes() {


        if (this.clientesPlaneados.length() > 0) {
            String lstItemStringCategoria1 = getStringArrayList(this.clientesPlaneados);
            lstItemsCategoria1 = new ArrayList<String>(Arrays.asList(lstItemStringCategoria1.substring(1, lstItemStringCategoria1.length() - 1).split(",")));
            lstItemsCategoria1 = cleanEmptyCharacter(lstItemsCategoria1);
            adapterItemsCategoria1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstItemsCategoria1);
            adapterItemsCategoria1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.spinnerClientes.setAdapter(adapterItemsCategoria1);


        }
    }


    public void llenarSpinnerCausalesDeNoRedoleccion() {


            String lstItemStringCategoria2 = sharedpreferences.getString("CAUSALES_RECOLECCION","[]");
            lstItemsCategoria2 = new ArrayList<String>(Arrays.asList(lstItemStringCategoria2.substring(1, lstItemStringCategoria2.length() - 1).split(",")));
            lstItemsCategoria2 = cleanEmptyCharacter(lstItemsCategoria2);
            adapterItemsCategoria2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstItemsCategoria2);
            adapterItemsCategoria2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.spinnerCausalesNoRecoleccion.setAdapter(adapterItemsCategoria2);

    }

    public void sendCancelarVisita(View view){

        if(this.checkBoxNoVisita.isChecked()){

            if(!this.edtObservacionLogistico.getText().toString().equals("") && !this.edtObservacionCancelacion.getText().toString().equals("")){



                JSONObject auxobject= new JSONObject();
                try {
                    clientesPlaneados = new JSONArray(sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
                    clienteSeleccionado = clientesPlaneados.getJSONObject(sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
                    clienteSeleccionado =clienteSeleccionado.put("estado","No Atendida");
                    clienteSeleccionado =clienteSeleccionado.put("observaciones_logistico",this.edtObservacionLogistico.getText().toString());
                    clienteSeleccionado =clienteSeleccionado.put("onservaciones_cancelacion",this.edtObservacionCancelacion.getText().toString());
                    clienteSeleccionado =clienteSeleccionado.put("causales_no_recoleccion",this.spinnerCausalesNoRecoleccion.getSelectedItem().toString());

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("PLANNED_CLIENTS",clientesPlaneados.toString());
                    editor.commit();
                    send_data_json = new JSONArray();
                    auxobject.put("fecha_hora_evento", Utilities.getDate());
                    auxobject.put("metodo","json_tecni_cancelar_visita");
                    auxobject.put("usuario",sharedpreferences.getString("USER_ID", "No reporto"));
                    send_data_json.put(auxobject);
                    send_data_json.put(clienteSeleccionado);
                    methodInt="52";
                    method="json_tecni_cancelar_visita";
                    Utilities.sendInformation(myself,methodInt,method,send_data_json.toString());
                    finish();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }

            else{

                Toast.makeText(this,"los campos de observaciones son requeridos.",Toast.LENGTH_LONG).show();

            }

        }
        else{

            Toast.makeText(this,"El campo tipo checkbox, no se puede realizar la visita es requerido.",Toast.LENGTH_LONG).show();

        }


    }

    public String getStringArrayList(JSONArray json){
        ArrayList<String> aux = new ArrayList<String>();
        for(int i=0;i<json.length();i++){
            try {

                    aux.add(json.getJSONObject(i).getString("nombre_cliente")+"\n"+json.getJSONObject(i).getString("solicitud"));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return aux.toString();
    }

    public ArrayList<String> cleanEmptyCharacter(ArrayList<String> list){
        for (int i=0;i<list.size();i++)
            list.set(i, list.get(i).trim());
        return list;
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

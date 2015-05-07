package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;


public class M_CancelacionVisita extends Activity {



    private JSONArray clientesPlaneados;
    private SharedPreferences sharedpreferences;
    private Activity myself;
    private Spinner spinnerCausalesNoRecoleccion,spinnerClientes;
    private CheckBox checkBox;
    private EditText edtObservacionLogistico,edtObservacionCancelacion;
    private ArrayList<String> lstItemsCategoria1,lstItemsCategoria2;
    private ArrayAdapter<String> adapterItemsCategoria1,adapterItemsCategoria2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m_cancelacion_visita);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.inicializarComponentes();
        this.myself=this;
    }

    public void inicializarComponentes(){

        try {
            this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.spinnerCausalesNoRecoleccion= (Spinner)findViewById(R.id.spinnerCausalesNoRecoleccion);
        this.spinnerClientes= (Spinner)findViewById(R.id.spinnerClientes);
        this.checkBox = (CheckBox) findViewById(R.id.checkBox);
        this.edtObservacionCancelacion = (EditText) findViewById(R.id.edtObservacionCancelacion);
        this.edtObservacionLogistico = (EditText) findViewById(R.id.edtObservacionLogistico);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.arraySalesforce, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerClientes.setAdapter(adapter);

        llenarSpinnerClientes();
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

}

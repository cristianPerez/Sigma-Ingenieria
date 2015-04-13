package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import utilidades.SaveInformation;
import utilidades.Utilities;


public class C_GrupoTrabajo extends Activity implements AdapterView.OnItemLongClickListener {

    private JSONArray operariosSeleccionados,operariosTotales;
    private JSONObject operarioSeleccionado;
    private SharedPreferences sharedpreferences;
    private AlertDialog.Builder adb;
    private ListView lstOperadoresSeleccionados;
    private JSONArray send_data_json;
    private String method;
    private String methodInt;
    private TextView date;
    private Button empezarJornada,agregarOperario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_grupo_trabajo);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        identifyElements();
        configurarLista();

    }


    /**
     * Method that will activate when the next activity responds
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (resultCode == 2) {
                configurarLista();
            }
            else if(resultCode == 4){
                configurarLista();
            }
        }
    }

    public void configurarLista(){

        try {
            this.operariosTotales = new JSONArray(this.sharedpreferences.getString("OPERATORS", "[]"));
            this.operariosSeleccionados = new JSONArray(this.sharedpreferences.getString("SELECT_OPERATORS","[]"));

            if(operariosSeleccionados.length()==0){
                operariosSeleccionados = new JSONArray(sharedpreferences.getString("SELECT_OPERATORS","[]"));
                displayOperadoresSeleccionados(operariosSeleccionados);
                adb.setTitle("Alerta");
                adb.setMessage("No tienes ningun operario asignado, quieres agregar alguno?");
                adb.setPositiveButton(
                        getResources().getString(R.string.confirm_button_1),
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(),C_SeleccionarOperarios.class);
                                startActivityForResult(intent,10);
                            }
                        });
                adb.setNegativeButton(
                        getResources().getString(R.string.confirm_button_2),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();

                                try {
                                    operariosSeleccionados = new JSONArray(sharedpreferences.getString("SELECT_OPERATORS","[]"));
                                    displayOperadoresSeleccionados(operariosSeleccionados);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                adb.show();
            }
            else{

                displayOperadoresSeleccionados(this.operariosSeleccionados);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void identifyElements(){

        this.adb = new AlertDialog.Builder(this);
        this.lstOperadoresSeleccionados = (ListView) findViewById(R.id.listViewOperariosSeleccionados);
        this.lstOperadoresSeleccionados.setOnItemLongClickListener(this);
        this.date = (TextView)findViewById(R.id.dateNow);
        this.date.setText(this.sharedpreferences.getString("DATE", Utilities.getDate().split(" ")[0]));
        this.agregarOperario = (Button) findViewById(R.id.agregarOperario);
        this.empezarJornada = (Button) findViewById(R.id.empezarJornada);
        this.date = (TextView)findViewById(R.id.dateNow);
        this.date.setText(this.sharedpreferences.getString("FECHA_SERVER", Utilities.getDate().split(" ")[0]));

        if(sharedpreferences.getInt("EMPEZO_JORNADA",0)==1){

            blockElements();

        }


    }

    public void blockElements(){

        this.agregarOperario.setBackgroundColor(getResources().getColor(R.color.gray));
        this.agregarOperario.setEnabled(false);
        this.empezarJornada.setBackgroundColor(getResources().getColor(R.color.gray));
        this.empezarJornada.setEnabled(false);

    }

    public void agregarOperarios(View view){

        Intent intent = new Intent(getApplicationContext(),C_SeleccionarOperarios.class);
        startActivityForResult(intent, 10);


    }



    public void verAyuda(View view){

        Utilities.showAlert(this,getResources().getString(R.string.alert_info_remover));

    }


    public void back(View view){

        this.finish();

    }



    /**
     * Method to save the name of the planned routes in arrayList and display this in the listView
     * @param operarios
     */
    private void displayOperadoresSeleccionados(JSONArray operarios){
        ArrayList<String> listRouteNames = new ArrayList<String>();
        //this.plannedRoutesActive = new JSONArray();
        for(int i=0; i<operarios.length(); i++){
            try {
                listRouteNames.add(operarios.getJSONObject(i).getString("nombre"));

            } catch (JSONException e) {
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listRouteNames);
        this.lstOperadoresSeleccionados.setAdapter(adapter);
    }



    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        final int posicion=position;


        try {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Alerta!");

            adb.setMessage(getResources().getString(R.string.alert_info_remover_instruccions) + " " + this.operariosSeleccionados.getJSONObject(position).getString("nombre"));

            adb.setPositiveButton(
                    getResources().getString(R.string.confirm_button_1),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            try {
                                if (sharedpreferences.getInt("EMPEZO_JORNADA", 0) == 1) {






                                } else {

                                    operariosTotales.put(operariosSeleccionados.getJSONObject(posicion));
                                    operariosSeleccionados = Utilities.delete(operariosSeleccionados, posicion);

                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("OPERATORS", operariosTotales.toString());
                                    editor.putString("SELECT_OPERATORS", operariosSeleccionados.toString());
                                    editor.commit();
                                    configurarLista();

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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Method for ask about if really want to save the selected operators, if the answer
     * is positive call the next method to save the selected operators
     *
     * @param v
     */
    public void start(View v) {
        if (this.operariosSeleccionados.length() > 0) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Alerta !");
            adb.setMessage("¿Realmente desea iniciar la jornada de los operarios seleccionados?");
            adb.setPositiveButton(
                    getResources().getString(R.string.confirm_button_1),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveSelectedOperators();
                            blockElements();
                            finish();
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
        } else {
            Utilities.showAlert(this, "Seleccione operarios para empezar su jornada de trabajo");
        }

    }

    /**
     * Method to change of list the operators or finish the journey of someone
     *
     */
    public void bajarOperario(int position) {

        //falta

    }

    /**
     * Read the description of the previous method
     */
    private void saveSelectedOperators() {
        send_data_json = new JSONArray();
        JSONArray truckInfo;
        JSONObject aux = new JSONObject();
        String hour = Utilities.getDate();
        for (int i = 0; i < this.operariosSeleccionados.length(); i++) {
            try {
                this.operariosSeleccionados.getJSONObject(i).put("hora_inicio", hour);
                this.operariosSeleccionados.getJSONObject(i).put("hora_fin", "");
            } catch (JSONException e) {
            }
        }
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("SELECT_OPERATORS", operariosSeleccionados.toString());
        editor.putInt("EMPEZO_JORNADA", 1);
        editor.commit();

        try {
            truckInfo = new JSONArray(sharedpreferences.getString("TRUCK_INFO", "[]"));
            aux.put("operarios_seleccionados", operariosSeleccionados);
            aux.put("informacion_vehiculo", truckInfo);
            send_data_json.put(aux);

            method = "json_tecni_operarios";
            methodInt = "42";

        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendInformation();
    }

    public void showALert(String mensaje){

        Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show();

    }

    /**
     *Method that send the information to server, from whatever method
     */
    public void sendInformation(){

        try {
            new SaveInformation(this).execute("http://www.concesionesdeaseo.com/gruposala/FUNEventosMovil/Eventos",
                    this.methodInt,
                    this.method,
                    this.send_data_json.toString());
        } catch (Exception e) {
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

                            auxobject.put("fecha_hora_evento",Utilities.getDate());
                            auxobject.put("metodo","cerrar_sesion");
                            auxobject.put("usuario",sharedpreferences.getString("USER_ID", "14880479"));
                            send_data_json.put(auxobject);
                            send_data_json.put(auxjson.get(0));
                            methodInt="14";
                            method="cerrar_sesion";
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
}

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
    private SharedPreferences sharedpreferences;
    private AlertDialog.Builder adb;
    private ListView lstOperadoresSeleccionados;
    private JSONArray send_data_json;
    private String method;
    private String methodInt;
    private TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_grupo_trabajo);this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
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
            this.operariosSeleccionados = new JSONArray(this.sharedpreferences.getString("OPERATORS_SELECT","[]"));

            if(operariosSeleccionados.length()==0){
                adb.setTitle("Alerta");
                adb.setMessage("No tienes ningun operario asignado, quieres agregar alguno?");
                adb.setPositiveButton(
                        getResources().getString(R.string.confirm_button_1),
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(),C_SeleccionarOperadores.class);
                                intent.putExtra("Metodo",1);
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


    }

    public void agregarOperarios(View view){


        Intent intent = new Intent(getApplicationContext(),C_SeleccionarOperadores.class);
        intent.putExtra("Metodo",1);
        startActivityForResult(intent, 10);


    }

    public void quitarOperarios(View view){

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Alerta");
        adb.setMessage("Seguro que quiere bajar al operario");
        adb.setPositiveButton(
                getResources().getString(R.string.accept_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        adb.show();
    }


    public void back(View view){

        this.finish();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

            adb.setMessage("Desea remover al operario: "+ this.operariosSeleccionados.getJSONObject(position).getString("nombre"));

            adb.setPositiveButton(
                    getResources().getString(R.string.confirm_button_1),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
/*
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            try {

                                rutasAsignadas = new JSONArray(sharedpreferences.getString("ASIGNED_ROUTES_BARRIDO" , "[]"));
                                JSONObject aux = rutasAsignadas.getJSONObject(sharedpreferences.getInt("SELECT_ROUTE_BARRIDO", 0)).getJSONArray("operarios_asignados").getJSONObject(posicion);
                                operariosSeleccionados = Utilities.delete(rutasAsignadas.getJSONObject(sharedpreferences.getInt("SELECT_ROUTE_BARRIDO", 0)).getJSONArray("operarios_asignados"),posicion);
                                rutasAsignadas.getJSONObject(sharedpreferences.getInt("SELECT_ROUTE_BARRIDO", 0)).put("operarios_asignados",operariosSeleccionados);
                                operariosTotales = new JSONArray(sharedpreferences.getString("OPERATORS" , "[]"));
                                operariosTotales.put(aux);
                                editor.putString("OPERATORS", operariosTotales.toString());
                                editor.putString("ASIGNED_ROUTES_BARRIDO", rutasAsignadas.toString());
                                editor.commit();
                                displayOperadoresSeleccionados(operariosSeleccionados);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }*/
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
     * Method to register when the truck arrive in the base, finally register in the server the route list.
     * @param v
     */
    public void guardarOperariosSeleccionados(View v){
/*
        if(this.operariosSeleccionados.length()>0){

            this.adb.setTitle(getResources().getString(R.string.titulo_alerta));
            this.adb.setMessage(getResources().getString(R.string.alert_guardar));
            this.adb.setPositiveButton(getResources().getString(R.string.confirm_button_1),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            JSONObject auxobject= new JSONObject();
                            JSONArray auxjson;
                            try {
                                auxjson =  new JSONArray(sharedpreferences.getString("ASIGNED_ROUTES_BARRIDO","[]").toString());

                                send_data_json = new JSONArray();

                                auxobject.put("fecha_hora_evento", Utilities.getDate());
                                auxobject.put("metodo","operarios_seleccionados");
                                auxobject.put("supervisor",sharedpreferences.getString("USER_ID","xxx"));


                                send_data_json.put(auxobject);
                                send_data_json.put(auxjson.getJSONObject(sharedpreferences.getInt("SELECT_ROUTE_BARRIDO",0)));
                                methodInt="27";
                                method="guardar_gestion_barrido";

                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("ASIGNED_ROUTES_BARRIDO", auxjson.toString());
                                editor.commit();

                                sendInformation();

                                showALert("los operarios asignados fueron guardados correctamente");

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });
            this.adb.setNegativeButton(getResources().getString(R.string.confirm_button_2),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            this.adb.show();

        }
        else{
            Toast.makeText(getApplication().getBaseContext(), getResources().getString(R.string.alert_sin_seleccionados), Toast.LENGTH_LONG).show();
        }
*/
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
}

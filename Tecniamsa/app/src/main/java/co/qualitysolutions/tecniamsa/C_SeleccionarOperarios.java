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
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import utilidades.Utilities;


public class C_SeleccionarOperarios extends Activity implements SearchView.OnQueryTextListener,AdapterView.OnItemClickListener {

    private JSONArray operadoresTotales,displayedListOperators,operariosSeleccionados;
    private SharedPreferences sharedpreferences;
    private AlertDialog.Builder adb;
    private ListView listaOperarios;
    private SearchView busqueda;
    private TextView date;
    private boolean flagCollection;
    private JSONObject operadorSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_seleccionar_operarios);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.flagCollection = this.sharedpreferences.getBoolean("FLAG_COLLECTION",false);
        identifyElements();
        diaplayListAllOperators();
    }

    public void back(View view){

        this.finish();

    }

    public void identifyElements(){

        this.date = (TextView)findViewById(R.id.dateNow);
        this.date.setText(this.sharedpreferences.getString("DATE", Utilities.getDate().split(" ")[0]));
        this.adb = new AlertDialog.Builder(this);
        this.listaOperarios = (ListView) findViewById(R.id.listViewOperadores);
        this.listaOperarios.setOnItemClickListener(this);
        this.busqueda = (SearchView) findViewById(R.id.searchView);
        this.busqueda.setOnQueryTextListener(this);

        this.displayedListOperators = new JSONArray();
        this.operariosSeleccionados = new JSONArray();
        try {
             this.operadoresTotales = new JSONArray(this.sharedpreferences.getString("OPERATORS", "[]"));
             this.operariosSeleccionados = new JSONArray(this.sharedpreferences.getString("SELECT_OPERATORS", "[]"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void diaplayListAllOperators(){
        if(this.displayedListOperators.length()==0)
            this.displayedListOperators=this.operadoresTotales;
        ArrayList<String> listRouteNames = new ArrayList<String>();
        for (int i = 0; i < this.displayedListOperators.length(); i++) {
            try {
                listRouteNames.add(this.displayedListOperators.getJSONObject(i).getString("nombre"));
            } catch (JSONException e) {
            }

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listRouteNames);
        this.listaOperarios.setAdapter(adapter);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        this.displayedListOperators = new JSONArray();
        for(int i=0; i<this.operadoresTotales.length(); i++){
            try {
                if(this.operadoresTotales.getJSONObject(i).getString("nombre").toUpperCase().contains((newText.toString().toUpperCase()))){
                    this.displayedListOperators.put(this.operadoresTotales.getJSONObject(i));
                }
            } catch (JSONException e) {
            }
        }
        this.diaplayListAllOperators();
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int posicion = position;

        adb.setTitle("Alerta");
        adb.setMessage("Seguro que desea agragar este operario?");
        adb.setPositiveButton(
                getResources().getString(R.string.confirm_button_1),
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(seleccionarOperario(posicion)){

                            Intent intent = new Intent();
                            setResult(4, intent);
                            finish();
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

    public boolean seleccionarOperario(int position){

            try {
                operadorSeleccionado = new JSONObject();
                int i=0;
                while(i < operadoresTotales.length() && !operadoresTotales.getJSONObject(i).getString("nombre").equals(displayedListOperators.getJSONObject(position).getString("nombre"))){
                    i++;
                }
                if(i<operadoresTotales.length()){
                    operadorSeleccionado   = operadoresTotales.getJSONObject(i);
                    operariosSeleccionados.put(operadorSeleccionado);
                    operadoresTotales = Utilities.delete(operadoresTotales, i);
                }
                displayedListOperators = Utilities.delete(displayedListOperators, position);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("SELECT_OPERATORS",operariosSeleccionados.toString());
                editor.putString("OPERATORS",operadoresTotales.toString());
                editor.commit();
                return true;

            } catch (JSONException e) {
                return false;
            }
    }

    /**
     * Method to close the session
     *
     * @param v
     */
    public void logOut(View v) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getResources().getString(R.string.cerrarSesion));
        adb.setPositiveButton(
                getResources().getString(R.string.confirm_button_1),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(),A_Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
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

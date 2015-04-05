package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnQueryTextListener;

import java.util.ArrayList;


public class F_Seleccionar_cliente extends Activity implements OnQueryTextListener,OnItemClickListener {

    private JSONArray clientesPlaneados,clientesVisualizados;
    private SharedPreferences sharedpreferences;
    private ListView lstClientes;
    private SearchView busqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f_seleccionar_cliente);
        inicializarComponentes();
    }

    public void inicializarComponentes(){

        this.lstClientes = (ListView) findViewById(R.id.listViewCLientes);
        this.lstClientes.setOnItemClickListener(this);
        this.busqueda = (SearchView) findViewById(R.id.searchView3);
        this.busqueda.setOnQueryTextListener(this);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.clientesVisualizados = new JSONArray();
        try {
            this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]").toString());
            this.mostrarCLientes();
        } catch (JSONException e) {
            Toast.makeText(this, "NO hay clientes planeados", Toast.LENGTH_SHORT).show();
        }

    }
    /**
     * Method to save the name of the planned routes in arrayList and display this in the listView
     *
     */
    private void mostrarCLientes(){

        if(this.clientesVisualizados.length()==0)
            this.clientesVisualizados=this.clientesPlaneados;

        ArrayList<String> listRouteNames = new ArrayList<String>();
        for(int i=0; i<this.clientesVisualizados.length(); i++){
            try {
                listRouteNames.add(this.clientesVisualizados.getJSONObject(i).getString("nombre_cliente"));

            } catch (JSONException e) {
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listRouteNames);
        this.lstClientes.setAdapter(adapter);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("CLIENTE_SELECCIONADO",position);
        editor.commit();
        Intent intent = new Intent(this, F_Datos_cliente.class);
        startActivity(intent);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        this.clientesVisualizados = new JSONArray();
        for(int i=0; i<this.clientesPlaneados.length(); i++){
            try {
                if(this.clientesPlaneados.getJSONObject(i).getString("nombre_cliente").toUpperCase().contains((newText.toString().toUpperCase()))){
                    this.clientesVisualizados.put(this.clientesPlaneados.getJSONObject(i));
                }
            } catch (JSONException e) {
            }
        }
        this.mostrarCLientes();
        return false;

    }
}

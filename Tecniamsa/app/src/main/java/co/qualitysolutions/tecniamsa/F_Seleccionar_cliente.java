package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnQueryTextListener;

import java.util.ArrayList;

import utilidades.SaveInformation;
import utilidades.Utilities;
import utilidades.WebService;


public class F_Seleccionar_cliente extends Activity implements OnQueryTextListener,OnItemClickListener {

    private JSONArray clientesPlaneados,clientesVisualizados;
    private SharedPreferences sharedpreferences;
    private ListView lstClientes;
    private SearchView busqueda;
    private String method;
    private String methodInt;
    private TextView date;
    private JSONArray send_data_json;
    private Activity myself;
    private ProgressDialog progress;
    private String Metodo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f_seleccionar_cliente);
        this.myself=this;
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.Metodo = getIntent().getExtras().getString("Metodo");
        inicializarComponentes();
    }

    public void inicializarComponentes(){

        this.date = (TextView)findViewById(R.id.dateNow);
        this.date.setText(this.sharedpreferences.getString("FECHA_SERVER", Utilities.getDate().split(" ")[0]));
        this.lstClientes = (ListView) findViewById(R.id.listViewCLientes);
        this.lstClientes.setOnItemClickListener(this);
        this.busqueda = (SearchView) findViewById(R.id.searchView3);
        this.busqueda.setOnQueryTextListener(this);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.clientesVisualizados = new JSONArray();
        this.progress = new ProgressDialog(this);
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

                    listRouteNames.add(this.clientesVisualizados.getJSONObject(i).getString("nombre_cliente")+ " -- " + this.clientesVisualizados.getJSONObject(i).getString("solicitud") +"--\n" +this.clientesVisualizados.getJSONObject(i).getString("estado").toString());

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




            try {
                if(this.clientesPlaneados.getJSONObject(position).getString("estado").equals("Atendida total")){
                    Toast.makeText(this,"El cliente ya fue atendido !",Toast.LENGTH_LONG).show();
                }
                else if(this.clientesPlaneados.getJSONObject(position).getString("estado").equals("No Atendida")){
                    Toast.makeText(this,"El cliente no pudo ser atendido y su solicitud fue cancelada !",Toast.LENGTH_LONG).show();
                }
                else{

                    if(Metodo.equals("gestionarTrazas")) {


                         if(this.clientesPlaneados.getJSONObject(position).getString("estado").equals("inactiva")){

                            Intent intent = new Intent(this, F_Datos_cliente.class);
                            startActivityForResult(intent, 10);

                        }
                        else{

                            Toast.makeText(this,"El cliente ya fue atendido.!",Toast.LENGTH_LONG).show();

                        }


                    }
                    else  if(Metodo.equals("actualizarTrazas")){

                        if(this.clientesPlaneados.getJSONObject(position).getString("estado").equals("inactiva")){
                            Toast.makeText(this,"Debes atender esta traza por la opción de menu inicio recolección !",Toast.LENGTH_LONG).show();
                        }

                        else{
                            Intent intent = new Intent(this, G_TrazasEmbalaje.class);
                            intent.putExtra("Metodo","actualizarTrazas");
                            startActivityForResult(intent, 10);

                        }


                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (resultCode == 2) {
                Intent intent = new Intent();
                setResult(2, intent);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

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

    public void actualizarClientes(View view){


        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Alerta");
        adb.setMessage("Desea actualizar las ordenes?");
        adb.setPositiveButton(
                getResources().getString(R.string.confirm_button_1),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            JSONArray truck = new JSONArray(sharedpreferences.getString("TRUCK_INFO","[]"));

                            new ConsultarInformacion(myself).execute("http://www.concesionesdeaseo.com/pruebas/FUNLoginTecniamsa/Login2?ciudad="+sharedpreferences.getString("CITY","")+"&vehiculo="+truck.getJSONObject(0).getString("placa").replace(" ","%20"),
                                    sharedpreferences.getString("USER_ID",""),
                                    sharedpreferences.getString("PASSWORD",""));

                            /*new ConsultarInformacion(myself).execute("http://www.concesionesdeaseo.com/gruposala/FUNLoginTecniamsa/Login2?ciudad="+sharedpreferences.getString("CITY","")+"&vehiculo="+truck.getJSONObject(0).getString("placa").replace(" ","%20"),
                                    sharedpreferences.getString("USER_ID",""),
                                    sharedpreferences.getString("PASSWORD",""));*/

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
    public class ConsultarInformacion extends AsyncTask<String, Void, Void> {

        private WebService connection;
        private Activity activity;
        private SharedPreferences sharedpreferences;
        private JSONArray nuevasSolicitudes= new JSONArray();
        private int lengthClientesPlaneados;

    public  ConsultarInformacion(Activity activity) {
        this.connection = new WebService();
        this.activity = activity;
        this.sharedpreferences = activity.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.lengthClientesPlaneados=clientesPlaneados.length();
    }

    @Override
    protected void onPreExecute() {
        progress.setTitle("Descargando");
        progress.setMessage("Obteniendo nuevas solicitudes");
        progress.setCancelable(true);
        progress.show();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {
        JSONArray answer;

        if (thereIsInternet()) {
            //Send current event
            this.connection.setUrl(params[0]);
            String[] parameters = { "login",params[1], params[2]};
            answer = this.connection.conectar(parameters);
            try {
                if (answer.getJSONObject(0).getJSONArray("lstdatos_cliente").length()>0) {

                    nuevasSolicitudes = answer.getJSONObject(0).getJSONArray("lstdatos_cliente");
                    compararSolicitudes(nuevasSolicitudes);

                } else {
                        Utilities.showAlert(myself,"No hay solicitudes nuevas planeadas");
                    }
            } catch (JSONException e) {
            }
        } else {
            Toast.makeText(activity, "No tienes coneccion a internet!", Toast.LENGTH_LONG).show();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void result) {
        progress.dismiss();

        if(clientesPlaneados.length()==lengthClientesPlaneados)
            Toast.makeText(activity,"No hay Nuevas solicitudes !",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(activity,"Se agregaron nuevas solicitudes !",Toast.LENGTH_LONG).show();
        mostrarCLientes();

    }

        public void compararSolicitudes(JSONArray nuevasSolicitudes){

            JSONArray planeadosActuales = clientesPlaneados;
            JSONObject objetoNuevo=new JSONObject();
            JSONObject objetoViejo=new JSONObject();
            int bandera=0;

            for(int i=0;i<nuevasSolicitudes.length();i++){
                try {
                    bandera=0;
                    objetoNuevo = nuevasSolicitudes.getJSONObject(i);

                for(int j=0;j<planeadosActuales.length();j++){

                    try {
                        objetoViejo = planeadosActuales.getJSONObject(i);
                        if(objetoNuevo.getString("solicitud").equals(objetoViejo.getString("solicitud")))
                        bandera=1;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(bandera==0){
                    objetoNuevo.put("estado", "inactiva");
                    objetoNuevo.put("tipo", "planeada");
                    objetoNuevo.put("hora_llegada_sitio_entrega", "0");
                    clientesPlaneados.put(Utilities.inicializarCliente(objetoNuevo));
                }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("PLANNED_CLIENTS",clientesPlaneados.toString());
            editor.commit();
        }




}

    private boolean thereIsInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }

}

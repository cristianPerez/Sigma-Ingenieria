package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import utilidades.Utilities;


public class A_LoginSecond extends Activity implements AdapterView.OnItemClickListener {

    private SharedPreferences sharedpreferences;
    private ListView listTruck;
    private JSONArray jsonListTruck;
    private TextView placa,numeroInterno;
    private AlertDialog.Builder adb;
    private ProgressDialog progress;
    private utilidades.WebService conection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.a_login_second);
        this.progress = new ProgressDialog(this);
        this.conection = new utilidades.WebService();
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.placa = (TextView) findViewById(R.id.placa);
        this.numeroInterno = (TextView) findViewById(R.id.numeroInterno);
        this.listTruck = (ListView) findViewById(R.id.ListTruck);
        this.listTruck.setOnItemClickListener(this);
        this.adb = new AlertDialog.Builder(this);
        try {
            this.jsonListTruck = new JSONArray(this.sharedpreferences.getString("TRUCK_LIST","[]"));
            diaplayListAllTrucks();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void showAlert(String message){

        Utilities.showAlert(this,message);

    }


    private void diaplayListAllTrucks(){
        List<String> lstNames = new ArrayList<String>();
        for(int i=0; i<this.jsonListTruck.length(); i++){
            try {
                lstNames.add(this.jsonListTruck.getJSONObject(i).getString("numero_interno2"));
            } catch (JSONException e) {
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstNames);
        this.listTruck.setAdapter(adapter);
    }

    /**
     * Override method to hide the keyboard when the user touch outside of an
     * element
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    public void startSession(View v){
        if(!this.placa.getText().toString().equals("XXX-XXX") && !this.numeroInterno.getText().toString().equals("XX-XX")){
            adb.setTitle(getResources().getString(R.string.alertMensaje));
            adb.setMessage(getResources().getString(R.string.placaConfirm)+" "+this.placa.getText().toString() +" "+getResources().getString(R.string.numeroInternoConfirm) +" "+this.numeroInterno.getText().toString());
            adb.setPositiveButton(
                    getResources().getString(R.string.confirm_button_1),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("PLACA",placa.getText().toString());
                            editor.putString("NUMERO_INTERNO",numeroInterno.getText().toString());
                            editor.commit();
                            new Login().execute(sharedpreferences.getString("USER_ID",""),sharedpreferences.getString("PASSWORD",""),sharedpreferences.getString("CITY",""),sharedpreferences.getString("PLACA","").replace(" ","%20"));
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
            Utilities.showAlert(this, "Seleccione un vehículo");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        try {
            this.placa.setText(this.jsonListTruck.getJSONObject(position).getString("placa2").toString());
            this.numeroInterno.setText(this.jsonListTruck.getJSONObject(position).getString("numero_interno2").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class Login extends AsyncTask<String, Void, Boolean> {

        private JSONArray answer;

        @Override
        protected void onPreExecute() {
            progress.setTitle(getResources().getString(R.string.titleProgress));
            progress.setMessage(getResources().getString(R.string.messageProgress));
            progress.setCancelable(true);
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if(params.length==4){
                String[] parameters = {"login_1",params[0],params[1]};
                conection.setUrl("http://www.concesionesdeaseo.com/pruebas/FUNLoginTecniamsa/Login2?ciudad="+params[2]+"&vehiculo="+params[3]);
                this.answer = conection.conectar(parameters);
                try {
                    if(this.answer.getJSONObject(0).getJSONArray("lstdatos_cliente").length()>0){
                        if(this.saveInformation()){
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putInt("LOGIN_OK",1);
                            editor.commit();
                            return true;
                        }
                        else{
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putInt("LOGIN_ERROR",1); //ERROR PARA CUANDO NO GUARDA LA INFORMACION
                            editor.commit();
                            return false;
                        }
                    }
                    else{
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putInt("LOGIN_ERROR",0); //ERROR PARA CUANDO NO HAY CLIETES PLANEADOS
                        editor.commit();
                        return false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        public Login() {
            super();
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result){

                if(Utilities.compareDates(sharedpreferences.getString("FECHA_SERVER","10/10/10"),sharedpreferences.getString("HORA_SERVER","10:10"))){
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean("SERVER_SYNC", true);
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), B_MenuPrincipal.class);
                    startActivity(intent);
                    }
                    else{
                        adb.setCancelable(false);
                        adb.setTitle("Alerta!");
                        adb.setMessage("Usted debe configurar el celular para que tome la hora adecuada desde internet, o hacerlo manualmente esta es la informacion traida desde el servidor FECHA : " + sharedpreferences.getString("FECHA_SERVER","10/10/10") +" HORA: " + sharedpreferences.getString("HORA_SERVER","10:10") +"ir a ello ->");
                        adb.setPositiveButton(getResources().getString(R.string.accept_button),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                                    }
                                });
                        adb.show();
                        }
            }
            else{
                int loginError = sharedpreferences.getInt("LOGIN_ERROR",-1);
                if(loginError != -1){
                    if(loginError == 0)
                        showAlert("No tiene clientes planeados");
                    else if(loginError == 1)
                        showAlert("Ocurrio un error guardando la informacion, consulta con el proveedor de esta aplicación.");
                }
            }
            progress.dismiss();

        }

        /**
         * This method gets and saves the information that return the server about the necessary data for start the
         * application
         * @return
         */
        private Boolean saveInformation(){

            try {

                String token = this.answer.getJSONObject(0).getString("token");
                String fecha = this.answer.getJSONObject(0).getString("fecha");
                String hora = this.answer.getJSONObject(0).getString("hora");
                String user = sharedpreferences.getString("USER_ID", "xxx");

                //String url = this.answer.getJSONObject(0).getString("url");
                //JSONArray alternateRoutes = this.answer.getJSONObject(0).getJSONArray("rutas_alternas");

                JSONArray listaClientes = Utilities.inicializarClientesPlaneados(this.answer.getJSONObject(0).getJSONArray("lstdatos_cliente"));
                JSONArray operators = this.answer.getJSONObject(0).getJSONArray("lstoperarios");
                JSONArray truckInformation = this.answer.getJSONObject(0).getJSONArray("lstvehiculos");
                truckInformation.getJSONObject(0).put("cedula_conductor",user);
                JSONArray casetas1 =new JSONArray(this.answer.getJSONObject(0).getString("lstcanastas_activas"));
                JSONObject casetas = new JSONObject(casetas1.get(0).toString());
                JSONArray casetas2 = casetas.getJSONArray("caseta_peaje");



                if(listaClientes.length()>0){

                    for(int i=0; i<listaClientes.length(); i++){
                        listaClientes.getJSONObject(i).put("estado", "inactiva");
                        listaClientes.getJSONObject(i).put("tipo", "planeada");
                    }

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("TOKEN", token);
                    editor.putString("FECHA_SERVER",fecha);
                    editor.putString("HORA_SERVER",hora);
                    //editor.putString("URL_MAP", url);
                    //editor.putBoolean("SERVER_SYNC", false);
                    editor.putString("USER_ID", user);
                    editor.putString("OPERATORS", operators.toString());
                    editor.putString("SELECT_OPERATORS", "[]");
                    editor.putString("PLANNED_CLIENTS", listaClientes.toString());
                    editor.putInt("EMPEZO_JORNADA",0);
                    editor.putString("TRUCK_INFO", truckInformation.toString());
                    editor.putString("CASETAS", getStringArrayList(casetas2));
                    editor.putBoolean("INOPERABILITY", false);
                    editor.putInt("CURRENT_STATE", 0);
                    editor.putInt("LOGIN_OK",1);
                    editor.putBoolean("CLOSE_DAY",false);
                    editor.putInt("SELECT_TRAZA",0);
                    editor.putInt("SELECT_EMBALAJE",0);
                    editor.commit();


                    return true;
                }
                else{
                    return false;
                }



            } catch (JSONException e) {
                return false;
            }
        }

        public String getStringArrayList(JSONArray json){
            ArrayList<String> aux = new ArrayList<String>();
            for(int i=0;i<json.length();i++){
                try {
                    aux.add(json.getJSONObject(i).getString("caseta"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return aux.toString();
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        moveTaskToBack(true);
        return super.onKeyDown(keyCode, event);
    }

    public void atras(View view){

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        Intent intent = new Intent(getApplicationContext(),A_Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }




}

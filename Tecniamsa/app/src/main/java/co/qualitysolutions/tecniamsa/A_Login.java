package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import utilidades.Utilities;

public class A_Login extends Activity {

    private ProgressDialog progress;
    private utilidades.WebService conection;
    private SharedPreferences sharedpreferences;
    private String user;
    private TextView txtUser;
    private TextView txtPassword;
    private AlertDialog.Builder adb;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_login);
        this.progress = new ProgressDialog(this);
        this.conection = new utilidades.WebService();
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.adb = new AlertDialog.Builder(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    /**
     * Method to verify if the user have already a session
     */
    @Override
    protected void onResume() {
        super.onResume();

        if(this.sharedpreferences.getString("USER_ID", null) != null && !this.sharedpreferences.getString("USER_ID", null).equals(""))
        {
            Intent intent = new Intent(getApplicationContext(), B_MenuPrincipal.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (resultCode == 2){

                new Login().execute(this.sharedpreferences.getString("USER_ID",""),this.sharedpreferences.getString("PASSWORD",""),this.sharedpreferences.getString("CITY",""),this.sharedpreferences.getString("PLACA","").replace(" ","%20"));

            }
        }
    }

    /**
     * This method verify if there are empty fields, else send the user information (user, password)
     * to start a new session.
     * @param v
     */
    public void logIn(View v){
        this.txtUser = (TextView) findViewById(R.id.txtUser);
        this.txtPassword = (TextView) findViewById(R.id.txtPassword);
        this.user = this.txtUser.getText().toString();
        String password = txtPassword.getText().toString();

        if(this.user.equals("") || password.equals("")){
            Utilities.showAlert(this, getResources().getString(R.string.alertEditTextEmpty));
        }
        else{
            if(hayInternet()){
                new Login().execute(this.user,password);
            }
            else{
                Utilities.showAlert(this, getResources().getString(R.string.toastInternetFail));
            }
        }
    }

    public String getSystem(int position){
        return getResources().getStringArray(R.array.systemV)[position];
    }

    public void changeActivity(){
        Intent intent = new Intent(this, A_LoginSecond.class);
        startActivityForResult(intent, 10);
    }

    /**
     * Method to verify if the device have an internet connection
     * @return
     */
    private boolean hayInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Override method to hide the keyboard when the user touch outside of an element
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
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
            if(params.length==2){
                String[] parameters = {"login",params[0],params[1]};
                conection.setUrl("http://www.concesionesdeaseo.com/pruebas/FUNLoginTecniamsa/Login1");
                this.answer = conection.conectar(parameters);
                try {
                    if(answer.getJSONObject(0).getString("informacion_vehiculo")!=null){
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("TRUCK_LIST", answer.getJSONObject(0).getJSONArray("informacion_vehiculo").toString());
                        editor.putString("CITY", answer.getJSONObject(0).getString("ciudad"));
                        editor.putString("USER_ID", params[0]);
                        editor.putString("PASSWORD", params[1]);
                        editor.commit();
                        changeActivity();
                        return true;
                    }
                    else{
                        return false;
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else if(params.length==4){
                String[] parameters = {"login_1",params[0],params[1]};
                conection.setUrl("http://www.concesionesdeaseo.com/pruebas/FUNLoginTecniamsa/Login2?ciudad="+params[2]+"&vehiculo="+params[3]);
                this.answer = conection.conectar(parameters);

                try {
                    //if(!this.answer.getJSONObject(0).getString("token").equals("")){
                    if(this.answer.getJSONObject(0).getJSONArray("lstdatos_cliente").length()>0){
                        if(this.saveInformation()){
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putInt("LOGIN_OK",1);
                            editor.commit();
                            return true;
                        }
                        else{
                            return false;
                        }
                    }
                    else{
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
            progress.dismiss();
            if(!result){
                try {
                    JSONArray clientes = this.answer.getJSONObject(0).getJSONArray("lstdatos_cliente");
                    int loginOk = sharedpreferences.getInt("LOGIN_OK",0);

                    if (loginOk == 0){
                        if(clientes.length()==0){
                            Toast.makeText(getApplication().getBaseContext(), getResources().getString(R.string.alertSinClientes), Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplication().getBaseContext(), getResources().getString(R.string.toastErrorLogin), Toast.LENGTH_LONG).show();
                        }

                    }

                    else  if (loginOk == 1){
                        Intent intent = new Intent(getApplicationContext(), B_MenuPrincipal.class);
                        startActivity(intent);
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplication().getBaseContext(), getResources().getString(R.string.alertInvalidos) + " o conexión demaciado inestable", Toast.LENGTH_LONG).show();
                }
            }
            else{
                try {
                    /*if(Utilities.compareDates(this.answer.getJSONObject(0).getString("fecha"),this.answer.getJSONObject(0).getString("hora"))){
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean("SERVER_SYNC", true);
                        editor.putString("DATE",this.answer.getJSONObject(0).getString("fecha"));
                        editor.putString("HOUR",this.answer.getJSONObject(0).getString("hora"));
                        editor.commit();
                        txtUser.setText("");
                        txtPassword.setText("");
                        Intent intent = new Intent(getApplicationContext(), B_MenuPrincipal.class);
                        startActivity(intent);*/
                    /*}
                    else{
                        adb.setCancelable(false);
                        adb.setTitle(getResources().getString(R.string.alertMensaje));
                        adb.setMessage("DEBE CAMBIAR LA HORA Y LA FECHA PARA QUE SEAN IGUALES A EL SERVIDOR, FECHA : " + this.answer.getJSONObject(0).getString("fecha") +" HORA: " + this.answer.getJSONObject(0).getString("hora"));
                        adb.setPositiveButton(getResources().getString(R.string.accept_button),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                                    }
                                });
                        adb.show();

                    }*/

                    if (sharedpreferences.getInt("LOGIN_OK",0) == 1){
                        Intent intent = new Intent(getApplicationContext(), B_MenuPrincipal.class);
                        startActivity(intent);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * This method gets and saves the information that return the server about the necessary data for start the
         * application
         * @return
         */
        private Boolean saveInformation(){

            try {

                //String token = this.answer.getJSONObject(0).getString("token");
                //String url = this.answer.getJSONObject(0).getString("url");
                //JSONArray alternateRoutes = this.answer.getJSONObject(0).getJSONArray("rutas_alternas");



                JSONArray listaClientes = this.answer.getJSONObject(0).getJSONArray("lstdatos_cliente");
                JSONArray operators = this.answer.getJSONObject(0).getJSONArray("lstoperarios");
                JSONArray truckInformation = this.answer.getJSONObject(0).getJSONArray("lstvehiculos");
                truckInformation.getJSONObject(0).put("cedula_conductor",user);
                JSONArray casetas1 =new JSONArray(this.answer.getJSONObject(0).getString("lstcanastas_activas"));
                JSONObject casetas = new JSONObject(casetas1.get(0).toString());
                JSONArray casetas2 = casetas.getJSONArray("caseta_peaje");



                if(listaClientes.length()>0){

                   /* for(int i=0; i<plannedRoutes.length(); i++){
                        //cambio del cero
                        if (plannedRoutes.getJSONObject(i).getBoolean("ticket_pendiente")) {
                            plannedRoutes.getJSONObject(i).put("estado", "terminada");
                            plannedRoutes.getJSONObject(i).put("tickets", new JSONArray());

                        }
                        else{
                            plannedRoutes.getJSONObject(i).put("estado", "inactiva");
                            plannedRoutes.getJSONObject(i).put("tipo", "planeada");
                            plannedRoutes.getJSONObject(i).put("tickets", new JSONArray());
                        }
                    }*/

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("TOKEN", "SIS_36.OQA5ADAALQA5ADkAMAA=");
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
                    editor.putInt("LOGIN_OK",0);
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
}

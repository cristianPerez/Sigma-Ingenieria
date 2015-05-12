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

        if(this.sharedpreferences.getBoolean("SERVER_SYNC",false))
        {
            Intent intent = new Intent(getApplicationContext(), B_MenuPrincipal.class);
            startActivity(intent);
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

    public void showAlert(String message){

        Utilities.showAlert(this,message);

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
                //conection.setUrl("http://www.concesionesdeaseo.com/gruposala/FUNLoginTecniamsa/Login1");
                this.answer = conection.conectar(parameters);
                try {
                    if(answer.getJSONObject(0).getString("informacion_vehiculo")!=null){
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("TRUCK_LIST", answer.getJSONObject(0).getJSONArray("informacion_vehiculo").toString());
                        editor.putString("CITY", answer.getJSONObject(0).getString("ciudad"));
                        editor.putString("USER_ID", params[0]);
                        editor.putString("PASSWORD", params[1]);

                        editor.commit();
                        //changeActivity();
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

            return false;
        }

        public Login() {
            super();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progress.dismiss();

            if(result)
                changeActivity();
            else
                showAlert("Datos invalidos, intentalo de nuevo");

        }

    }
}

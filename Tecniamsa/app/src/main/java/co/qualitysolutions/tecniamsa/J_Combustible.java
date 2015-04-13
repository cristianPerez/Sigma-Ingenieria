package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utilidades.SaveInformation;
import utilidades.Utilities;


public class J_Combustible extends Activity {

    private EditText txtTicket;
    private EditText txtGallons;
    private EditText txtProvider;
    private EditText txtCost;
    private RadioGroup radioGroup;
    private SharedPreferences sharedpreferences;
    private TextView date;
    private JSONArray send_data_json;
    private String method;
    private String methodInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.j_combustible);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.identifyElements();
    }

    /**
     * This method validate if the fields are empty, else ask user if really wants to save
     * the information, if the answer is "yes" call the next method (sendInformation)
     * @param v
     */
    public void save(View v){
        final String ticket = this.txtTicket.getText().toString();
        final String gallons = this.txtGallons.getText().toString();
        final String provider = this.txtProvider.getText().toString();
        final String cost = this.txtCost.getText().toString();
        final String gasKind = (String)((Button)findViewById(this.radioGroup.getCheckedRadioButtonId())).getText().toString();

        if(ticket.equals("") || gallons.equals("") || cost.equals("")){
            Utilities.showAlert(this, getResources().getString(R.string.alertEditTextEmpty));
        }
        else{
            if(Float.valueOf(gallons)<=200){

                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(getResources().getString(R.string.confirmSaveInformation));
                adb.setPositiveButton(
                        getResources().getString(R.string.confirm_button_1),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                JSONObject information = new JSONObject();
                                JSONObject auxobject = new JSONObject();
                                JSONArray auxjson;
                                send_data_json = new JSONArray();
                                try {

                                    auxobject.put("fecha_hora_evento",Utilities.getDate());
                                    auxobject.put("metodo","json_tecni_combustible");
                                    auxjson =  new JSONArray(sharedpreferences.getString("TRUCK_INFO",null));
                                    information.put("ticket", ticket);
                                    information.put("galones", gallons);
                                    information.put("proveedor", provider);
                                    information.put("tipo_combustible", gasKind);
                                    information.put("valor", cost);
                                    send_data_json.put(auxobject);
                                    send_data_json.put(auxjson.get(0));
                                    send_data_json.put(information);
                                    methodInt="44";
                                    method="json_tecni_combustible";
                                    clearFields();
                                } catch (JSONException e) {
                                }

                                sendInformation();
                                finish();
                            }
                        });
                adb.setNegativeButton(getResources().getString(R.string.confirm_button_2),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                adb.show();

            }
            else{

                Utilities.showAlert(this, "El numero de galones debe ser menor o igual a 200 !");

            }
        }
    }

    /**
     * Method to send information
     */
    public void sendInformation() {
        new SaveInformation(this).execute("http://www.concesionesdeaseo.com/gruposala/FUNEventosMovil/Eventos",
                methodInt,
                method,
                send_data_json.toString());
    }

    /**
     * Method for clear all text fields
     */
    private void clearFields(){
        this.txtTicket.setText("");
        this.txtGallons.setText("");
        this.txtProvider.setText("");
        this.txtCost.setText("");
    }

    /**
     * Method for load all necessary elements in the view
     */
    private void identifyElements(){
        this.txtTicket = (EditText) findViewById(R.id.txtTicket);
        this.txtGallons = (EditText) findViewById(R.id.txtGallons);
        this.txtProvider = (EditText) findViewById(R.id.txtProvider);
        this.txtCost = (EditText) findViewById(R.id.txtCost);
        this.radioGroup = (RadioGroup) findViewById(R.id.radioGroupGasKind);
        this.date = (TextView)findViewById(R.id.dateNow);
        this.date.setText(this.sharedpreferences.getString("DATE", Utilities.getDate().split(" ")[0]));
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

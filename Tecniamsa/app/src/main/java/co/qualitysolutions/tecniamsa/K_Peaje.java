package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import utilidades.SaveInformation;
import utilidades.Utilities;


public class K_Peaje extends Activity {

    private Button payToll;
    private EditText ticket;
    private SharedPreferences sharedpreferences;
    private AlertDialog.Builder adb;
    private TextView date;
    private ArrayList<String> lstItemsCasetas;
    private ArrayAdapter<String> adapterItemsCasetas;
    private Spinner casetas;
    private JSONArray send_data_json;
    private String method;
    private String methodInt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.k_peaje);

        this.sharedpreferences = getSharedPreferences("MyPreferences",
                Context.MODE_PRIVATE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        inicializeObjects();
        llenarCasetas();
    }

    public void llenarCasetas (){

        String lstItemStringCasetas=sharedpreferences.getString("CASETAS","[]");
        lstItemsCasetas = new ArrayList<String>(Arrays.asList(lstItemStringCasetas.substring(1, lstItemStringCasetas.length() - 1).split(",")));
        lstItemsCasetas = cleanEmptyCharacter(lstItemsCasetas);
        adapterItemsCasetas = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstItemsCasetas);
        adapterItemsCasetas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        casetas.setAdapter(adapterItemsCasetas);

    }

    public ArrayList<String> cleanEmptyCharacter(ArrayList<String> list){

        for (int i=0;i<list.size();i++)
            list.set(i, list.get(i).trim());

        return list;

    }

    /**
     *
     */
    public void inicializeObjects() {


        this.payToll = (Button) findViewById(R.id.btnPayToll);
        this.ticket = (EditText) findViewById(R.id.etTicket);
        this.adb = new AlertDialog.Builder(this);
        this.date = (TextView)findViewById(R.id.dateNow);
        this.date.setText(this.sharedpreferences.getString("DATE", Utilities.getDate().split(" ")[0]));
        this.casetas = (Spinner) findViewById(R.id.casetas);

    }

    public void pay_toll (View v){

        if(!this.ticket.getText().toString().equals("")){

            try {

                JSONObject auxobject = new JSONObject();
                JSONArray auxjson;
                send_data_json = new JSONArray();
                JSONObject auxJsonTicket = new JSONObject();
                auxobject.put("fecha_hora_evento",Utilities.getDate());
                auxobject.put("metodo","json_tecni_peaje");
                auxjson =  new JSONArray(sharedpreferences.getString("TRUCK_INFO",null));
                auxJsonTicket.put("ticket", this.ticket.getText().toString());
                auxJsonTicket.put("caseta", casetas.getSelectedItem().toString());
                send_data_json.put(auxobject);
                send_data_json.put(auxjson.get(0));
                send_data_json.put(auxJsonTicket);
                methodInt="45";
                method="json_tecni_peaje";
                sendInformation();
                Toast.makeText(this, "Peaje enviado con exito", Toast.LENGTH_LONG).show();
                finish();

            } catch (Exception e) {
            }

        }

        else{
            Toast.makeText(this, "Complete el campo # Ticket para continuar", Toast.LENGTH_LONG).show();
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

                        JSONObject auxobject = new JSONObject();
                        JSONArray auxjson;
                        try {
                            auxjson = new JSONArray(sharedpreferences.getString("TRUCK_INFO", null));
                            send_data_json = new JSONArray();

                            auxobject.put("fecha_hora_evento", Utilities.getDate());
                            auxobject.put("metodo", "json_tecni_cerrarsesion");
                            auxobject.put("usuario", sharedpreferences.getString("USER_ID", "14880479"));
                            send_data_json.put(auxobject);
                            send_data_json.put(auxjson.get(0));
                            methodInt = "51";
                            method = "json_tecni_cerrarsesion";
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }


}

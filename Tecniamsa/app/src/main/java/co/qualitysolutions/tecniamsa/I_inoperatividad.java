package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utilidades.SaveInformation;
import utilidades.Utilities;


public class I_inoperatividad extends Activity {

    private SharedPreferences sharedpreferences;
    private SharedPreferences privatePreferences;
    private RadioGroup radioGroup;
    private EditText txtDetail;
    private ImageButton btnStart,btnFinish;
    private String inoperabilityCase;
    private TextView date;
    private Drawable btn_start_inoperability,btn_start_inoperability_two,btn_finish_inoperability,btn_finish_inoperability_two;
    private JSONArray send_data_json,clientesPlaneados;
    private JSONObject clienteSeleccionado;
    private String method;
    private String methodInt;
    private Activity myself;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.i_inoperatividad);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.privatePreferences = getSharedPreferences("InoperabilityPreferences", Context.MODE_PRIVATE);
        this.myself=this;
        this.identifyElements();
        this.inoperabilityCase="";
        if(this.sharedpreferences.getBoolean("INOPERABILITY", false)){
            btnfinishtAvailable();
            this.blockElements();
            String detail = this.privatePreferences.getString("DETAIL", null);
            int checked = this.privatePreferences.getInt("CHECKED", -1);
            this.radioGroup.check(checked);
            this.txtDetail.setText(detail);


        }
        this.date = (TextView)findViewById(R.id.dateNow);
        this.date.setText(this.sharedpreferences.getString("DATE", Utilities.getDate().split(" ")[0]));
        try {
            this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
            this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method called when the user pressed the start button, this method asks the user if really wants start
     * inoperability, if the answer is "yes" the method sendInformation() is called.
     * @param v
     */
    public void start(View v){
        this.inoperabilityCase = (String)((Button)findViewById(this.radioGroup.getCheckedRadioButtonId())).getText().toString();
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getResources().getString(R.string.confirmStartInoperability));
        adb.setPositiveButton(
                getResources().getString(R.string.confirm_button_1),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnfinishtAvailable();
                        blockElements();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean("INOPERABILITY", true);
                        editor.putString("START_INOPERABILITY", Utilities.getDate().toString());
                        editor.commit();
                        saveInformation();
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
        //}
    }

    /**
     * Method called when the user pressed the finish button, this method asks the user if really wants finish
     * inoperability, if the answer is "yes" the method sendInformation() is called for send the information to server.
     * @param v
     */
    public void finish(View v){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getResources().getString(R.string.confirmFinishInoperability));
        adb.setPositiveButton(
                getResources().getString(R.string.confirm_button_1),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean("INOPERABILITY", false);
                        editor.commit();
                        String user = sharedpreferences.getString("USER_ID", null);
                        JSONObject infoInoperability = new JSONObject();
                        JSONArray auxjson;
                        try {

                            auxjson =  new JSONArray(sharedpreferences.getString("TRUCK_INFO",null));
                            infoInoperability.put("hora_inicio_inoperatividad",sharedpreferences.getString("START_INOPERABILITY", ""));
                            infoInoperability.put("usuario_reportante", user);
                            infoInoperability.put("motivo", inoperabilityCase);
                            infoInoperability.put("observacion",privatePreferences.getString("DETAIL", ""));
                            infoInoperability.put("hora_fin_inoperatividad",Utilities.getDate().toString());
                            infoInoperability.put("metodo","json_tecni_inoperatividad");
                            infoInoperability.put("hoja",clienteSeleccionado.getString("hoja"));

                            send_data_json = new JSONArray();
                            send_data_json.put(infoInoperability);//Information of user and inoperability

                            send_data_json.put(auxjson.get(0));
                        } catch (JSONException e) {
                        }
                        methodInt="43";
                        method="json_tecni_inoperatividad";
                        //sendInformation();
                        Utilities.sendInformation(myself,methodInt,method,send_data_json.toString());
                        deleteInformation();
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


    /**
     * Method to recover the activity view state when the inoperability start.
     */
    private void saveInformation(){
        SharedPreferences.Editor editor = this.privatePreferences.edit();
        editor.putString("DETAIL", this.txtDetail.getText().toString());
        editor.putInt("CHECKED", this.radioGroup.getCheckedRadioButtonId());
        editor.commit();
    }

    /**
     * Method to remove of preferences previously saved information.
     */
    private void deleteInformation(){
        SharedPreferences.Editor editor = this.privatePreferences.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * Method for load all necessary elements in the view
     */
    private void identifyElements(){
        this.radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        this.txtDetail = (EditText) findViewById(R.id.txtDetail);
        this.btnStart = (ImageButton) findViewById(R.id.btnStart);
        this.btnFinish = (ImageButton) findViewById(R.id.btnFinish);
        this.btn_start_inoperability = this.getResources().getDrawable(R.mipmap.btn_iniciar);
        this.btn_start_inoperability_two = this.getResources().getDrawable(R.mipmap.btn_iniciar_activo);
        this.btn_finish_inoperability = this.getResources().getDrawable(R.mipmap.btn_terminar);
        this.btn_finish_inoperability_two = this.getResources().getDrawable(R.mipmap.btn_terminar_activo);
        this.btnFinish.setEnabled(false);
    }

    private void blockElements(){

        this.btnStart.setEnabled(false);
        this.txtDetail.setEnabled(false);
        this.radioGroup.setEnabled(false);

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



    public void btnfinishtAvailable(){

        this.btnFinish.setImageDrawable(this.btn_finish_inoperability);
        this.btnFinish.setEnabled(true);

        this.btnStart.setImageDrawable(this.btn_start_inoperability_two);
        this.btnStart.setEnabled(false);

    }

}

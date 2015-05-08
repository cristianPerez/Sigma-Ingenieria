package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utilidades.SaveInformation;
import utilidades.Utilities;


public class H_FormularioDisposicionFInal extends Activity {

    private ImageButton in_filler, out_filler , actualizar_trazas;
    private Drawable d_in_filler, d_in_filler_two, d_out_filler,d_out_filler_two , d_actualizar_trazas,d_actualizar_trazas_two;
    private SharedPreferences sharedpreferences;
    private AlertDialog.Builder adb;
    private TextView date;
    private JSONArray send_data_json,clientesPlaneados;
    private JSONObject clienteSeleccionado;
    private String method;
    private String methodInt;
    private Activity myself;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h_formulario_disposicion_final);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.myself=this;
        initializeComponents();
        if(sharedpreferences.getBoolean("IN_FILLER",false)){
            buttonsInFiller();
        }
        else
            noInfillier();
    }


    public void inFiller(View v) {


        this.adb.setTitle("ESTA SEGURO DE QUE ESTA DISPOSICIÓN FINAL");
        this.adb.setPositiveButton(getResources().getString(R.string.confirm_button_1),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("TIME_START_IN_FILLER", Utilities.getDate());
                        editor.putBoolean("IN_FILLER", true);
                        editor.commit();
                        buttonsInFiller();
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

    public void ActualizarTrazas(View v) {


        this.adb.setTitle("Esta seguro que desea actualizar las trazas?");
        this.adb.setPositiveButton(getResources().getString(R.string.confirm_button_1),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(myself,F_Seleccionar_cliente.class);
                        startActivity(intent);

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

    public void outFiller(View v) {


        try {
            clientesPlaneados = new JSONArray(sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
            clienteSeleccionado = clientesPlaneados.getJSONObject(sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Alerta!");
        adb.setMessage("Desea finalizar el tiempo de disposicion final?");
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
            auxobject.put("metodo","json_tecni_disposicionfinal");
            auxobject.put("usuario",sharedpreferences.getString("USER_ID", "mavalencia"));
            auxobject.put("hora_inicio_disposicion_final",sharedpreferences.getString("TIME_START_IN_FILLER", Utilities.getDate()));
            auxobject.put("hora_fin_disposicion_final",Utilities.getDate());
            auxobject.put("hoja",clienteSeleccionado.getString("hoja"));
            send_data_json.put(auxobject);
            send_data_json.put(auxjson.get(0));
            methodInt="49";
            method="json_tecni_disposicionfinal";
            Utilities.sendInformation(myself,methodInt,method,send_data_json.toString());
            //sendInformation();

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("IN_FILLER", false);
            editor.commit();

            Intent intent = new Intent();
            setResult(2, intent);
            finish();
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
                            method="json_tecni_cerrarsesionn";
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


    public void buttonsInFiller(){
        this.in_filler.setEnabled(false);
        this.in_filler.setImageDrawable(this.d_in_filler_two);
        this.out_filler.setEnabled(true);
        this.out_filler.setImageDrawable(this.d_out_filler);
        this.actualizar_trazas.setEnabled(true);
        this.actualizar_trazas.setImageDrawable(this.d_actualizar_trazas);
    }

    public void noInfillier(){


            this.in_filler.setEnabled(true);
            this.in_filler.setImageDrawable(this.d_in_filler);
            this.out_filler.setEnabled(false);
            this.out_filler.setImageDrawable(this.d_out_filler_two);
            this.actualizar_trazas.setEnabled(false);
            this.actualizar_trazas.setImageDrawable(this.d_actualizar_trazas_two);


    }

    public void initializeComponents() {

        this.adb = new AlertDialog.Builder(this);
        this.in_filler = (ImageButton) findViewById(R.id.in_filler);
        this.out_filler = (ImageButton) findViewById(R.id.out_filler);
        this.actualizar_trazas = (ImageButton) findViewById(R.id.actualizar_trazas);
        this.d_in_filler = this.getResources().getDrawable(R.mipmap.btn_in_filler);
        this.d_in_filler_two = this.getResources().getDrawable(R.mipmap.btn_in_filler_two);
        this.d_out_filler = this.getResources().getDrawable(R.mipmap.btn_out_filler);
        this.d_out_filler_two = this.getResources().getDrawable(R.mipmap.btn_out_filler_two);
        this.d_actualizar_trazas = this.getResources().getDrawable(R.mipmap.actualizar_trazas);
        this.d_actualizar_trazas_two = this.getResources().getDrawable(R.mipmap.actualizar_trazas_two);
        this.out_filler.setEnabled(false);
        this.date = (TextView)findViewById(R.id.dateNow);
        this.date.setText(this.sharedpreferences.getString("FECHA_SERVER", Utilities.getDate().split(" ")[0]));
    }
}

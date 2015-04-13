package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utilidades.SaveInformation;
import utilidades.Utilities;


public class E_MenuCiclo extends Activity {

    private SharedPreferences sharedpreferences;
    private AlertDialog.Builder adb;

    private ImageButton btn_base_exit, btn_start_collection,
            btn_collection_finish, btn_arrive_final_disposition,
            btn_come_back_to_base, btn_inoperability, btn_trazas;
    private Drawable d_base_exit, d_base_exit_two, d_start_collection,
            d_start_collection_two,
            d_collection_finish, d_collection_finish_two,
            d_arrive_final_disposition, d_arrive_final_disposition_two,
            d_come_back_to_base, d_come_back_to_base_two,
            d_inoperability, d_inoperability_two , g_trazas, g_trazas_two;

    private JSONArray send_data_json,clientesPlaneados;
    private JSONObject clienteSeleccionado;
    private String method;
    private TextView numberOfCompactions,nameRoute;
    private String methodInt;
    private TextView date;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.e_menu_ciclo);
        this.methodInt= "0";
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.initializeComponents();
        currentState();
    }

    /**
     * Method to register the base output, send to the server the important information,
     * and change the state of buttons.
     * @param v
     */
    public void baseOut(View v){

            this.adb.setTitle("Alerta!");
            this.adb.setMessage(getResources().getString(R.string.baseOutput));
            this.adb.setPositiveButton(getResources().getString(R.string.confirm_button_1),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                JSONArray truckInformation = new JSONArray((String) sharedpreferences.getString("TRUCK_INFO", ""));
                                truckInformation.getJSONObject(0).put("hora_salida_base",Utilities.getDate());
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("TRUCK_INFO",truckInformation.toString());
                                editor.commit();
                                buttonsOutBase();
                                dialog.dismiss();

                            } catch (Exception e) {
                                // TODO: handle exception
                            }
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


    /**
     * Method to display the select route interface
     * @param v
     */
    public void startCollection(View v) {

        Intent intent = new Intent(this, F_Seleccionar_cliente.class);
        startActivityForResult(intent, 10);
    }

    /**
     * Method to display the select route interface
     * @param v
     */
    public void trazasCliente(View v) {

        Intent intent = new Intent(this, G_TrazasEmbalaje.class);
        startActivityForResult(intent, 10);
    }


    /**
     * Method to finish the collection and close the route if the driver response is the positive button
     * @param v
     */
    public void finishCollection(View v) {

        this.adb.setTitle(getResources().getString(R.string.alertMensaje));
        adb.setMessage(getResources().getString(R.string.confirmFinishCollection));
        this.adb.setPositiveButton(
                getResources().getString(R.string.confirm_button_1),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        JSONArray auxjson;
                        try {
                            auxjson =  new JSONArray(sharedpreferences.getString("TRUCK_INFO",null));

                            clientesPlaneados = new JSONArray(sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
                            clienteSeleccionado = clientesPlaneados.getJSONObject(sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));

                            send_data_json = new JSONArray();
                            JSONObject auxobject = new JSONObject();
                            auxobject.put("fecha_hora_evento",Utilities.getDate());
                            auxobject.put("metodo","fin_porte");
                            //auxobject.put("compactaciones",sharedpreferences.getInt("COMPACTIONS",0));
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putInt("CURRENT_STATE", 4);
                            editor.commit();
                            send_data_json.put(auxobject);
                            auxobject = new JSONObject();
                            auxobject.put("operarios", new JSONArray(sharedpreferences.getString("SELECT_OPERATORS", "[]")));
                            send_data_json.put(clienteSeleccionado);
                            send_data_json.put(auxobject);
                            send_data_json.put(auxjson.get(0));
                            method="fin_porte";
                            methodInt="2";
                            sendInformation();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        buttonsFinishCollection();
                    }
                });
        this.adb.setNegativeButton(
                getResources().getString(R.string.confirm_button_2),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        this.adb.show();
    }

    /**
     * Method to display the filler interface
     * @param v
     */
    public void arriveFinalDisposition(View v){
        Intent intent = new Intent(this, H_FormularioDisposicionFInal.class);
        startActivityForResult(intent, 10);
    }

    /**
     * Method to register when the truck arrive in the base, finally register in the server the route list.
     * @param v
     */
    public void comeBackToBase(View v){

        this.adb.setTitle("Alerta");
        this.adb.setMessage(getResources().getString(R.string.areYouSureBase));
        this.adb.setPositiveButton(getResources().getString(R.string.confirm_button_1),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buttonsInBase();
                        JSONObject auxobject= new JSONObject();
                        JSONArray auxjson;
                        try {
                            auxjson =  new JSONArray(sharedpreferences.getString("TRUCK_INFO",null));
                            send_data_json = new JSONArray();

                            auxobject.put("fecha_hora_evento",Utilities.getDate());
                            auxobject.put("metodo","regreso_base");

                            clientesPlaneados = new JSONArray(sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
                            clienteSeleccionado = clientesPlaneados.getJSONObject(sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
                            auxobject.put("hoja", clienteSeleccionado.get("hoja"));
                            send_data_json.put(auxobject);

                            auxobject = new JSONObject();
                            auxobject.put("rutas_planeadas", clientesPlaneados);
                            send_data_json.put(auxobject);
                            send_data_json.put(auxjson.get(0));
                            methodInt="7";
                            method="regreso_base";
                            sendInformation();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
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


    /**
     * Method to display inoperability interface
     * @param v
     */
    public void inoperability(View v){

        Intent intent = new Intent(this, I_inoperatividad.class);
        startActivity(intent);
    }


    /**
     * Method that will activate when the next activity responds
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (resultCode == 2){
                buttonsStartCollection();
            }
            else if(resultCode == 3){
                finishFiller();
            }
            else if(resultCode == 1){


            }
        }
    }

    /**
     * Method that will activate when the user come back to the cycle menu interface and after
     * change the state of buttons.
     */
    @Override
    protected void onResume() {
        setCompactationsAndSheet();
        if (sharedpreferences.getBoolean("INOPERABILITY", false)) {
            buttonsInoperability();
        }
        else if (sharedpreferences.getBoolean("IN_FILLER",false))
        {
            buttonsInFiller();
        }
        else{
            currentState();
        }
        super.onResume();
    }

    /**
     *Method that send the information to server, from whatever method
     */
    public void sendInformation(){

        try {
            new SaveInformation(this).execute("http://www.concesionesdeaseo.com/gruposala/FUNEventosMovil/Eventos",
                    this.methodInt,
                    this.method,
                    this.send_data_json.toString());
        } catch (Exception e) {
        }
    }

    /**
     *Method to display the teamwork select interface
     */
    public void startTeamOfWork() {
        /*Intent intent = new Intent(this, C_GrupoTrabajo.class);
        startActivity(intent);*/
    }

    /**
     *Method to configure the buttons logic, when the truck go out the base
     */
    public void buttonsOutBase() {

        SharedPreferences.Editor editor = sharedpreferences.edit();
        int current_state = sharedpreferences.getInt("CURRENT_STATE", 0);

        if (current_state == 0) {
            editor.putInt("CURRENT_STATE", 1);
            editor.commit();
        }

        this.btn_base_exit.setImageDrawable(this.d_base_exit_two);
        this.btn_base_exit.setEnabled(false);

        this.btn_start_collection.setImageDrawable(this.d_start_collection);
        this.btn_start_collection.setEnabled(true);

        //boton nuevo
        this.btn_trazas.setImageDrawable(this.g_trazas_two);
        this.btn_trazas.setEnabled(false);

        /*this.btn_compaction.setImageDrawable(this.d_compactation_two);
        this.btn_compaction.setEnabled(false);*/

        this.btn_collection_finish.setImageDrawable(this.d_collection_finish_two);
        this.btn_collection_finish.setEnabled(false);

        this.btn_arrive_final_disposition.setImageDrawable(this.d_arrive_final_disposition);
        this.btn_arrive_final_disposition.setEnabled(true);

        this.btn_come_back_to_base.setImageDrawable(this.d_come_back_to_base_two);
        this.btn_come_back_to_base.setEnabled(false);

       /* this.btn_special_service.setImageDrawable(this.d_special_service_two);
        this.btn_special_service.setEnabled(false);*/

        this.btn_inoperability.setImageDrawable(this.d_inoperability);
        this.btn_inoperability.setEnabled(true);

       /* this.btn_transbordo.setImageDrawable(this.d_transbordo_two);
        this.btn_transbordo.setEnabled(false);

        this.btn_cambio_vehiculo.setImageDrawable(this.d_cambio_vehiculo_two);
        this.btn_cambio_vehiculo.setEnabled(false);*/
    }


    /**
     * Method to configure the buttons logic, when the truck start to collection
     *
     */
    public void buttonsStartCollection() {

        this.btn_base_exit.setImageDrawable(this.d_base_exit_two);
        this.btn_base_exit.setEnabled(false);

        this.btn_start_collection.setImageDrawable(this.d_start_collection_two);
        this.btn_start_collection.setEnabled(false);

        //boton nuevo
        this.btn_trazas.setImageDrawable(this.g_trazas);
        this.btn_trazas.setEnabled(true);

        /*this.btn_compaction.setImageDrawable(this.d_compactation);
        this.btn_compaction.setEnabled(true);*/

        this.btn_collection_finish.setImageDrawable(this.d_collection_finish);
        this.btn_collection_finish.setEnabled(true);

        this.btn_arrive_final_disposition.setImageDrawable(this.d_arrive_final_disposition_two);
        this.btn_arrive_final_disposition.setEnabled(false);

        this.btn_come_back_to_base.setImageDrawable(this.d_come_back_to_base_two);
        this.btn_come_back_to_base.setEnabled(false);

        /*this.btn_special_service.setImageDrawable(this.d_special_service_two);
        this.btn_special_service.setEnabled(false);*/

        this.btn_inoperability.setImageDrawable(this.d_inoperability);
        this.btn_inoperability.setEnabled(true);

        /*this.btn_transbordo.setImageDrawable(this.d_transbordo_two);
        this.btn_transbordo.setEnabled(false);

        this.btn_cambio_vehiculo.setImageDrawable(this.d_cambio_vehiculo_two);
        this.btn_cambio_vehiculo.setEnabled(false);*/

    }

    /**
     * Method to configure the buttons logic, when finish collection or Finish in filler
     *
     */

    public void buttonsFinishCollection() {

        this.btn_base_exit.setImageDrawable(this.d_base_exit_two);
        this.btn_base_exit.setEnabled(false);

        this.btn_start_collection.setImageDrawable(this.d_start_collection);
        this.btn_start_collection.setEnabled(true);

        //boton nuevo
        this.btn_trazas.setImageDrawable(this.g_trazas_two);
        this.btn_trazas.setEnabled(false);

        /*this.btn_compaction.setImageDrawable(this.d_compactation_two);
        this.btn_compaction.setEnabled(false);*/

        this.btn_collection_finish.setImageDrawable(this.d_collection_finish_two);
        this.btn_collection_finish.setEnabled(false);


        this.btn_arrive_final_disposition.setImageDrawable(this.d_arrive_final_disposition);
        this.btn_arrive_final_disposition.setEnabled(true);


        /*
        this.btn_arrive_final_disposition.setImageDrawable(this.d_arrive_final_disposition_two);
        this.btn_arrive_final_disposition.setEnabled(false);*/


        this.btn_come_back_to_base.setImageDrawable(this.d_come_back_to_base);
        this.btn_come_back_to_base.setEnabled(true);

        /*this.btn_special_service.setImageDrawable(this.d_special_service_two);
        this.btn_special_service.setEnabled(false);*/

        this.btn_inoperability.setImageDrawable(this.d_inoperability);
        this.btn_inoperability.setEnabled(true);

        /*this.btn_transbordo.setImageDrawable(this.d_transbordo);
        this.btn_transbordo.setEnabled(true);

        this.btn_cambio_vehiculo.setImageDrawable(this.d_cambio_vehiculo);
        this.btn_cambio_vehiculo.setEnabled(true);*/
    }

    /**
     *Method to configure the buttons logic, when the truck be in the base
     */
    public void buttonsInBase() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("CURRENT_STATE", 0);
        editor.commit();
        this.btn_base_exit.setImageDrawable(this.d_base_exit);
        this.btn_base_exit.setEnabled(true);

        this.btn_start_collection.setImageDrawable(this.d_start_collection_two);
        this.btn_start_collection.setEnabled(false);

        //boton nuevo
        this.btn_trazas.setImageDrawable(this.g_trazas_two);
        this.btn_trazas.setEnabled(false);


        /*this.btn_compaction.setImageDrawable(this.d_compactation_two);
        this.btn_compaction.setEnabled(false);*/

        this.btn_collection_finish.setImageDrawable(this.d_collection_finish_two);
        this.btn_collection_finish.setEnabled(false);

        this.btn_arrive_final_disposition.setImageDrawable(this.d_arrive_final_disposition_two);
        this.btn_arrive_final_disposition.setEnabled(false);

        this.btn_come_back_to_base.setImageDrawable(this.d_come_back_to_base_two);
        this.btn_come_back_to_base.setEnabled(false);

        /*this.btn_special_service.setImageDrawable(this.d_special_service_two);
        this.btn_special_service.setEnabled(false);*/

        this.btn_inoperability.setImageDrawable(this.d_inoperability);
        this.btn_inoperability.setEnabled(true);

        /*this.btn_transbordo.setImageDrawable(this.d_transbordo_two);
        this.btn_transbordo.setEnabled(false);

        this.btn_cambio_vehiculo.setImageDrawable(this.d_cambio_vehiculo_two);
        this.btn_cambio_vehiculo.setEnabled(false);*/
    }


    /**
     * Method to configure the buttons logic, when there is inoperability or filler
     *
     */
    public void buttonsInFiller(){

        this.btn_base_exit.setImageDrawable(this.d_base_exit_two);
        this.btn_base_exit.setEnabled(false);

        this.btn_start_collection.setEnabled(false);
        this.btn_start_collection.setImageDrawable(this.d_start_collection_two);

        //boton nuevo
        this.btn_trazas.setImageDrawable(this.g_trazas_two);
        this.btn_trazas.setEnabled(false);

       /* this.btn_compaction.setImageDrawable(this.d_compactation_two);
        this.btn_compaction.setEnabled(false);*/

        this.btn_collection_finish.setImageDrawable(this.d_collection_finish_two);
        this.btn_collection_finish.setEnabled(false);

        this.btn_come_back_to_base.setImageDrawable(this.d_come_back_to_base_two);
        this.btn_come_back_to_base.setEnabled(false);

       /* this.btn_special_service.setImageDrawable(this.d_special_service_two);
        this.btn_special_service.setEnabled(false);*/

        this.btn_arrive_final_disposition.setImageDrawable(this.d_arrive_final_disposition);
        this.btn_arrive_final_disposition.setEnabled(true);

        this.btn_inoperability.setImageDrawable(this.d_inoperability_two);
        this.btn_inoperability.setEnabled(true);

        /*this.btn_transbordo.setImageDrawable(this.d_transbordo_two);
        this.btn_transbordo.setEnabled(false);

        this.btn_cambio_vehiculo.setImageDrawable(this.d_cambio_vehiculo_two);
        this.btn_cambio_vehiculo.setEnabled(false);*/

    }





    /**
     * Method to configure the buttons logic, when finish collection or Finish in filler
     * @param
     */
    public void finishFiller() {

        this.btn_base_exit.setImageDrawable(this.d_base_exit_two);
        this.btn_base_exit.setEnabled(false);

        this.btn_start_collection.setImageDrawable(this.d_start_collection);
        this.btn_start_collection.setEnabled(true);

        //boton nuevo
        this.btn_trazas.setImageDrawable(this.g_trazas_two);
        this.btn_trazas.setEnabled(false);

        /*this.btn_compaction.setImageDrawable(this.d_compactation_two);
        this.btn_compaction.setEnabled(false);*/

        this.btn_collection_finish.setImageDrawable(this.d_collection_finish_two);
        this.btn_collection_finish.setEnabled(false);

        this.btn_arrive_final_disposition.setImageDrawable(this.d_arrive_final_disposition_two);
        this.btn_arrive_final_disposition.setEnabled(false);

        this.btn_come_back_to_base.setImageDrawable(this.d_come_back_to_base);
        this.btn_come_back_to_base.setEnabled(true);

       /* this.btn_special_service.setImageDrawable(this.d_special_service_two);
        this.btn_special_service.setEnabled(false);*/

        this.btn_inoperability.setImageDrawable(this.d_inoperability);
        this.btn_inoperability.setEnabled(true);

        /*this.btn_transbordo.setImageDrawable(this.d_transbordo_two);
        this.btn_transbordo.setEnabled(false);

        this.btn_cambio_vehiculo.setImageDrawable(this.d_cambio_vehiculo_two);
        this.btn_cambio_vehiculo.setEnabled(false);*/
    }




    /**
     * Method to configure the buttons logic, when there is inoperability or filler
     *
     */
    public void buttonsInoperability(){

        this.btn_base_exit.setImageDrawable(this.d_base_exit_two);
        this.btn_base_exit.setEnabled(false);

        this.btn_start_collection.setEnabled(false);
        this.btn_start_collection.setImageDrawable(this.d_start_collection_two);

        //boton nuevo
        this.btn_trazas.setImageDrawable(this.g_trazas_two);
        this.btn_trazas.setEnabled(false);

        /*this.btn_compaction.setImageDrawable(this.d_compactation_two);
        this.btn_compaction.setEnabled(false);*/

        this.btn_collection_finish.setImageDrawable(this.d_collection_finish_two);
        this.btn_collection_finish.setEnabled(false);

        this.btn_come_back_to_base.setImageDrawable(this.d_come_back_to_base_two);
        this.btn_come_back_to_base.setEnabled(false);

        /*this.btn_special_service.setImageDrawable(this.d_special_service_two);
        this.btn_special_service.setEnabled(false);*/


        this.btn_inoperability.setImageDrawable(this.d_inoperability);
        this.btn_inoperability.setEnabled(true);

        this.btn_arrive_final_disposition.setImageDrawable(this.d_arrive_final_disposition_two);
        this.btn_arrive_final_disposition.setEnabled(false);

        /*this.btn_transbordo.setImageDrawable(this.d_transbordo_two);
        this.btn_transbordo.setEnabled(false);

        this.btn_cambio_vehiculo.setImageDrawable(this.d_cambio_vehiculo_two);
        this.btn_cambio_vehiculo.setEnabled(false);*/


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


    /**
     * Method for load all necessary elements in the view
     */
    public void initializeComponents() {
        this.adb = new AlertDialog.Builder(this);
        this.btn_base_exit = (ImageButton) findViewById(R.id.btn_base_exit);
        this.btn_start_collection = (ImageButton) findViewById(R.id.btn_start_collection);
        this.btn_start_collection.setEnabled(false);

        //nuevo boton
        this.btn_trazas = (ImageButton) findViewById(R.id.btn_trazas);
        this.btn_trazas.setEnabled(false);

        this.btn_collection_finish = (ImageButton) findViewById(R.id.btn_collection_finish);
        this.btn_collection_finish.setEnabled(false);
        this.btn_arrive_final_disposition = (ImageButton) findViewById(R.id.btn_arrive_final_disposition);
        this.btn_arrive_final_disposition.setEnabled(false);
        this.btn_come_back_to_base = (ImageButton) findViewById(R.id.btn_come_back_to_base);
        this.btn_come_back_to_base.setEnabled(false);
        this.btn_inoperability = (ImageButton) findViewById(R.id.btn_inoperability);
        this.btn_inoperability.setEnabled(false);
        this.btn_inoperability = (ImageButton) findViewById(R.id.btn_inoperability);
        this.btn_inoperability.setEnabled(false);
        this.d_base_exit = this.getResources().getDrawable(R.mipmap.btn_base_exit);
        this.d_base_exit_two = this.getResources().getDrawable(R.mipmap.btn_base_exit_two);

        this.d_start_collection = this.getResources().getDrawable(R.mipmap.btn_start_collection);
        this.d_start_collection_two = this.getResources().getDrawable(R.mipmap.btn_start_collection_two);

        this.d_collection_finish = this.getResources().getDrawable(R.mipmap.btn_collection_finish);
        this.d_collection_finish_two = this.getResources().getDrawable(R.mipmap.btn_collection_finish_two);

        this.d_arrive_final_disposition = this.getResources().getDrawable(R.mipmap.btn_arrive_final_disposition);
        this.d_arrive_final_disposition_two = this.getResources().getDrawable(R.mipmap.btn_arrive_final_disposition_two);

        this.d_come_back_to_base = this.getResources().getDrawable(R.mipmap.btn_come_back_to_base);
        this.d_come_back_to_base_two = this.getResources().getDrawable(R.mipmap.btn_come_back_to_base_two);

        this.d_inoperability = this.getResources().getDrawable(R.mipmap.btn_inoperability);
        this.d_inoperability_two = this.getResources().getDrawable(R.mipmap.btn_inoperability_two);

        this.g_trazas = this.getResources().getDrawable(R.mipmap.btn_trazas);
        this.g_trazas_two = this.getResources().getDrawable(R.mipmap.btn_trazas_two);

        this.send_data_json= new JSONArray();
        this.date = (TextView)findViewById(R.id.dateNow);
        this.date.setText(this.sharedpreferences.getString("FECHA_SERVER", Utilities.getDate().split(" ")[0]));
    }
    /**
     * Method to change the buttons logic according to the state
     */
    public void currentState(){

        int current_state = sharedpreferences.getInt("CURRENT_STATE", 0);

        if (current_state != 0) {

            if (sharedpreferences.getBoolean("INOPERABILITY", false)) {
                buttonsInoperability();
            }else if (sharedpreferences.getBoolean("IN_FILLER", false)) {
                buttonsInFiller();
            }else if (current_state == 1) {
                buttonsOutBase();
            }else if (current_state == 2) {
                buttonsStartCollection();
            }else if (current_state == 4) {
                buttonsFinishCollection();
            }else if(current_state == 5){
                finishFiller();
            }

        }
        else{
            buttonsInBase();
        }

    }

    /**
     * Method to visualize the number of compaction and sheet route in the interface
     */
    public void setCompactationsAndSheet(){

        int position = sharedpreferences.getInt("POS_CURRENT_ROUTE", -1);
        if(position!=-1){
            try {
                JSONArray auxRoutes = new JSONArray(sharedpreferences.getString("PLANNED_ROUTES", null));
                JSONObject auxRoute = auxRoutes.getJSONObject(position);
                int compactions = sharedpreferences.getInt("COMPACTIONS",0);
                this.numberOfCompactions.setText(String.valueOf(compactions));
                this.nameRoute.setText(auxRoute.getString("hoja"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Method to clear the number of compaction and sheet route in the interface
     */
    public void clearSheet(){
        this.nameRoute.setText("");
    }
    /**
     * Method to clear the number of compaction and sheet route in the interface
     */
    public void clearCompactions(){
        this.numberOfCompactions.setText("0");
    }

}

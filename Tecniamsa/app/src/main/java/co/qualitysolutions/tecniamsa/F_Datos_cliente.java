package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utilidades.SaveInformation;
import utilidades.Utilities;


public class F_Datos_cliente extends Activity {

    private TextView codigo_cliente, nombre_cliente, direccion_cliente, jornada_cliente, hoja_cliente, entrega_cliente, cargo_cliente, observacion_cliente;
    private Spinner spinner_estado;
    private JSONArray clientesPlaneados;
    private JSONObject clienteSeleccionado;
    private SharedPreferences sharedpreferences;
    private String method;
    private String methodInt;
    private TextView date;
    private JSONArray send_data_json;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f_datos_cliente);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        inicializarComponentes();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }



    public void inicializarComponentes() {

        this.date = (TextView)findViewById(R.id.dateNow);
        this.date.setText(this.sharedpreferences.getString("FECHA_SERVER", Utilities.getDate().split(" ")[0]));

        this.codigo_cliente = (TextView) findViewById(R.id.codigo_cliente);
        this.nombre_cliente = (TextView) findViewById(R.id.nombre_cliente);
        this.direccion_cliente = (TextView) findViewById(R.id.direccion_cliente);
        this.jornada_cliente = (TextView) findViewById(R.id.jornada_cliente);
        this.hoja_cliente = (TextView) findViewById(R.id.hoja_cliente);
        this.entrega_cliente = (TextView) findViewById(R.id.entrega_cliente);
        this.cargo_cliente = (TextView) findViewById(R.id.cargo_cliente);
        this.observacion_cliente = (TextView) findViewById(R.id.observacion_cliente);
        this.spinner_estado = (Spinner) findViewById(R.id.spinner_estado);

        try {
            this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
            this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
            this.codigo_cliente.setText(this.clienteSeleccionado.getString("codigo_cliente"));
            this.nombre_cliente.setText(this.clienteSeleccionado.getString("nombre_cliente"));
            this.direccion_cliente.setText(this.clienteSeleccionado.getString("direccion"));
            this.jornada_cliente.setText("No definida");
            this.hoja_cliente.setText(this.clienteSeleccionado.getString("hoja"));
            this.entrega_cliente.setText("No definida");
            this.cargo_cliente.setText("No definida");
            String observacion = this.clienteSeleccionado.getString("observacion");
            if (!observacion.equals(""))
                this.observacion_cliente.setText(observacion);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*public void startRoute(View view) {


        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        if (this.clienteSeleccionado != null) {
            try {
                SharedPreferences.Editor editor = this.sharedpreferences.edit();
                if (this.clienteSeleccionado.getString("estado").equals("inactiva")) {
                    this.clienteSeleccionado.put("estado", "iniciada");
                    this.clienteSeleccionado.put("fecha_inicio", Utilities.getDate());
                    this.clienteSeleccionado.put("estado", String.valueOf(this.spinner_estado.getSelectedItemPosition()));
                    this.clienteSeleccionado.put("observacion", this.observacion_cliente.getText());
                    this.clientesPlaneados.put(sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0), this.clienteSeleccionado);

                    editor.putString("PLANNED_CLIENTS", this.clientesPlaneados.toString());
                    editor.commit();
                    adb.setTitle("DESEA INICIAR LA RUTA  " + this.clienteSeleccionado.getString("hoja"));
                    this.method = "iniciar_porte";
                    this.methodInt = "1";
                } else {
                    adb.setTitle("DESEA CONTINUAR LA RUTA " + this.clienteSeleccionado.getString("hoja"));
                    this.method = "continuar_porte";
                    this.methodInt = "3";
                }
                //editor.putInt("POS_CURRENT_ROUTE", this.routePosition);
                editor.putInt("CURRENT_STATE", 2);
                editor.commit();
                adb.setPositiveButton(
                        getResources().getString(R.string.confirm_button_1),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();


                                send_data_json = new JSONArray();
                                JSONArray auxjson = new JSONArray();
                                JSONArray auxjson2 = new JSONArray();
                                JSONObject auxobject = new JSONObject();

                                try {
                                    auxjson2 = new JSONArray(sharedpreferences.getString("TRUCK_INFO", null));
                                    auxobject = new JSONObject();
                                    auxobject.put("fecha_hora_evento", Utilities.getDate());
                                    auxobject.put("metodo", method);

                                    send_data_json.put(auxobject);
                                    send_data_json.put(clienteSeleccionado);
                                    if (method.equals("iniciar_porte")) {
                                        auxjson = new JSONArray(sharedpreferences.getString("SELECT_OPERATORS", null));
                                        auxobject = new JSONObject();
                                        auxobject.put("operators_select", auxjson);
                                        send_data_json.put(auxobject);
                                    }
                                    send_data_json.put(auxjson2.get(0));
                                } catch (JSONException e) {

                                }
                                sendInformation();
                                Intent intent = new Intent();
                                setResult(2, intent);
                                finish();
                            }
                        });
                adb.setNegativeButton(
                        getResources().getString(R.string.confirm_button_2),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                adb.show();
            } catch (JSONException e) {
            }
        } else {
            Utilities.showAlert(this, "Seleccione un cliente por favor en la lista anterior");
        }
    }*/

    public void startRoute(View view) {


        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Alert!");
            try {
                SharedPreferences.Editor editor = this.sharedpreferences.edit();

                clienteSeleccionado.put("estado", "iniciada");
                clienteSeleccionado.put("fecha_inicio", Utilities.getDate());
                clienteSeleccionado.put("estado", String.valueOf(this.spinner_estado.getSelectedItemPosition()));
                clienteSeleccionado.put("observacion", this.observacion_cliente.getText());
                clientesPlaneados.put(sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0), this.clienteSeleccionado);
                this.method = "json_tecni_inicioporte";
                this.methodInt = "46";

                editor.putString("PLANNED_CLIENTS", this.clientesPlaneados.toString());
                editor.commit();
                adb.setTitle("DESEA INICIAR LA RUTA  " + this.clienteSeleccionado.getString("hoja"));
                editor.putInt("CURRENT_STATE", 2);
                editor.commit();
                adb.setPositiveButton(
                        getResources().getString(R.string.confirm_button_1),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                    send_data_json = new JSONArray();
                                    JSONArray auxjson = new JSONArray();
                                    JSONArray auxjson2 = new JSONArray();
                                    JSONObject auxobject = new JSONObject();

                                    try {
                                        auxjson2 = new JSONArray(sharedpreferences.getString("TRUCK_INFO", null));
                                        auxobject = new JSONObject();
                                        auxobject.put("fecha_hora_evento", Utilities.getDate());
                                        auxobject.put("metodo", method);

                                        send_data_json.put(auxobject);
                                        send_data_json.put(clienteSeleccionado);

                                            auxjson = new JSONArray(sharedpreferences.getString("SELECT_OPERATORS", null));
                                            auxobject = new JSONObject();
                                            auxobject.put("operators_select", auxjson);
                                            send_data_json.put(auxobject);

                                        send_data_json.put(auxjson2.get(0));
                                    } catch (JSONException e) {

                                    }

                                sendInformation();
                                Intent intent = new Intent();
                                setResult(2, intent);
                                finish();
                                dialog.dismiss();
                            }
                        });
                adb.setNegativeButton(
                        getResources().getString(R.string.confirm_button_2),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                adb.show();
            } catch (JSONException e) {
            }
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
     * Method to create a new json to save the information and send it to server
     */
    public void sendInformation(){
        new SaveInformation(this).execute("http://www.concesionesdeaseo.com/gruposala/FUNEventosMovil/Eventos",
                this.methodInt,
                this.method,
                this.send_data_json.toString());
    }


}

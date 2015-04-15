package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utilidades.SaveInformation;
import utilidades.Utilities;


public class B_MenuPrincipal extends Activity {

    private SharedPreferences sharedpreferences;
    private JSONArray send_data_json;
    private String method;
    private String methodInt;
    private int posCurrentRout;
    private Button btn_grupo_trabajo,btn_iniciar_dia,btn_cerrar_dia,btn_combustible,btn_peaje,btn_mapa;
    private JSONArray operariosSelect;
    private TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_menu_principal);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.inizializarComponentes();
       /* SharedPreferences.Editor editor = sharedpreferences.edit();
        String datos="[{'hoja':'HREC_10','placa':'NAF 206','codigo_cliente':'1','nombre_cliente':'DROGUERIA CARACAS','estado':'2','fecha':'3\\/31\\/2015 12:00:00 AM','direccion':'AV 5  8-96 CENTRO','ciudad':'cucuta','departamento':'','latitud':'-72.480578349','longitud':'7.895862627','residuo':'Concreto','traza':'12','orden':'1','lsttrazas':[{'traza':'11','hoja':'HREC_10','definicion':'1122','nombre':'Bio','descripcion':'Biologicos','residuo':'Bloques','lstembalaje':[{'traza':'11','embalaje':'01','definicion':'2211','cantidad':'20','peso':'22','nombre':'Med Amb','descripcion':'Medio Ambiente','pesoTotal':77.1,'pesos_embalaje':[{'peso_asignado':0.4},{'peso_asignado':0.4},{'peso_asignado':0.4},{'peso_asignado':0.4},{'peso_asignado':0.4},{'peso_asignado':0.4},{'peso_asignado':0.4},{'peso_asignado':0.4},{'peso_asignado':0.4},{'peso_asignado':0.2},{'peso_asignado':0.2},{'peso_asignado':0.2},{'peso_asignado':0.2},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25}],'barras_embalaje':[{'barra_asignada':359350050079405}]},{'traza':'11','embalaje':'02','definicion':'2222','cantidad':'22','peso':'120','nombre':'Mad','descripcion':'Maderas','pesoTotal':0}]},{'traza':'12','hoja':'HREC_10','definicion':'1133','nombre':'Med','descripcion':'Medicos','residuo':'Concreto','lstembalaje':[{'traza':'12','embalaje':'03','definicion':'2211','cantidad':'23','peso':'120','nombre':'Med Amb','descripcion':'Medio Ambiente'}]}],'observacion':'   lohola'},{'hoja':'HREC_10','placa':'NAF 206','codigo_cliente':'1','nombre_cliente':'DROGUERIA CARACAS','estado':'0','fecha':'3\\/31\\/2015 12:00:00 AM','direccion':'AV 5  8-96 CENTRO','ciudad':'cucuta','departamento':'','latitud':'-72.480578349','longitud':'7.895862627','residuo':'Bloques','traza':'11','orden':'1','lsttrazas':[{'traza':'11','hoja':'HREC_10','definicion':'1122','nombre':'Bio','descripcion':'Biologicos','residuo':'Bloques','lstembalaje':[{'traza':'11','embalaje':'01','definicion':'2211','cantidad':'20','peso':'22','nombre':'Med Amb','descripcion':'Medio Ambiente'},{'traza':'11','embalaje':'02','definicion':'2222','cantidad':'22','peso':'120','nombre':'Mad','descripcion':'Maderas'}]},{'traza':'12','hoja':'HREC_10','definicion':'1133','nombre':'Med','descripcion':'Medicos','residuo':'Concreto','lstembalaje':[{'traza':'12','embalaje':'03','definicion':'2211','cantidad':'23','peso':'120','nombre':'Med Amb','descripcion':'Medio Ambiente'}]}],'observacion':''},{'hoja':'HREC_11','placa':'NAF 206','codigo_cliente':'2','nombre_cliente':'PUESTO DE SALUD CUNDINAMARCA','estado':'2','fecha':'3\\/31\\/2015 12:00:00 AM','direccion':'CL 12 22-70 CUNDINAMARCA','ciudad':'cucuta','departamento':'','latitud':'-72.5419785668','longitud':'7.89425107821','residuo':'Elementos Perecederos','traza':'13','orden':'2','lsttrazas':[{'traza':'13','hoja':'HREC_11','definicion':'1133','nombre':'Med','descripcion':'Medicos','residuo':'Elementos Perecederos','lstembalaje':[]}],'observacion':''},{'hoja':'HREC_31','placa':'NAF 206','codigo_cliente':'1100','nombre_cliente':'INSUMOS GRAJALES','estado':'1','fecha':'3\\/31\\/2015 12:00:00 PM','direccion':'AV 99 3-33 NORTE','ciudad':'cucuta','departamento':'','latitud':'-99.480578349','longitud':'9.895862627','residuo':'PRUEBA RESIDUO2','traza':'2367','orden':'5','lsttrazas':[{'traza':'2366','hoja':'HREC_31','definicion':'1122','nombre':'Bio','descripcion':'Biologicos','residuo':'PRUEBA RESIDUO','lstembalaje':[{'traza':'2366','embalaje':'EMB_6','definicion':'2211','cantidad':'1','peso':'2211','nombre':'Med Amb','descripcion':'Medio Ambiente'},{'traza':'2366','embalaje':'EMB_7','definicion':'2233','cantidad':'2','peso':'200','nombre':'Met','descripcion':'Metales'}]},{'traza':'2367','hoja':'HREC_31','definicion':'1133','nombre':'Med','descripcion':'Medicos','residuo':'PRUEBA RESIDUO2','lstembalaje':[{'traza':'2367','embalaje':'EMB_8','definicion':'2233','cantidad':'20','peso':'100','nombre':'Met','descripcion':'Metales'}]}],'observacion':'hola'},{'hoja':'HREC_31','placa':'NAF 206','codigo_cliente':'1100','nombre_cliente':'INSUMOS GRAJALES','estado':'1','fecha':'3\\/31\\/2015 12:00:00 PM','direccion':'AV 99 3-33 NORTE','ciudad':'cucuta','departamento':'','latitud':'-99.480578349','longitud':'9.895862627','residuo':'PRUEBA RESIDUO','traza':'2366','orden':'5','lsttrazas':[{'traza':'2366','hoja':'HREC_31','definicion':'1122','nombre':'Bio','descripcion':'Biologicos','residuo':'PRUEBA RESIDUO','lstembalaje':[{'traza':'2366','embalaje':'EMB_6','definicion':'2211','cantidad':'1','peso':'2211','nombre':'Med Amb','descripcion':'Medio Ambiente','pesoTotal':0.30000000000000004},{'traza':'2366','embalaje':'EMB_7','definicion':'2233','cantidad':'2','peso':'200','nombre':'Met','descripcion':'Metales'}]},{'traza':'2367','hoja':'HREC_31','definicion':'1133','nombre':'Med','descripcion':'Medicos','residuo':'PRUEBA RESIDUO2','lstembalaje':[{'traza':'2367','embalaje':'EMB_8','definicion':'2233','cantidad':'20','peso':'100','nombre':'Met','descripcion':'Metales'}]}],'observacion':''},{'hoja':'HREC_18','placa':'NAF 206','codigo_cliente':'5','nombre_cliente':'PUESTO DE SALUD LA DIVINA PAST','estado':'0','fecha':'3\\/31\\/2015 12:00:00 AM','direccion':'CL 31  39-15 DIVINA PASTORA','ciudad':'cucuta','departamento':'','latitud':'-72.53646276','longitud':'7.87688609262','residuo':'Bloques','traza':'18','orden':'4','lsttrazas':[{'traza':'17','hoja':'HREC_18','definicion':'1133','nombre':'Med','descripcion':'Medicos','residuo':'Construccion','lstembalaje':[{'traza':'17','embalaje':'04','definicion':'2233','cantidad':'10','peso':'100','nombre':'Met','descripcion':'Metales'}]},{'traza':'18','hoja':'HREC_18','definicion':'1122','nombre':'Bio','descripcion':'Biologicos','residuo':'Bloques','lstembalaje':[{'traza':'18','embalaje':'05','definicion':'2222','cantidad':'5','peso':'80','nombre':'Mad','descripcion':'Maderas'}]}],'observacion':'se'},{'hoja':'HREC_18','placa':'NAF 206','codigo_cliente':'5','nombre_cliente':'PUESTO DE SALUD LA DIVINA PAST','estado':'0','fecha':'3\\/31\\/2015 12:00:00 AM','direccion':'CL 31  39-15 DIVINA PASTORA','ciudad':'cucuta','departamento':'','latitud':'-72.53646276','longitud':'7.87688609262','residuo':'Construccion','traza':'17','orden':'4','lsttrazas':[{'traza':'17','hoja':'HREC_18','definicion':'1133','nombre':'Med','descripcion':'Medicos','residuo':'Construccion','lstembalaje':[{'traza':'17','embalaje':'04','definicion':'2233','cantidad':'10','peso':'100','nombre':'Met','descripcion':'Metales'}]},{'traza':'18','hoja':'HREC_18','definicion':'1122','nombre':'Bio','descripcion':'Biologicos','residuo':'Bloques','lstembalaje':[{'traza':'18','embalaje':'05','definicion':'2222','cantidad':'5','peso':'80','nombre':'Mad','descripcion':'Maderas'}]}]}]";
        editor.putString("PLANNED_CLIENTS",datos);
        editor.commit();*/
    }

    @Override
    protected void onResume() {

        try {
            this.operariosSelect = new JSONArray(this.sharedpreferences.getString("SELECT_OPERATORS","[]"));

        } catch (JSONException e) {
            this.operariosSelect = new JSONArray();
        }

        super.onResume();
    }

    public void inizializarComponentes(){

        this.btn_grupo_trabajo = (Button) findViewById(R.id.btn_grupo_trabajo);
        this.btn_iniciar_dia = (Button) findViewById(R.id.btn_iniciar_dia);
        this.btn_cerrar_dia = (Button) findViewById(R.id.btn_cerrar_dia);
        this.btn_combustible = (Button) findViewById(R.id.btn_combustible);
        this.btn_peaje = (Button) findViewById(R.id.btn_peaje);
        this.btn_mapa = (Button) findViewById(R.id.btn_mapa);
        this.date = (TextView)findViewById(R.id.dateNow);
        this.date.setText(this.sharedpreferences.getString("FECHA_SERVER", Utilities.getDate().split(" ")[0]));

        if(sharedpreferences.getBoolean("CLOSE_DAY",false))
            bloquearMenu();

        try {
            this.operariosSelect = new JSONArray(this.sharedpreferences.getString("SELECT_OPERATORS","[]"));

        } catch (JSONException e) {
            this.operariosSelect = new JSONArray();
        }


    }

    /**
     * Method to start day of work, if the hour meter today is empty, the system
     * displays the truck information form interface. Else displays the cycle
     * menu interface.
     *
     * @param v
     */
    public void startDay(View v) {



        if(this.operariosSelect.length()>0 && sharedpreferences.getInt("EMPEZO_JORNADA",0)==1){

            int newOrometer;
            try {
                JSONArray truckInformation = new JSONArray((String) sharedpreferences.getString("TRUCK_INFO", ""));
                JSONObject truckObject = truckInformation.getJSONObject(0);
                newOrometer = truckObject.getInt("nuevo_horometro");
            } catch (Exception e){
                newOrometer = -1;
            }
            if (newOrometer != -1){
                // Interfaz para continuar con el ciclo
                Intent intent = new Intent(this, E_MenuCiclo.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, D_RegistrarDatosVehiculo.class);
                startActivity(intent);
            }

        }

        else{

            Utilities.showAlert(this,"Debe agregar los operarios en el item grupo de trabajo y empezar la jornada, para poder iniciar dia");

        }



    }

    /**
     * Method to display the ticket gasoline register interface
     *
     * @param v
     */
    public void registerGas(View v) {
        Intent intent = new Intent(this, J_Combustible.class);
        startActivity(intent);
    }

    /**
     * Method to display the ticket toll register interface
     *
     * @param v
     */
    public void registerToll(View v) {
        Intent intent = new Intent(this, K_Peaje.class);
        startActivity(intent);
    }

    /**
     * Method to start day of work, if the hour meter today is empty, the system
     * displays the truck information form interface. Else displays the cycle
     * menu interface.
     *
     * @param v
     */
    public void grupoDeTrabajo(View v) {

        Intent intent = new Intent(this, C_GrupoTrabajo.class);
        startActivity(intent);

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
     * Method to display the close route interface
     *
     * @param v
     */
    public void closeDay(View v){
        posCurrentRout = sharedpreferences.getInt("CLIENTE_SELECCIONADO", -1);
        if(posCurrentRout==-1){
            Utilities.showAlert(this, "Debe al menos haber atendido un clinete para finalizar el dia");
        }
        else{
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Alert!");
            adb.setMessage(getResources().getString(R.string.AreYouSureCloseDay));
            adb.setPositiveButton(getResources().getString(R.string.confirm_button_1),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String selectedOperators = sharedpreferences.getString("SELECT_OPERATORS",null);
                            String horaFin= Utilities.getDate();
                            try {
                                if(selectedOperators!=null){
                                    JSONArray savedOperators = new JSONArray(selectedOperators);

                                    for(int i=0; i<savedOperators.length(); i++){
                                        if(savedOperators.getJSONObject(i).getString("hora_fin").equals("")){
                                            savedOperators.getJSONObject(i).put("hora_fin", horaFin);
                                        }
                                    }
                                    JSONArray plannedClients = new JSONArray(sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
                                    JSONArray truckInfo = new JSONArray(sharedpreferences.getString("TRUCK_INFO", "[]"));
                                    send_data_json = new JSONArray();
                                    JSONObject aux = new JSONObject();
                                    aux.put("clientes_planeados", plannedClients);
                                    aux.put("operarios_fin_jornada", savedOperators);
                                    send_data_json.put(aux);
                                    send_data_json.put(truckInfo.get(0));

                                    methodInt="50";
                                    method="json_tecni_cerrardia";

                                    bloquearMenu();
                                    sendInformation();

                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putBoolean("CLOSE_DAY",true);
                                    editor.commit();

                                    Toast.makeText(getApplicationContext(), "El dia ha sido cerrado satisfactoriamente", Toast.LENGTH_LONG).show();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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

    }

    public void bloquearMenu(){

        this.btn_grupo_trabajo.setBackgroundColor(getResources().getColor(R.color.gray));
        this.btn_grupo_trabajo.setEnabled(false);

        this.btn_iniciar_dia.setBackgroundColor(getResources().getColor(R.color.gray));
        this.btn_iniciar_dia.setEnabled(false);

        this.btn_cerrar_dia.setBackgroundColor(getResources().getColor(R.color.gray));
        this.btn_cerrar_dia.setEnabled(false);

    }


    /**
     * Method to send information
     */
    public void sendInformation(){
        new SaveInformation(this).execute("http://www.concesionesdeaseo.com/gruposala/FUNEventosMovil/Eventos",
                methodInt,
                method,
                send_data_json.toString());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        moveTaskToBack(true);
        return super.onKeyDown(keyCode, event);
    }

}

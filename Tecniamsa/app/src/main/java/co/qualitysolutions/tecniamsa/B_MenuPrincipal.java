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
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import utilidades.SaveInformation;


public class B_MenuPrincipal extends Activity {

    private SharedPreferences sharedpreferences;
    private JSONArray send_data_json;
    private String method;
    private String methodInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_menu_principal);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
       /* SharedPreferences.Editor editor = sharedpreferences.edit();
        String datos="[{'hoja':'HREC_10','placa':'NAF 206','codigo_cliente':'1','nombre_cliente':'DROGUERIA CARACAS','estado':'2','fecha':'3\\/31\\/2015 12:00:00 AM','direccion':'AV 5  8-96 CENTRO','ciudad':'cucuta','departamento':'','latitud':'-72.480578349','longitud':'7.895862627','residuo':'Concreto','traza':'12','orden':'1','lsttrazas':[{'traza':'11','hoja':'HREC_10','definicion':'1122','nombre':'Bio','descripcion':'Biologicos','residuo':'Bloques','lstembalaje':[{'traza':'11','embalaje':'01','definicion':'2211','cantidad':'20','peso':'22','nombre':'Med Amb','descripcion':'Medio Ambiente','pesoTotal':77.1,'pesos_embalaje':[{'peso_asignado':0.4},{'peso_asignado':0.4},{'peso_asignado':0.4},{'peso_asignado':0.4},{'peso_asignado':0.4},{'peso_asignado':0.4},{'peso_asignado':0.4},{'peso_asignado':0.4},{'peso_asignado':0.4},{'peso_asignado':0.2},{'peso_asignado':0.2},{'peso_asignado':0.2},{'peso_asignado':0.2},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25},{'peso_asignado':0.25}],'barras_embalaje':[{'barra_asignada':359350050079405}]},{'traza':'11','embalaje':'02','definicion':'2222','cantidad':'22','peso':'120','nombre':'Mad','descripcion':'Maderas','pesoTotal':0}]},{'traza':'12','hoja':'HREC_10','definicion':'1133','nombre':'Med','descripcion':'Medicos','residuo':'Concreto','lstembalaje':[{'traza':'12','embalaje':'03','definicion':'2211','cantidad':'23','peso':'120','nombre':'Med Amb','descripcion':'Medio Ambiente'}]}],'observacion':'   lohola'},{'hoja':'HREC_10','placa':'NAF 206','codigo_cliente':'1','nombre_cliente':'DROGUERIA CARACAS','estado':'0','fecha':'3\\/31\\/2015 12:00:00 AM','direccion':'AV 5  8-96 CENTRO','ciudad':'cucuta','departamento':'','latitud':'-72.480578349','longitud':'7.895862627','residuo':'Bloques','traza':'11','orden':'1','lsttrazas':[{'traza':'11','hoja':'HREC_10','definicion':'1122','nombre':'Bio','descripcion':'Biologicos','residuo':'Bloques','lstembalaje':[{'traza':'11','embalaje':'01','definicion':'2211','cantidad':'20','peso':'22','nombre':'Med Amb','descripcion':'Medio Ambiente'},{'traza':'11','embalaje':'02','definicion':'2222','cantidad':'22','peso':'120','nombre':'Mad','descripcion':'Maderas'}]},{'traza':'12','hoja':'HREC_10','definicion':'1133','nombre':'Med','descripcion':'Medicos','residuo':'Concreto','lstembalaje':[{'traza':'12','embalaje':'03','definicion':'2211','cantidad':'23','peso':'120','nombre':'Med Amb','descripcion':'Medio Ambiente'}]}],'observacion':''},{'hoja':'HREC_11','placa':'NAF 206','codigo_cliente':'2','nombre_cliente':'PUESTO DE SALUD CUNDINAMARCA','estado':'2','fecha':'3\\/31\\/2015 12:00:00 AM','direccion':'CL 12 22-70 CUNDINAMARCA','ciudad':'cucuta','departamento':'','latitud':'-72.5419785668','longitud':'7.89425107821','residuo':'Elementos Perecederos','traza':'13','orden':'2','lsttrazas':[{'traza':'13','hoja':'HREC_11','definicion':'1133','nombre':'Med','descripcion':'Medicos','residuo':'Elementos Perecederos','lstembalaje':[]}],'observacion':''},{'hoja':'HREC_31','placa':'NAF 206','codigo_cliente':'1100','nombre_cliente':'INSUMOS GRAJALES','estado':'1','fecha':'3\\/31\\/2015 12:00:00 PM','direccion':'AV 99 3-33 NORTE','ciudad':'cucuta','departamento':'','latitud':'-99.480578349','longitud':'9.895862627','residuo':'PRUEBA RESIDUO2','traza':'2367','orden':'5','lsttrazas':[{'traza':'2366','hoja':'HREC_31','definicion':'1122','nombre':'Bio','descripcion':'Biologicos','residuo':'PRUEBA RESIDUO','lstembalaje':[{'traza':'2366','embalaje':'EMB_6','definicion':'2211','cantidad':'1','peso':'2211','nombre':'Med Amb','descripcion':'Medio Ambiente'},{'traza':'2366','embalaje':'EMB_7','definicion':'2233','cantidad':'2','peso':'200','nombre':'Met','descripcion':'Metales'}]},{'traza':'2367','hoja':'HREC_31','definicion':'1133','nombre':'Med','descripcion':'Medicos','residuo':'PRUEBA RESIDUO2','lstembalaje':[{'traza':'2367','embalaje':'EMB_8','definicion':'2233','cantidad':'20','peso':'100','nombre':'Met','descripcion':'Metales'}]}],'observacion':'hola'},{'hoja':'HREC_31','placa':'NAF 206','codigo_cliente':'1100','nombre_cliente':'INSUMOS GRAJALES','estado':'1','fecha':'3\\/31\\/2015 12:00:00 PM','direccion':'AV 99 3-33 NORTE','ciudad':'cucuta','departamento':'','latitud':'-99.480578349','longitud':'9.895862627','residuo':'PRUEBA RESIDUO','traza':'2366','orden':'5','lsttrazas':[{'traza':'2366','hoja':'HREC_31','definicion':'1122','nombre':'Bio','descripcion':'Biologicos','residuo':'PRUEBA RESIDUO','lstembalaje':[{'traza':'2366','embalaje':'EMB_6','definicion':'2211','cantidad':'1','peso':'2211','nombre':'Med Amb','descripcion':'Medio Ambiente','pesoTotal':0.30000000000000004},{'traza':'2366','embalaje':'EMB_7','definicion':'2233','cantidad':'2','peso':'200','nombre':'Met','descripcion':'Metales'}]},{'traza':'2367','hoja':'HREC_31','definicion':'1133','nombre':'Med','descripcion':'Medicos','residuo':'PRUEBA RESIDUO2','lstembalaje':[{'traza':'2367','embalaje':'EMB_8','definicion':'2233','cantidad':'20','peso':'100','nombre':'Met','descripcion':'Metales'}]}],'observacion':''},{'hoja':'HREC_18','placa':'NAF 206','codigo_cliente':'5','nombre_cliente':'PUESTO DE SALUD LA DIVINA PAST','estado':'0','fecha':'3\\/31\\/2015 12:00:00 AM','direccion':'CL 31  39-15 DIVINA PASTORA','ciudad':'cucuta','departamento':'','latitud':'-72.53646276','longitud':'7.87688609262','residuo':'Bloques','traza':'18','orden':'4','lsttrazas':[{'traza':'17','hoja':'HREC_18','definicion':'1133','nombre':'Med','descripcion':'Medicos','residuo':'Construccion','lstembalaje':[{'traza':'17','embalaje':'04','definicion':'2233','cantidad':'10','peso':'100','nombre':'Met','descripcion':'Metales'}]},{'traza':'18','hoja':'HREC_18','definicion':'1122','nombre':'Bio','descripcion':'Biologicos','residuo':'Bloques','lstembalaje':[{'traza':'18','embalaje':'05','definicion':'2222','cantidad':'5','peso':'80','nombre':'Mad','descripcion':'Maderas'}]}],'observacion':'se'},{'hoja':'HREC_18','placa':'NAF 206','codigo_cliente':'5','nombre_cliente':'PUESTO DE SALUD LA DIVINA PAST','estado':'0','fecha':'3\\/31\\/2015 12:00:00 AM','direccion':'CL 31  39-15 DIVINA PASTORA','ciudad':'cucuta','departamento':'','latitud':'-72.53646276','longitud':'7.87688609262','residuo':'Construccion','traza':'17','orden':'4','lsttrazas':[{'traza':'17','hoja':'HREC_18','definicion':'1133','nombre':'Med','descripcion':'Medicos','residuo':'Construccion','lstembalaje':[{'traza':'17','embalaje':'04','definicion':'2233','cantidad':'10','peso':'100','nombre':'Met','descripcion':'Metales'}]},{'traza':'18','hoja':'HREC_18','definicion':'1122','nombre':'Bio','descripcion':'Biologicos','residuo':'Bloques','lstembalaje':[{'traza':'18','embalaje':'05','definicion':'2222','cantidad':'5','peso':'80','nombre':'Mad','descripcion':'Maderas'}]}]}]";
        editor.putString("PLANNED_CLIENTS",datos);
        editor.commit();*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_b__menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to start day of work, if the hour meter today is empty, the system
     * displays the truck information form interface. Else displays the cycle
     * menu interface.
     *
     * @param v
     */
    public void startDay(View v) {

        /*int newOrometer;
        try {
            JSONArray truckInformation = new JSONArray((String) sharedpreferences.getString("TRUCK_INFO", ""));
            JSONObject truckObject = truckInformation.getJSONObject(0);
            newOrometer = truckObject.getInt("nuevo_horometro");
        } catch (Exception e){
            newOrometer = -1;
        }
        if (newOrometer != -1){
            // Interfaz para continuar con el siclo
            Intent intent = new Intent(this, E_MenuCiclo.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, D_RegistrarDatosVehiculo.class);
            startActivity(intent);
        }*/

        Intent intent = new Intent(this, E_MenuCiclo.class);
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

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(),A_Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
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

                 /*if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                 return true;
             }
                  */
        moveTaskToBack(true);
        return super.onKeyDown(keyCode, event);
    }

}

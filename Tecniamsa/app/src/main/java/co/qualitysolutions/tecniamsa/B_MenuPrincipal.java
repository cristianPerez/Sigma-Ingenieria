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

import org.json.JSONArray;

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

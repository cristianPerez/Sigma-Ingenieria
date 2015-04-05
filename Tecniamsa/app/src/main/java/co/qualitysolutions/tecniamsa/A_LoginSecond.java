package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;
import utilidades.Utilities;


public class A_LoginSecond extends Activity implements AdapterView.OnItemClickListener {

    private SharedPreferences sharedpreferences;
    private ListView listTruck;
    private JSONArray jsonListTruck;
    private TextView placa,numeroInterno;
    private AlertDialog.Builder adb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.a_login_second);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.placa = (TextView) findViewById(R.id.placa);
        this.numeroInterno = (TextView) findViewById(R.id.numeroInterno);
        this.listTruck = (ListView) findViewById(R.id.ListTruck);
        this.listTruck.setOnItemClickListener(this);
        this.adb = new AlertDialog.Builder(this);
        try {
            this.jsonListTruck = new JSONArray(this.sharedpreferences.getString("TRUCK_LIST","[]"));
            diaplayListAllTrucks();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void diaplayListAllTrucks(){
        List<String> lstNames = new ArrayList<String>();
        for(int i=0; i<this.jsonListTruck.length(); i++){
            try {
                lstNames.add(this.jsonListTruck.getJSONObject(i).getString("numero_interno2"));
            } catch (JSONException e) {
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstNames);
        this.listTruck.setAdapter(adapter);
    }

    /**
     * Override method to hide the keyboard when the user touch outside of an
     * element
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    public void startSession(View v){
        if(!this.placa.getText().toString().equals("XXX-XXX") && !this.numeroInterno.getText().toString().equals("XX-XX")){
            adb.setTitle(getResources().getString(R.string.alertMensaje));
            adb.setMessage(getResources().getString(R.string.placaConfirm)+" "+this.placa.getText().toString() +" "+getResources().getString(R.string.numeroInternoConfirm) +" "+this.numeroInterno.getText().toString());
            adb.setPositiveButton(
                    getResources().getString(R.string.confirm_button_1),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("PLACA",placa.getText().toString());
                            editor.putString("NUMERO_INTERNO",numeroInterno.getText().toString());
                            editor.commit();
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
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                    });
            adb.show();
        }
        else{
            Utilities.showAlert(this, "Seleccione un vehículo");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        try {
            this.placa.setText(this.jsonListTruck.getJSONObject(position).getString("placa2").toString());
            this.numeroInterno.setText(this.jsonListTruck.getJSONObject(position).getString("numero_interno2").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

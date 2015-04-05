package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class F_Datos_cliente extends Activity {

    private TextView codigo_cliente,nombre_cliente, direccion_cliente,jornada_cliente,hoja_cliente,entrega_cliente,cargo_cliente,observacion_cliente;
    private Spinner spinner_estado;
    private JSONArray clientesPlaneados;
    private JSONObject clienteSeleccionado;
    private SharedPreferences sharedpreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f_datos_cliente);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        inicializarComponentes();
    }


    public void inicializarComponentes() {

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
            this.spinner_estado.setSelection(Integer.parseInt(this.clienteSeleccionado.getString("estado")));
            String observacion = this.clienteSeleccionado.getString("observacion");
            if(!observacion.equals(""))
                this.observacion_cliente.setText(observacion);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

        public void guardarCliente(View view){

            try {
                this.clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0)).put("estado",String.valueOf(this.spinner_estado.getSelectedItemPosition()));
                this.clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0)).put("observacion",this.observacion_cliente.getText());
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("PLANNED_CLIENTS",this.clientesPlaneados.toString());
                editor.commit();
                Toast.makeText(this,"Cliente almacenado con exito!",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, G_TrazasEmbalaje.class);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }





}

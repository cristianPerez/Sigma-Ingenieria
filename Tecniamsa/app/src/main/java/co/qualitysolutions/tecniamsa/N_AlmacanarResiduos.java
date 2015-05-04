package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utilidades.ItemAdapterJsonCodigos;


public class N_AlmacanarResiduos extends Activity {


    private SharedPreferences sharedpreferences;
    private JSONArray lstBarrasAlmacen;
    private ListView list_barras_alamacen;
    private ItemAdapterJsonCodigos adapterJson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.n_almacanar_residuos);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        inicializarComponenetes();
    }

    public void inicializarComponenetes(){

        this.list_barras_alamacen = (ListView) findViewById(R.id.list_barras_alamacen);
        this.lstBarrasAlmacen = new JSONArray();


    }

    public void registrarBarras(View view)
    {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "ONE_D_MODE");
        startActivityForResult(intent, 0);
    }

    public void almacenar(View view)
    {

        Toast.makeText(this,"Los residuos fueron almacenados satisfactoriamente",Toast.LENGTH_LONG).show();
        finish();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                Log.d("SCAN->", contents + "|" + format);
                //int cont=Integer.parseInt(txtTotalCantidadLeida.getText().toString());
                //cont++;
                JSONObject nuevaBarra= new JSONObject();
                try {
                    nuevaBarra.put("barra_asignada",contents);
                    lstBarrasAlmacen.put(nuevaBarra);
                    //embalajeSeleccionado.put("barras_embalaje",listaBarrasPorEmbalaje);
                    //embalajeSeleccionado.put("cantTotal",cont);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /*SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("PLANNED_CLIENTS",clientesPlaneados.toString());
                editor.commit();*/
                actionAdapter();
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("SCAN->","Cancelado");
            }
        }

    }

    public void actionAdapter(){

        this.adapterJson = new ItemAdapterJsonCodigos(this, lstBarrasAlmacen);
        this.list_barras_alamacen.setAdapter(adapterJson);
    }

}

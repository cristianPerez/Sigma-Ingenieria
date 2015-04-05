package co.qualitysolutions.tecniamsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import utilidades.ItemAdapter;
import utilidades.ItemAdapterJson;
import utilidades.Utilities;

public class RegistrarPeso extends Activity{

    private ListView listView;

    //public static ArrayList<Item> view_pesos;
    public static JSONArray view_pesos_json;

    //private ItemAdapter adapter;
    private ItemAdapterJson adapterJson;

    private int cont = 1;
    private TextView tipoEmbalaje,pesoTotal;
    private JSONArray clientesPlaneados,listaTrazas,listaEmbalajes;
    private JSONObject clienteSeleccionado,trazaSeleccionada,embalajeSeleccionado;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_peso);
        this.sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        this.inicializarComponentes();
    }

    public void inicializarComponentes(){
        listView = (ListView) findViewById(R.id.list_pesos);
        //listView.setOnItemClickListener(this);

        //view_pesos = new ArrayList<Item>();


        this.tipoEmbalaje = (TextView) findViewById(R.id.tipoEmbalaje);
        this.pesoTotal = (TextView)findViewById(R.id.peso_total);
        try {
            this.clientesPlaneados = new JSONArray(this.sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
            this.clienteSeleccionado = clientesPlaneados.getJSONObject(this.sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
            this.listaTrazas = this.clienteSeleccionado.getJSONArray("lsttrazas");
            this.trazaSeleccionada = listaTrazas.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA", 0));
            this.listaEmbalajes = this.trazaSeleccionada.getJSONArray("lstembalaje");
            this.embalajeSeleccionado = this.listaEmbalajes.getJSONObject(this.sharedpreferences.getInt("SELECT_EMBALAJE", 0));
            this.tipoEmbalaje.setText(this.embalajeSeleccionado.getString("nombre"));
            this.pesoTotal.setText(this.embalajeSeleccionado.getDouble("pesoTotal")+" kg");
            this.actionAdapter();
        }catch (JSONException e) {
            this.pesoTotal.setText("0kg");
            e.printStackTrace();
        }
    }


    public void buscar_dispositivo(View view){
        startActivityForResult(new Intent(getApplicationContext(), Dispositivos.class), 20);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==24) {
            actionAdapter();
            sumarPeso();
        }
        else if(resultCode==25)
        {
            Utilities.showAlert(this,"Verifique el peso");
        }
    }

    /*public void actionAdapter() {
        adapter = new ItemAdapter(this, view_pesos);
        listView.setAdapter(adapter);
    }*/

    public void actionAdapter(){
        try {
            //inicializarComponentes();
            this.view_pesos_json = this.embalajeSeleccionado.getJSONArray("pesos_embalaje");
        } catch (JSONException e) {
            this.view_pesos_json = new JSONArray();
            e.printStackTrace();
        }
        adapterJson = new ItemAdapterJson(this, view_pesos_json);
        listView.setAdapter(adapterJson);
    }

    public  void sumarPeso()
    {
        double cont=0;
        for(int i=0;i<view_pesos_json.length();i++)
        {
            //Item item=view_pesos.get(i);
            double peso;
            try {
                peso = view_pesos_json.getJSONObject(i).getDouble("peso_asignado");
                cont += peso;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        double total;

        try {
            total=cont+this.embalajeSeleccionado.getDouble("pesoTotal");
        } catch (JSONException e) {
            total = cont;
        }
        try {

            this.embalajeSeleccionado.put("pesoTotal",total);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("PLANNED_CLIENTS",this.clientesPlaneados.toString());
        //editor.putString("LISTA_PESOS",this.view_pesos_json.toString());

        editor.commit();

        pesoTotal.setText("" + total);
    }

    /*public  void sumarPeso()
    {
        double cont=0;
        for(int i=0;i<view_pesos.size();i++)
        {
            Item item=view_pesos.get(i);
            String peso=item.getPeso().trim();
            cont+=Double.parseDouble(peso);
        }
        double total;

        try {
            total=cont+this.embalajeSeleccionado.getDouble("pesoTotal");
        } catch (JSONException e) {
            total = cont;
        }
        try {

            this.embalajeSeleccionado.put("pesoTotal",total);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String listaString = this.view_pesos.toString();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("PLANNED_CLIENTS",this.clientesPlaneados.toString());
        editor.putString("LISTA_PESOS",this.view_pesos.toString());

        editor.commit();

        pesoTotal.setText("" + total);
    }*/

   /* private void actualizarEmbalajesPesados(JSONArray savedOperators2){
        try {
            this.adapterJson = new C_ItemSelectedOperator(this, new JSONArray());
            this.lstSelectedOperators.setAdapter(adapterJson);
            for (int i = 0; i < savedOperators2.length(); i++) {
                if (savedOperators2.getJSONObject(i).getString("hora_fin").equals("")) {
                    this.adapterJson.addOperator(savedOperators2.getJSONObject(i));
                }
            }
            this.blockElements();
            this.adapterJson.notifyDataSetChanged();
        } catch (JSONException e) {
        }
*/


    public void comfirm(final String peso){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Confirmar Acción");
        adb.setMessage("Deseas eliminar el peso seleccionado?");
        adb.setPositiveButton(
                getResources().getString(R.string.accept_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restarPeso(peso);
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
    }

    public void restarPeso(String peso)
    {
        double cont=0;
        TextView textView=(TextView)findViewById(R.id.peso_total);
        peso=peso.trim();
        cont=Double.parseDouble(textView.getText().toString())-Double.parseDouble(peso);
        textView.setText(""+cont);
    }

    /*@Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*Item model = (Item) (parent.getItemAtPosition(position));
        view_pesos.remove(position);
        actionAdapter();
        comfirm(model.getPeso());
        //restarPeso(model.getPeso());
    }*/
}

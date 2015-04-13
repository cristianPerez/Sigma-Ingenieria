package utilidades;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.qualitysolutions.tecniamsa.R;
import co.qualitysolutions.tecniamsa.RegistrarBarras;
import co.qualitysolutions.tecniamsa.RegistrarPeso;

/**
 * Created by Andres on 05/04/2015.
 */
public class ItemAdapterJsonCodigos extends BaseAdapter{
    private final Activity activity;
    private JSONArray jsonSelected;
    private SharedPreferences sharedpreferences;

    public ItemAdapterJsonCodigos(){
        this.activity = null;
        this.jsonSelected = null;
        this.sharedpreferences = null;
    }

    public ItemAdapterJsonCodigos(Activity activity, JSONArray jsonSelected) {
        super();

        this.activity = activity;
        this.jsonSelected = jsonSelected;
        this.sharedpreferences = activity.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.jsonSelected.length();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * this method display the names of the employees in the list (selected employees) and change them of list
     * if the employee doesn't have started already the day. Else the user can finish the journey of any operator.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.list_item_barras, null,
                true);

        TextView lblName = (TextView) view.findViewById(R.id.nombre);
        Button btnDelete = (Button) view.findViewById(R.id.btnDelete);

        try {
            lblName.setText(String.valueOf(this.jsonSelected.getJSONObject(position).get("barra_asignada")));
            btnDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
						/*if (jsonSelected.getJSONObject(pos).getString("hora_fin").equals("")) {
							int posCurrentRout = sharedpreferences.getInt("POS_CURRENT_ROUTE", -1);
							if (posCurrentRout >= 0) {*/
                        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
                        adb.setTitle("Alerta!");
                        adb.setMessage("Desea eliminar el codigo de barras");
                        adb.setPositiveButton(activity.getResources()
                                        .getString(R.string.confirm_button_1),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,int which) {

                                        jsonSelected = Utilities.delete(jsonSelected,pos);

                                        JSONArray clientesPlaneados = null;
                                        JSONObject clienteSeleccionado = null;
                                        JSONArray listaTrazas = null;
                                        JSONObject trazaSeleccionada = null;
                                        JSONArray listaEmbalajes = null;
                                        JSONObject embalajeSeleccionado = null;
                                        int barrasNuevo = 0;

                                        try {
                                            clientesPlaneados = new JSONArray(sharedpreferences.getString("PLANNED_CLIENTS", "[]"));
                                            clienteSeleccionado = clientesPlaneados.getJSONObject(sharedpreferences.getInt("CLIENTE_SELECCIONADO", 0));
                                            listaTrazas = clienteSeleccionado.getJSONArray("lsttrazas");
                                            trazaSeleccionada = listaTrazas.getJSONObject(sharedpreferences.getInt("SELECT_TRAZA", 0));
                                            listaEmbalajes = trazaSeleccionada.getJSONArray("lstembalaje");
                                            embalajeSeleccionado = listaEmbalajes.getJSONObject(sharedpreferences.getInt("SELECT_EMBALAJE", 0));
                                            embalajeSeleccionado.put("barras_embalaje",jsonSelected);
                                            barrasNuevo = embalajeSeleccionado.getInt("cantTotal") - 1;
                                            embalajeSeleccionado.put("cantTotal", barrasNuevo);
                                        }catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString("PLANNED_CLIENTS",clientesPlaneados.toString());
                                        editor.commit();

                                        ((RegistrarBarras)activity).actionAdapter();
                                        ((RegistrarBarras)activity).restarBarras(String.valueOf(barrasNuevo));
                                        Toast.makeText(activity, "Eliminado satisfactoriamente", Toast.LENGTH_LONG).show();

                                    }
                                });
                        adb.setNegativeButton(activity.getResources()
                                        .getString(R.string.confirm_button_2),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,int which) {
                                        dialog.dismiss();
                                    }
                                });
                        adb.show();
							/*}
							else {
								Utilities.showAlert(activity,
										activity.getResources().getString(R.string.alertFinishJourneyWithoutRouteInit));
							}
						}*/

                    } catch (Exception e) {
                        e.printStackTrace();
						/*try {
							((C_GrupoTrabajo) activity).changeOperator(jsonSelected.getJSONObject(pos));
							jsonSelected = Utilities.delete(jsonSelected, pos);
						} catch (JSONException e1) {
						}*/
                    }

                }
            });
        } catch (JSONException e) {
        }

        return view;
    }

    public JSONArray getCurrentSelected(){
        return this.jsonSelected;
    }

    public void addOperator(JSONObject operator){
        this.jsonSelected.put(operator);
    }

    public void removeAll(){
        while(this.jsonSelected.length()>0){
            this.jsonSelected = Utilities.delete(this.jsonSelected, 0);
        }
    }
}

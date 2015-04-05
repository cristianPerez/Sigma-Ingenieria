package utilidades;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import co.qualitysolutions.tecniamsa.RegistrarPeso;
import co.qualitysolutions.tecniamsa.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ItemAdapterJson extends BaseAdapter {

	private final Activity activity;
	private JSONArray jsonSelected;
	private SharedPreferences sharedpreferences;

    public ItemAdapterJson(){
        this.activity = null;
        this.jsonSelected = null;
        this.sharedpreferences = null;
    }

	public ItemAdapterJson(Activity activity, JSONArray jsonSelected) {
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
		View view = inflater.inflate(R.layout.list_item_pesos, null,
				true);

		TextView lblName = (TextView) view.findViewById(R.id.nombre);
		Button btnDelete = (Button) view.findViewById(R.id.btnDelete);

		try {
			lblName.setText(String.valueOf(this.jsonSelected.getJSONObject(position).getDouble("peso_asignado"))+" KG");
			btnDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						/*if (jsonSelected.getJSONObject(pos).getString("hora_fin").equals("")) {
							int posCurrentRout = sharedpreferences.getInt("POS_CURRENT_ROUTE", -1);
							if (posCurrentRout >= 0) {*/
								AlertDialog.Builder adb = new AlertDialog.Builder(activity);
								adb.setTitle("Alerta!");
								adb.setMessage("Desea eliminar el peso");
								adb.setPositiveButton(activity.getResources()
										.getString(R.string.confirm_button_1),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog,int which) {
												/*try {
													((C_GrupoTrabajo) activity).changeOperator(jsonSelected.getJSONObject(pos));
													jsonSelected = Utilities.delete(jsonSelected,pos);
                                                    Toast.makeText(activity, "El operario se bajo satisfactoriamente", Toast.LENGTH_LONG).show();
												} catch (JSONException e) {
												}*/
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

package utilidades;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;

import co.qualitysolutions.tecniamsa.A_Login;
import co.qualitysolutions.tecniamsa.R;

public class SaveInformation extends AsyncTask<String, Void, Void> {
	
	private WebService connection;
	private Activity activity;
	private SharedPreferences sharedpreferences;
	private BackUpDataSource dataBase;
	
	public SaveInformation(Activity activity){
		this.connection = new WebService();
		this.activity = activity;
		this.sharedpreferences = activity.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
		this.dataBase = new BackUpDataSource(activity);
		this.dataBase.open();

    }

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected Void doInBackground(String... params){
		JSONArray answer;
		String token = this.sharedpreferences.getString("TOKEN", null);
		if(this.thereIsInternet()){
			//Send old information
			JSONArray stored = this.dataBase.getAllRoutes();
			if(stored.length()>0){
				this.connection.setUrl("http://www.concesionesdeaseo.com/pruebas/FUNEventosMovil/Eventos");
				String[] parameters = {"10",token, Utilities.getDate(),"backup",stored.toString()};
				answer = this.connection.conectar(parameters);
				try {
					if(answer.getJSONObject(0).getString("mensaje").equals("1")){
						this.dataBase.deleteRoutes();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			//Send current event
			this.connection.setUrl(params[0]);
            JSONArray aux=null;
            try {
                aux = new JSONArray(params[3]);
                aux.getJSONObject(0).put("version",R.string.version);
            } catch (JSONException e) {
                e.printStackTrace();
            }
			String[] parameters = {params[1], token, Utilities.getDate(), params[2], aux.toString()};
			answer = this.connection.conectar(parameters);
			try {
				if(!answer.getJSONObject(0).getString("mensaje").equals("1")){
					this.saveInDataBase(token, Utilities.getDate(), params[2], params[3]);
                    Log.e("Funcion", params[2].toString());
                    if(params[2].equals("json_tecni_cerrarsesion")){
                        closeSession();
                    }
				}
				else {
					if(params[2].equals("json_tecni_cerrarsesion")){
                        closeSession();
					}
				}
			} catch (JSONException e) {}
		}
		else{
			this.saveInDataBase(token, Utilities.getDate(), params[2], params[3]);
            if(params[2].equals("json_tecni_cerrarsesion")){
                closeSession();
            }
		}
		return null;
	}

    public void closeSession(){

        Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
        Intent intent = new Intent(activity.getApplicationContext(),A_Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);

    }

	@Override
	protected void onPostExecute(Void result) {
		
	}
	
	private void saveInDataBase(String token, String date, String event, String json){
		this.dataBase.CreateRoute(token, date, event, json);
	}
	
	private boolean thereIsInternet() {
		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}
}
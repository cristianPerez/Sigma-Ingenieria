package utilidades;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import co.qualitysolutions.tecniamsa.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utilities {
	
	public static JSONArray delete(JSONArray json, int pos){
		JSONArray last = new JSONArray();
		for(int i=0; i<json.length(); i++){
			if(i!=pos){
				try {
					last.put(json.getJSONObject(i));
				} catch (JSONException e) {
				}
			}
		}
		return last;
	}

    public static String getDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = dateFormat.format(c.getTime());
        return currentTime;
    }
	
	public static boolean compareDates(String dateServer , String hourServer){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
		String currentTime = dateFormat.format(c.getTime());
		String[] vecInitilize = currentTime.split(" ");
		String[] dateIntern = vecInitilize[0].split("-");
		String[] hourIntern = vecInitilize[1].split(":");
		String[] dateServerVec = dateServer.split("/");
		String[] hourServerVec = hourServer.split(":");

        if(Integer.parseInt(dateIntern[0])== Integer.parseInt(dateServerVec[0])&&
			Integer.parseInt(dateIntern[1])== Integer.parseInt(dateServerVec[1])&&
			 Integer.parseInt(dateIntern[2])== Integer.parseInt(dateServerVec[2]))
		{
            int aux1 = Integer.parseInt(hourIntern[0]) - Integer.parseInt(hourServerVec[0]);
            int aux2 = Integer.parseInt(hourServerVec[0]) - Integer.parseInt(hourIntern[0]);

            if(aux1==1  || aux2==1) {
                if (Integer.parseInt(hourIntern[1]) >= 50 && Integer.parseInt(hourIntern[1]) < 60 &&  Integer.parseInt(hourServerVec[1]) <= 10) {
                    return true;
                }
            }
            else
            {
                int aux3 = Integer.parseInt(hourIntern[1]) - Integer.parseInt(hourServerVec[1]);
                int aux4 = Integer.parseInt(hourServerVec[1]) - Integer.parseInt(hourIntern[1]);
                    if((Integer.parseInt(hourServerVec[0]) == Integer.parseInt(hourIntern[0]))){
                        if (  aux3<= 10 && aux3 >=0 ||aux4 <=10 && aux4 >=0 || (Integer.parseInt(hourServerVec[1]) == Integer.parseInt(hourIntern[1])))
                        return true;
                    else
                        return false;
                    }
                else
                   return false;
		    }
        }
		else
		{
			return false;
		}
        return false;
	}
	
	
	public static void showAlert(Activity activity, String message){
		AlertDialog.Builder adb = new AlertDialog.Builder(activity);
		adb.setTitle("!Alerta");
		adb.setMessage(message);
		adb.setPositiveButton(
                activity.getResources().getString(R.string.accept_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
		adb.show();
	}

    public static void sendInformation(Activity activity, String methodInt, String method,String send_data_json){
        new SaveInformation(activity).execute(activity.getResources().getString(R.string.url),
                methodInt,
                method,
                send_data_json.toString());
    }

    public static JSONArray inicializarClientesPlaneados(JSONArray clientes){

        JSONArray trazasCliente = new JSONArray();
        JSONArray embalajesTraza = new JSONArray();


        for (int i=0;i<clientes.length();i++){

            try {
                trazasCliente = clientes.getJSONObject(i).getJSONArray("lsttrazas");
                clientes.getJSONObject(i).put("hora_llegada_sitio_entrega","0");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int j=0;j<trazasCliente.length();j++){

                try {
                    embalajesTraza = trazasCliente.getJSONObject(j).getJSONArray("lstembalaje");
                    trazasCliente.getJSONObject(j).put("pesoTotal",0.0);
                    trazasCliente.getJSONObject(j).put("cantTotal",0);
                    trazasCliente.getJSONObject(j).put("punto_pesaje",0);
                    trazasCliente.getJSONObject(j).put("peso_en_recoleccion",0);
                    trazasCliente.getJSONObject(j).put("cantidad_en_recoleccion",0);
                    trazasCliente.getJSONObject(j).put("apto_cargue",0);
                    trazasCliente.getJSONObject(j).put("causales_no_cargue","");
                    trazasCliente.getJSONObject(j).put("check_recoleccion",false);
                    trazasCliente.getJSONObject(j).put("check_tirillas",false);
                    trazasCliente.getJSONObject(j).put("causales_no_cargue",0);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int k=0;k<embalajesTraza.length();k++){

                    try {
                        embalajesTraza.getJSONObject(k).put("barras_embalaje",new JSONArray());
                        embalajesTraza.getJSONObject(k).put("pesos_embalaje",new JSONArray());
                        embalajesTraza.getJSONObject(k).put("pesoTotal",0.0);
                        embalajesTraza.getJSONObject(k).put("cantTotal",0);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return clientes;
    }



    public static JSONObject inicializarCliente(JSONObject cliente){

        JSONArray trazasCliente = new JSONArray();
        JSONArray embalajesTraza = new JSONArray();


        try {
            trazasCliente = cliente.getJSONArray("lsttrazas");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int j=0;j<trazasCliente.length();j++){

            try {
                embalajesTraza = trazasCliente.getJSONObject(j).getJSONArray("lstembalaje");
                trazasCliente.getJSONObject(j).put("pesoTotal",0.0);
                trazasCliente.getJSONObject(j).put("cantTotal",0);
                trazasCliente.getJSONObject(j).put("punto_pesaje",0);
                trazasCliente.getJSONObject(j).put("peso_en_recoleccion",0);
                trazasCliente.getJSONObject(j).put("cantidad_en_recoleccion",0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int k=0;k<embalajesTraza.length();k++){

                try {
                    embalajesTraza.getJSONObject(k).put("barras_embalaje",new JSONArray());
                    embalajesTraza.getJSONObject(k).put("pesos_embalaje",new JSONArray());
                    embalajesTraza.getJSONObject(k).put("pesoTotal",0.0);
                    embalajesTraza.getJSONObject(k).put("cantTotal",0);
                    embalajesTraza.getJSONObject(k).put("apto_cargue",0);
                    embalajesTraza.getJSONObject(k).put("check_recoleccion",false);
                    embalajesTraza.getJSONObject(k).put("check_tirillas",false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        return cliente;
    }




}

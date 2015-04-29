package utilidades;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import co.qualitysolutions.tecniamsa.R;
import org.json.JSONArray;
import org.json.JSONException;

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
	
	/*public static String getDate(String serverDate , String hourServer){

		Calendar c = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = dateFormat.format(c.getTime());
        String[] vecInitilize = currentTime.split(" ");
        int yearSystem= Integer.valueOf(vecInitilize[0].toString().split("-")[0].toString());
        int yearServer= Integer.valueOf(serverDate.toString().split("/")[0].toString());
        int monthSystem= Integer.valueOf(vecInitilize[0].toString().split("-")[1].toString());
        int monthServer= Integer.valueOf(serverDate.toString().split("/")[1].toString());
        int daySystem= Integer.valueOf(vecInitilize[0].toString().split("-")[2].toString());
        int dayServer= Integer.valueOf(serverDate.toString().split("/")[2].toString());

        if(yearSystem != yearServer || monthSystem != monthServer || daySystem != dayServer ){
            return serverDate.toString().split("/")[0].toString()+"-"+serverDate.toString().split("/")[1].toString()+"-"+serverDate.toString().split("/")[2].toString()+" "+hourServer;
        }
        else {
            return currentTime;
        }

        return currentTime;
	}*/

    public static String getDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = dateFormat.format(c.getTime());
        return currentTime;
    }
	
	public static boolean compareDates(String dateServer , String hourServer){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
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
            if((Integer.parseInt(hourIntern[0])- Integer.parseInt(hourServerVec[0])<=1 || Integer.parseInt(hourIntern[0])- Integer.parseInt(hourServerVec[0])<=1)
                    && Integer.parseInt(hourIntern[1])>=50 && Integer.parseInt(hourIntern[1])<60
                    && Integer.parseInt(hourServerVec[1])<=10){

                return true;

            }
            else{
            if((Integer.parseInt(hourIntern[1])- Integer.parseInt(hourServerVec[1])<=10 && Integer.parseInt(hourIntern[1])- Integer.parseInt(hourServerVec[1])>0)
                    ||(Integer.parseInt(hourServerVec[1])- Integer.parseInt(hourIntern[1])<=10 && Integer.parseInt(hourServerVec[1])- Integer.parseInt(hourIntern[1])>0)
                    ||(Integer.parseInt(hourServerVec[1])== Integer.parseInt(hourIntern[1])))
                return true;

            else return false;
		}
        }
		else
		{
			return false;
		}

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
        new SaveInformation(activity).execute(activity.getResources().getString(R.string.urlPruebas),
                methodInt,
                method,
                send_data_json.toString());
    }

}

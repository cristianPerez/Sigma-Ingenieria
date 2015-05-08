package utilidades;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


public class GpsService implements LocationListener {

    private final Context ctx;
    public double latitud;
    public double longitud;
    public double altitude;
    public float speed;
    private SharedPreferences sharedpreferences;
    public Location location;
    public boolean gpsActivo;
    public TextView Ubicacion;
    public LocationManager locationManager;
    private JSONArray send_data_json;
    private String method;
    private String methodInt;


    public GpsService(Context ctx) {
        super();
        this.ctx = ctx;
        this.Ubicacion=null;
        this.sharedpreferences = this.ctx.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        getLocation();
        initializeTimeMilliseconds();
    }


    public void setView(View v){
        Ubicacion = (TextView)v;
        Ubicacion.setText(latitud+","+longitud);
    }

    public void setView2(){
        Log.d("SetView", obtenerCoordenadas());
    }

    public void initializeTimeMilliseconds(){

        SharedPreferences.Editor editor = sharedpreferences.edit();
        Calendar calendar = Calendar.getInstance();
        long timeMilliseconds = calendar.getTimeInMillis();
        editor.putString("TIME_MILLISECONDS", String.valueOf(timeMilliseconds));
        editor.commit();
        sendUbication();
    }

    /**
     *Method that send the information to server, from whatever method

    public void sendInformation(){
        try {
            new SaveLocationInformation(this.ctx).execute("http://www.concesionesdeaseo.com/gruposala/FUNEventosMovil/Eventos",
                    this.methodInt,
                    this.method,
                    this.send_data_json.toString());
        }catch (Exception e){
        }
    }*/


    public void getLocation() {
        try {

            locationManager =(LocationManager)this.ctx.getSystemService(this.ctx.LOCATION_SERVICE);
            gpsActivo = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
            if(gpsActivo){

                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,
                        1000
                        ,1
                        ,this);
                //Ultima ubicación conocida
                location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                altitude = location.getAltitude();
                speed = location.getSpeed();
                gpsActivo = true;

            }

        }
        catch (Exception e){

            try {
                locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER,
                        1000
                        ,1
                        ,this);

                location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                altitude = location.getAltitude();
                speed = location.getSpeed();
                gpsActivo=false;
            }
            catch(Exception r){

                r.printStackTrace();
            }
        }



    }

    public boolean gpsIsEnable(){

        return locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);

    }

    public String getLatitud(){

        return String.valueOf(latitud);

    }

    public String getLongitud(){

        return String.valueOf(longitud);

    }

    public String obtenerCoordenadas(){

        return latitud+","+longitud;

    }

    @Override
    public void onLocationChanged(Location location) {

        getLocation();
        try {

            if(gpsActivo){
                location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                altitude = location.getAltitude();
                speed = location.getSpeed();
            }
            else{
                location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                altitude = location.getAltitude();
                speed = location.getSpeed();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        if(Ubicacion!=null) {
            setView(Ubicacion);
        }

        sendUbication();


    }

    public void sendUbication(){

        Calendar calendar = Calendar.getInstance();
        long CurrentTimeMilliseconds = calendar.getTimeInMillis();
        long oldTimeMilliseconds = Long.valueOf(sharedpreferences.getString("TIME_MILLISECONDS", "1"));
        if(oldTimeMilliseconds!=1){
            if(CurrentTimeMilliseconds-oldTimeMilliseconds>=180000){

                Log.e("Entro", "sendUbication");

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("TIME_MILLISECONDS", String.valueOf(CurrentTimeMilliseconds));
                editor.commit();



                send_data_json = new JSONArray();
                JSONObject auxobject= new JSONObject();
                try {
                    auxobject.put("fecha_hora_evento",Utilities.getDate());
                    auxobject.put("metodo","Actualizar_Ubicacion");
                    auxobject.put("supervisor",sharedpreferences.getString("USER_ID","xxx"));
                    auxobject.put("coordenadas",obtenerCoordenadas());
                    send_data_json.put(auxobject);
                    method="actulizar_ubicacion";
                    methodInt="29";
                    //sendInformation();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }



    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        getLocation();
        try {

            if(gpsActivo){
                location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                altitude = location.getAltitude();
                speed = location.getSpeed();
            }
            else{
                location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                altitude = location.getAltitude();
                speed = location.getSpeed();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        if(Ubicacion!=null) {
            setView(Ubicacion);
            Log.d("Ubicacion 1", obtenerCoordenadas());
        }
        sendUbication();

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }



}
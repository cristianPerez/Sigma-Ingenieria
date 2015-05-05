package Adaptadores;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import co.qualitysolutions.tecniamsa.R;

public class ItemAdapter extends BaseAdapter {
	protected Activity activity;
	protected ArrayList<Item> items;

	public ItemAdapter(Activity activity, ArrayList<Item> items) {
		this.activity = activity;
		this.items = items;
	}

	public void setList(ArrayList<Item> arr) {
		this.items = arr;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return items.get(position).getId();
	}

	@Override
	public View getView(int position, View contentView, ViewGroup parent) {
		View vi = contentView;

		if (contentView == null) {
            if(activity.getClass().getName().equals("co.qualitysolutions.tecniamsa.Dispositivos")) {
                LayoutInflater inflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                vi = inflater.inflate(R.layout.list_item_dispositivos, null);
            }
            else{
                LayoutInflater inflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                vi = inflater.inflate(R.layout.list_item_pesos, null);
            }
		}

		Item item = items.get(position);
		ImageView image = (ImageView) vi.findViewById(R.id.imagen_item);
		int imageResource = activity.getResources().getIdentifier(
				item.getRutaImagen(), null, activity.getPackageName());
		image.setImageDrawable(activity.getResources().getDrawable(
				imageResource));

		TextView nombre = (TextView) vi.findViewById(R.id.nombre);
		nombre.setText(item.getNombre());

		TextView tipo = (TextView) vi.findViewById(R.id.tipo);
		tipo.setText(item.getTipo());

		return vi;
	}
}

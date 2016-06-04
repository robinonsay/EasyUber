package io.github.easyuber.easyuber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by robinonsay on 6/3/16.
 */
public class ProductsListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Map<String, String>> data;
    private LayoutInflater inflater=null;
    Context ctx;

    public ProductsListAdapter(Activity activity, ArrayList<Map<String, String>> data) {
        this.ctx = activity.getBaseContext();
        this.activity = activity;
        this.data = data;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;

        if(convertView==null)
            vi = inflater.inflate(R.layout.list_item, null);

        TextView descriptionTextView = (TextView) vi.findViewById(R.id.description_textview);
        TextView capcityTextView = (TextView) vi.findViewById(R.id.capacity_textview);
        TextView etaTextView = (TextView) vi.findViewById(R.id.eta_textview);

        Map<String, String> product;
        product = data.get(position);
        if(product != null) {
            String displayName = product.get("display_name");
            Log.d("DESCRIPTION", displayName);
            String capacity = product.get("capacity");
            Log.d("CAPACITY", capacity);
            String eta = product.get("estimate");
            String etaFormated;
            try {
                double arrivalTimeMinutes = (Integer.parseInt(eta) / 60.0);
                int minutes = (int) (arrivalTimeMinutes * 60) % 60;
                int seconds = (int) (arrivalTimeMinutes * (60 * 60)) % 60;
                etaFormated = minutes + ":" + seconds;
            }catch(NumberFormatException e){
                etaFormated = "Not Available";
            }

            Log.d("ESTIMATE", etaFormated);

            descriptionTextView.setText(displayName);
            capcityTextView.setText(capacity);
            etaTextView.setText(etaFormated);


            return vi;
        }
        return null;
    }
}

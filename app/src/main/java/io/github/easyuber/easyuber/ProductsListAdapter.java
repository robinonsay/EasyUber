package io.github.easyuber.easyuber;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by robinonsay on 6/3/16.
 */
public class ProductsListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Map<String, String>> data;
    private LayoutInflater inflater=null;
    private Container container;
    Context ctx;

    public ProductsListAdapter(Activity activity, ArrayList<Map<String, String>> data, Container container) {
        this.ctx = activity.getBaseContext();
        this.activity = activity;
        this.data = data;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.container = container;
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
        Button orderButton = (Button) vi.findViewById(R.id.order_button);

        final Map<String, String> product;
        product = data.get(position);
        if(product != null) {
            orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), UberOrderActivity.class);
                    intent.putExtra("access_token", container.getAccessToken());
                    intent.putExtra("start_latitude", container.getLatitude());
                    intent.putExtra("start_longitude", container.getLongitude());
                    intent.putExtra("product_id", product.get("product_id"));
                    activity.startActivity(intent);
                }
            });
            String displayName = product.get("display_name");
            Log.d("DESCRIPTION", displayName);
            String capacity = product.get("capacity");
            Log.d("CAPACITY", capacity);
            String eta = product.get("estimate");
            String etaFormated;
            try {
                double arrivalTimeMinutes = (Integer.parseInt(eta) / 60.0);
                int minutes = (int) (arrivalTimeMinutes);
                etaFormated = minutes + " minutes";
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

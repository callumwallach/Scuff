package nz.co.scuff.android.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import nz.co.scuff.data.school.Route;

/**
 * Created by Callum on 6/05/2015.
 */
public class RouteAdapter extends ArrayAdapter<Route> {

    private Context context;
    private List<Route> routes;

    public RouteAdapter(Context context, int textViewResourceId,
                        List<Route> routes) {
        super(context, textViewResourceId, routes);
        this.context = context;
        this.routes = routes;
    }

    public int getCount(){
        return routes.size();
    }

    public Route getItem(int position){
        return this.routes.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setText(this.routes.get(position).getName());
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
        label.setText(this.routes.get(position).getName());
        return label;
    }
}


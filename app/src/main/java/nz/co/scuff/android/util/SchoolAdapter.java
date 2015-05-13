package nz.co.scuff.android.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import nz.co.scuff.data.school.School;

/**
 * Created by Callum on 6/05/2015.
 */
public class SchoolAdapter extends ArrayAdapter<School> {

    private Context context;
    private List<School> schools;

    public SchoolAdapter(Context context, int textViewResourceId,
                         List<School> schools) {
        super(context, textViewResourceId, schools);
        this.context = context;
        this.schools = schools;
    }

    public int getCount(){
        return schools.size();
    }

    public School getItem(int position){
        return this.schools.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setText(this.schools.get(position).getName());
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
        label.setText(this.schools.get(position).getName());
        return label;
    }
}


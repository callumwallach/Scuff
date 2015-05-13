package nz.co.scuff.android.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import nz.co.scuff.android.R;
import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.family.Person;

/**
 * Created by Callum on 6/05/2015.
 */
public class PassengerAdapter extends ArrayAdapter<Passenger> {

    private static final String TAG = "PassengerAdapter";
    private static final boolean D = true;

    private Context context;
    private int layoutResourceId;
    private List<Passenger> passengers;

    public PassengerAdapter(Context context, int layoutResourceId, List<Passenger> passengers) {
        super(context, layoutResourceId, passengers);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.passengers = passengers;
    }

    public int getCount(){
        return passengers.size();
    }

    public Passenger getItem(int position){
        return this.passengers.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }


        Passenger child = passengers.get(position);
        holder.imageTitle.setText(child.getFirstName());
        Drawable profilePix = child.getGender() == Person.Gender.MALE ?
                context.getResources().getDrawable(R.drawable.male_blank_icon) :
                context.getResources().getDrawable(R.drawable.female_blank_icon);
        holder.image.setImageDrawable(profilePix);
        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
        label.setText(this.passengers.get(position).getFirstName());
        return label;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}


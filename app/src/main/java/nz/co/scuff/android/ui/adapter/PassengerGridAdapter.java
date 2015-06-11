package nz.co.scuff.android.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.co.scuff.android.R;
import nz.co.scuff.data.family.Child;
import nz.co.scuff.data.family.ChildData;
import nz.co.scuff.data.family.PersonalData;

/**
 * Created by Callum on 6/05/2015.
 */
public class PassengerGridAdapter extends ArrayAdapter<Child> {

    private static final String TAG = "PassengerGridAdapter";
    private static final boolean D = true;

    private Context context;
    private int layoutResourceId;
    private List<Child> children;
    private Map<Long, Child> selectedPassengers;

    public PassengerGridAdapter(Context context, int layoutResourceId, List<Child> children) {
        super(context, layoutResourceId, children);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.children = children;
        this.selectedPassengers = new HashMap<>();
    }

    public int getCount(){
        return children.size();
    }

    public Child getItem(int position){
        return this.children.get(position);
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

        Child child = children.get(position);
        holder.imageTitle.setText(child.getChildData().getFirstName());
        Drawable profilePix = child.getChildData().getGender() == ChildData.Gender.MALE ?
                context.getResources().getDrawable(R.drawable.male_blank_icon) :
                context.getResources().getDrawable(R.drawable.female_blank_icon);
        holder.image.setImageDrawable(profilePix);
        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
        label.setText(this.children.get(position).getChildData().getFirstName());
        return label;
    }

    public List<Child> getChildren() {
        return this.children;
    }

    public void setSelection(Child child) {
        this.selectedPassengers.put(child.getChildId(), child);
    }

    public void removeSelection(Child child) {
        this.selectedPassengers.remove(child.getChildId());
    }

    public int getSelectedCount() {
        return selectedPassengers.keySet().size();
    }

    public Map<Long, Child> getSelected() {
        return selectedPassengers;
    }

    public void clearSelection() {
        this.selectedPassengers = new HashMap<>();
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}


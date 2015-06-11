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

import java.util.List;

import nz.co.scuff.android.R;
import nz.co.scuff.data.base.Coordinator;
import nz.co.scuff.data.family.PersonalData;

/**
 * Created by Callum on 6/05/2015.
 */
public class CoordinatorAdapter extends ArrayAdapter<Coordinator> {

    private static final String TAG = "CoordAdapter";
    private static final boolean D = true;

    private Context context;
    private int layoutResourceId;
    private List<Coordinator> data;

    public CoordinatorAdapter(Context context, int layoutResourceId,
                              List<Coordinator> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    public int getCount(){
        return data.size();
    }

    public Coordinator getItem(int position){
        return this.data.get(position);
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

        Coordinator coordinator = data.get(position);
        holder.imageTitle.setText(coordinator.getName());
        Drawable picture = loadPicture(coordinator.getPicture());
        if (picture == null) {
            if (coordinator.getType().equals(Coordinator.CoordinatorType.ADULT)) {
                picture = coordinator.getPersonalData().getGender() == PersonalData.Gender.MALE ?
                        context.getResources().getDrawable(R.drawable.male_blank_icon) :
                        context.getResources().getDrawable(R.drawable.female_blank_icon);
            } else {
                picture = context.getResources().getDrawable(R.drawable.institution_blank_icon);
            }
        }
        holder.image.setImageDrawable(picture);
        return row;
    }

    private Drawable loadPicture(String path) {
        return null;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
        label.setText(this.data.get(position).getInstitutionData().getName());
        return label;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}


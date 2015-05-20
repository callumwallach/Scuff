package nz.co.scuff.android.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.manuelpeinado.multichoiceadapter.MultiChoiceBaseAdapter;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.R;
import nz.co.scuff.android.event.SelectionEvent;
import nz.co.scuff.android.util.ScuffApplication;
import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.family.Person;

/**
 * Created by Callum on 18/05/2015.
 */
public class PassengerMultiChoiceAdapter extends MultiChoiceBaseAdapter {

    private static final String TAG = PassengerMultiChoiceAdapter.class.getSimpleName();
    private static final boolean D = true;

    private List<Passenger> passengers;

    public PassengerMultiChoiceAdapter(Bundle savedInstanceState, List<Passenger> passengers) {
        super(savedInstanceState);
        this.passengers = passengers;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_select_children, menu);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (D) Log.d(TAG, "onActionItemClicked mode="+mode+" menuitem="+item);
        if (item.getItemId() == R.id.select) {
            List<Long> positions = new ArrayList<>(getCheckedItems());
            List<Passenger> selectedPassengers = new ArrayList<>(positions.size());
            for (long position : positions) {
                selectedPassengers.add(passengers.get((int) position));
                //DialogHelper.toast(getContext(), passengers.get((int)position).getFirstName());
            }
            EventBus.getDefault().post(new SelectionEvent(selectedPassengers));
            finishActionMode();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public int getCount() {
        return passengers.size();
    }

    @Override
    public Passenger getItem(int position) {
        return passengers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected View getViewImpl(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            int layout = R.layout.grid_item;
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
        }
        ImageView imageView = (ImageView) convertView;
        Passenger passenger = getItem(position);
        Context ctx = ScuffApplication.getContext();
        Drawable profilePix;
        if (passenger.getPicture() != null) {
            // load from disk
            String fileLocation = ctx.getFilesDir() + "/" + passenger.getPicture();
            profilePix = Drawable.createFromPath(fileLocation);
        } else {
            // default images based on gender
            profilePix = passenger.getGender() == Person.Gender.MALE ?
                    ctx.getResources().getDrawable(R.drawable.male_blank_icon) : ctx.getResources().getDrawable(R.drawable.female_blank_icon);
        }
        imageView.setImageDrawable(profilePix);
        return imageView;

        /*View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.select_passenger_grid, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Passenger passenger = passengers.get(position);
        holder.imageTitle.setText(passenger.getFirstName());
        Drawable profilePix;
        if (passenger.getPicture() != null) {
            // load from disk
            String fileLocation = getContext().getFilesDir() + "/" + passenger.getPicture();
            profilePix = Drawable.createFromPath(fileLocation);
        } else {
            // default images based on gender
            profilePix = passenger.getGender() == Person.Gender.MALE ?
                    getContext().getResources().getDrawable(R.drawable.male_blank_icon) : getContext().getResources().getDrawable(R.drawable.female_blank_icon);
        }
        holder.image.setImageDrawable(profilePix);
        return row;*/
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}

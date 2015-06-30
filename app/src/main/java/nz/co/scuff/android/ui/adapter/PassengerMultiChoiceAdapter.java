package nz.co.scuff.android.ui.adapter;

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

import com.manuelpeinado.multichoiceadapter.MultiChoiceBaseAdapter;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.R;
import nz.co.scuff.android.event.SelectionEvent;
import nz.co.scuff.android.util.ScuffApplication;
import nz.co.scuff.data.family.Child;
import nz.co.scuff.data.family.ChildData;
import nz.co.scuff.data.family.PersonalData;

/**
 * Created by Callum on 18/05/2015.
 */
public class PassengerMultiChoiceAdapter extends MultiChoiceBaseAdapter {

    private static final String TAG = PassengerMultiChoiceAdapter.class.getSimpleName();
    private static final boolean D = true;

    private List<Child> children;

    public PassengerMultiChoiceAdapter(Bundle savedInstanceState, List<Child> children) {
        super(savedInstanceState);
        this.children = children;
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
            List<Child> selectedChildren = new ArrayList<>(positions.size());
            for (long position : positions) {
                selectedChildren.add(children.get((int) position));
                //DialogHelper.toast(getContext(), passengers.get((int)position).getFirstName());
            }
            EventBus.getDefault().post(new SelectionEvent(selectedChildren));
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
        return children.size();
    }

    @Override
    public Child getItem(int position) {
        return children.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected View getViewImpl(int position, View convertView, ViewGroup parent) {

        /*if (convertView == null) {
            int layout = R.layout.grid_item;
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
        }
        ImageView imageView = (ImageView) convertView;
        Child child = getItem(position);
        Context ctx = ScuffApplication.getContext();
        Drawable profilePix;
        if (child.getChildData().getPicture() != null) {
            // load from disk
            String fileLocation = ctx.getFilesDir() + "/" + child.getChildData().getPicture();
            profilePix = Drawable.createFromPath(fileLocation);
        } else {
            // default images based on gender
            profilePix = child.getChildData().getGender() == ChildData.Gender.MALE ?
                    ctx.getResources().getDrawable(R.drawable.male_blank_icon) : ctx.getResources().getDrawable(R.drawable.female_blank_icon);
        }
        imageView.setImageDrawable(profilePix);
        return imageView;*/

        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            int layout = R.layout.select_passenger_grid;
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(layout, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Child child = getItem(position);
        Context ctx = ScuffApplication.getContext();
        Drawable profilePix;
        if (child.getChildData().getPicture() != null) {
            // load from disk
            String fileLocation = ctx.getFilesDir() + "/" + child.getChildData().getPicture();
            profilePix = Drawable.createFromPath(fileLocation);
        } else {
            // default images based on gender
            profilePix = child.getChildData().getGender() == ChildData.Gender.MALE ?
                    ctx.getResources().getDrawable(R.drawable.male_blank_icon) : ctx.getResources().getDrawable(R.drawable.female_blank_icon);
        }
        holder.image.setImageDrawable(profilePix);
        holder.imageTitle.setText(child.getChildData().getFirstName()+" "+child.getChildData().getLastName());
        return row;

    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}

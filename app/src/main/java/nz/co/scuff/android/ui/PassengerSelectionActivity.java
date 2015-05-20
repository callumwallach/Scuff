package nz.co.scuff.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.R;
import nz.co.scuff.android.event.SelectionEvent;
import nz.co.scuff.android.service.TicketIntentService;
import nz.co.scuff.android.ui.adapter.PassengerMultiChoiceAdapter;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.journey.Journey;

public class PassengerSelectionActivity extends Activity
        implements AdapterView.OnItemClickListener {

    private static final String TAG = PassengerSelectionActivity.class.getSimpleName();
    private static final boolean D = true;

    private PassengerMultiChoiceAdapter adapter;
    private ArrayList<Passenger> items;
    private Journey journey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_selection);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        items = getIntent().getParcelableArrayListExtra(Constants.PASSENGERS_KEY);
        journey = getIntent().getParcelableExtra(Constants.JOURNEY_KEY);
        rebuildList(savedInstanceState);
    }

    private GridView getGridView() {
        return (GridView) findViewById(android.R.id.list);
    }

    public void onItemClick(android.widget.AdapterView<?> adapterView, View view, int position, long id) {
        Toast.makeText(this, "Item click: " + items.get(position).toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*getMenuInflater().inflate(R.menu.menu_passenger_selection, menu);*/
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_select_children, menu);
        return true;
    }

    public void onEventMainThread(SelectionEvent event) {
        if (D) Log.d(TAG, "Main thread message selection event=" + event);

        ArrayList<Long> idsOfChildrenTravelling = new ArrayList<>();
        for (Object o : event.getItems()) {
            Passenger child = (Passenger)o;
            idsOfChildrenTravelling.add(child.getPersonId());
        }

        Intent ticketIntent = new Intent(this, TicketIntentService.class);
        ticketIntent.putExtra(Constants.JOURNEY_KEY, this.journey.getJourneyId());
        ticketIntent.putExtra(Constants.PASSENGERS_KEY, idsOfChildrenTravelling);
        startService(ticketIntent);

        finishUp();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishUp();
                finish();
                return true;
            case R.id.menu_select_all:
                selectAll();
                return true;
            case R.id.menu_reset_list:
                rebuildList(null);
                return true;
        }
        return false;
    }

    private void finishUp() {

        Intent parentActivityIntent = new Intent(this, PassengerHomeActivity.class);
        // TODO flags??
        parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(parentActivityIntent);

    }

    private void selectAll() {
        for (int i = 0; i < adapter.getCount(); ++i) {
            adapter.setItemChecked(i, true);
        }
    }

    private void rebuildList(Bundle savedInstanceState) {
        adapter = new PassengerMultiChoiceAdapter(savedInstanceState, items);
        adapter.setOnItemClickListener(this);
        adapter.setAdapterView(getGridView());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        adapter.save(outState);
    }

    @Override
    protected void onStart() {
        if (D) Log.d(TAG, "onStart");
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        if (D) Log.d(TAG, "onStop");
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
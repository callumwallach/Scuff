package nz.co.scuff.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import nz.co.scuff.android.R;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.ScuffApplication;
import nz.co.scuff.data.base.Coordinator;

public class GuidedJourneyActivity extends ActionBarActivity {

    private static final String TAG = "GuidedJourney";
    private static final boolean D = true;

    private static final int SELECT_GUIDEE = 0;
    private static final int SELECT_ROUTE = 1;

    private long guideeId;
    private long routeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guided_journey);

        Coordinator coordinator = ((ScuffApplication)getApplication()).getCoordinator();
        Intent guideeIntent = new Intent(this, SelectGuideeActivity.class);
        guideeIntent.putExtra(Constants.PARENT_ACTIVITY_CLASS_NAME, DriverJourneyChoiceActivity.class.getName());
        guideeIntent.putExtra(Constants.COORDINATOR_ID_KEY, coordinator.getCoordinatorId());
        startActivityForResult(guideeIntent, SELECT_GUIDEE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_guided_journey, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void selectRoute(Intent data) {
        this.guideeId = data.getLongExtra(Constants.COORDINATOR_ID_KEY, -1);
        if (D) Log.d(TAG, "selected guidee="+this.guideeId);
        Intent routeIntent = new Intent(this, SelectRouteActivity.class);
        routeIntent.putExtra(Constants.PARENT_ACTIVITY_CLASS_NAME, DriverJourneyChoiceActivity.class.getName());
        routeIntent.putExtra(Constants.COORDINATOR_ID_KEY, this.guideeId);
        startActivityForResult(routeIntent, SELECT_ROUTE);
    }

    private void startJourney(Intent data) {
        this.routeId = data.getLongExtra(Constants.ROUTE_ID_KEY, -1);
        if (D) Log.d(TAG, "selected route="+this.routeId);
        Coordinator coordinator = ((ScuffApplication)getApplication()).getCoordinator();
        Intent journeyIntent = new Intent(this, DriverHomeActivity.class);
        journeyIntent.putExtra(Constants.OWNER_ID_KEY, this.guideeId);
        journeyIntent.putExtra(Constants.AGENT_ID_KEY, coordinator.getCoordinatorId());
        journeyIntent.putExtra(Constants.ROUTE_ID_KEY, this.routeId);
        startActivity(journeyIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case (SELECT_GUIDEE) : {
                if (resultCode == Activity.RESULT_OK) selectRoute(data);
                break;
            }
            case (SELECT_ROUTE) : {
                if (resultCode == Activity.RESULT_OK) startJourney(data);
                break;
            }
        }
    }

}

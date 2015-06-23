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

public class SoloJourneyActivity extends ActionBarActivity {

    private static final String TAG = "SoloJourney";
    private static final boolean D = true;

    private static final int SELECT_FRIEND = 0;
    private static final int SELECT_ROUTE = 1;

    private long agentId;
    private long routeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_journey);

        Coordinator coordinator = ((ScuffApplication)getApplication()).getCoordinator();
        Intent routeIntent = new Intent(this, SelectRouteActivity.class);
        routeIntent.putExtra(Constants.PARENT_ACTIVITY_CLASS_NAME, DriverJourneyChoiceActivity.class.getName());
        routeIntent.putExtra(Constants.COORDINATOR_ID_KEY, coordinator.getCoordinatorId());
        startActivityForResult(routeIntent, SELECT_ROUTE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_solo_journey, menu);
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

    private void startJourney(Intent data) {
        this.agentId = data.getLongExtra(Constants.COORDINATOR_ID_KEY, -1);
        if (D) Log.d(TAG, "selected friend="+this.agentId);
        Coordinator coordinator = ((ScuffApplication)getApplication()).getCoordinator();
        Intent journeyIntent = new Intent(this, DriverHomeActivity.class);
        journeyIntent.putExtra(Constants.OWNER_ID_KEY, coordinator.getCoordinatorId());
        journeyIntent.putExtra(Constants.AGENT_ID_KEY, this.agentId);
        journeyIntent.putExtra(Constants.ROUTE_ID_KEY, this.routeId);
        startActivity(journeyIntent);
    }

    private void selectAgent(Intent data) {
        this.routeId = data.getLongExtra(Constants.ROUTE_ID_KEY, -1);
        if (D) Log.d(TAG, "selected route="+this.routeId);
        Coordinator coordinator = ((ScuffApplication)getApplication()).getCoordinator();
        Intent agentIntent = new Intent(this, SelectFriendActivity.class);
        agentIntent.putExtra(Constants.PARENT_ACTIVITY_CLASS_NAME, DriverJourneyChoiceActivity.class.getName());
        agentIntent.putExtra(Constants.COORDINATOR_ID_KEY, coordinator.getCoordinatorId());
        startActivityForResult(agentIntent, SELECT_FRIEND);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case (SELECT_FRIEND) : {
                if (resultCode == Activity.RESULT_OK) startJourney(data);
                break;
            }
            case (SELECT_ROUTE) : {
                if (resultCode == Activity.RESULT_OK) selectAgent(data);
                break;
            }
        }
    }

}

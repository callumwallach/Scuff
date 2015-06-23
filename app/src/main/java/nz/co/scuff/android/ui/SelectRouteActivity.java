package nz.co.scuff.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Set;

import nz.co.scuff.android.R;
import nz.co.scuff.android.ui.fragment.RouteFragment;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.DialogHelper;
import nz.co.scuff.data.base.Coordinator;
import nz.co.scuff.data.institution.Route;

public class SelectRouteActivity extends ActionBarActivity
        implements RouteFragment.OnFragmentInteractionListener{

    private static final String TAG = "SelectRoute";
    private static final boolean D = true;

    private String parentActivityClassName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_route);

        long coordinatorId = getIntent().getLongExtra(Constants.COORDINATOR_ID_KEY, -1);
        Coordinator coordinator = Coordinator.findById(coordinatorId);
        Set<Route> routes = coordinator.getRoutes();

        parentActivityClassName = getIntent().getStringExtra(Constants.PARENT_ACTIVITY_CLASS_NAME);

        if (D) Log.d(TAG, "found coordinator="+coordinator.getCoordinatorId()+" routes="+routes);

        RouteFragment fragment = RouteFragment.newInstance(new ArrayList<>(routes));
        getSupportFragmentManager().beginTransaction().add(R.id.linearLayout, fragment).commit();
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        Intent intent = getParentActivityIntentImpl();
        if (intent == null) {
            intent = super.getSupportParentActivityIntent();
        }
        return intent;
    }

    @Override
    public Intent getParentActivityIntent() {
        Intent intent = getParentActivityIntentImpl();
        if (intent == null) {
            intent = super.getParentActivityIntent();
        }
        return intent;
    }

    private Intent getParentActivityIntentImpl() {
        Intent intent = null;
        if (parentActivityClassName != null) {
            try {
                Class parentActivityClass = Class.forName(parentActivityClassName);
                intent = new Intent(this, parentActivityClass);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Parent activity class="+parentActivityClassName+" not found");
            }
        }
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_route, menu);
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

    public void onFragmentInteraction(Route item) {
        DialogHelper.toast(this, "Selected "+item.getName());
        Intent result = new Intent();
        result.putExtra(Constants.ROUTE_ID_KEY, item.getRouteId());
        setResult(Activity.RESULT_OK, result);
        finish();
    }

}

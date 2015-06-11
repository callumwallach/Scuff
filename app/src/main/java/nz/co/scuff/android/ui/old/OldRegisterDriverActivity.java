package nz.co.scuff.android.ui.old;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import nz.co.scuff.android.R;
import nz.co.scuff.data.institution.Route;
import nz.co.scuff.android.util.ImageHelper;

public class OldRegisterDriverActivity extends Activity {

    static final int PICK_ROUTE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_activity_register_driver);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void chooseRoute(View v) {

        Intent intent = new Intent(this, OldSelectRouteActivity.class);
        startActivityForResult(intent, PICK_ROUTE_REQUEST);

    }

    public void addDriver(View v) {

/*        Coordinator adult = new Coordinator();
        String name = ((EditText)findViewById(R.id.driver_name)).getText().toString();
        String email = ((EditText)findViewById(R.id.driver_email)).getText().toString();
        String phone = ((EditText)findViewById(R.id.driver_phone)).getText().toString();
        adult.setFirstName(name);
        adult.setEmail(email);
        adult.setPhone(phone);

        Family family = ((ScuffContextProvider)getApplicationContext()).getFamily();
        family.addParent(adult);
        TestDataProvider.storeFamily(family);
        finish();*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_ROUTE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Button driverButton = (Button)findViewById(R.id.button);
                //ViewGroup.LayoutParams params = driverButton.getLayoutParams();
                int height = driverButton.getHeight() - driverButton.getPaddingTop() - driverButton.getPaddingBottom();
                int width = driverButton.getWidth() - driverButton.getPaddingLeft() - driverButton.getPaddingRight();
                Route route = (Route)data.getParcelableExtra(OldSelectRouteActivity.CHOSEN_ROUTE);
                String fileLocation = getFilesDir() + "/" + route.getRouteMap();
                //Bitmap bitmap = BitmapFactory.decodeFile(fileLocation);
                Bitmap bitmap = ImageHelper.lessResolution(fileLocation, width, height);
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                driverButton.setBackground(drawable);
                //driverButton.setVisibility(View.GONE);
            }
        }
    }

}

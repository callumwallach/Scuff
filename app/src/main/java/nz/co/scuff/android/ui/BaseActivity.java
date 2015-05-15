package nz.co.scuff.android.ui;

import android.app.Activity;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.event.ErrorEvent;
import nz.co.scuff.android.util.DialogHelper;

/**
 * Created by Callum on 15/05/2015.
 */
public class BaseActivity extends Activity {

    private class ErrorHandler {
        public void onEventMainThread(ErrorEvent event) {
            DialogHelper.errorToast(getBaseContext(), event.getError().getLocalizedMessage());
        }
    }

    private ErrorHandler errorHandler;

    @Override
    protected void onResume() {
        super.onResume();
        this.errorHandler = new ErrorHandler();
        EventBus.getDefault().register(errorHandler);
    }
    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(errorHandler);
    }

}

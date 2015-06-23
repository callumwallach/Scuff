package nz.co.scuff.android.ui;

import android.support.v7.app.ActionBarActivity;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.event.ErrorEvent;
import nz.co.scuff.android.util.DialogHelper;
import nz.co.scuff.server.error.ScuffException;

/**
 * Created by Callum on 15/05/2015.
 */
public class BaseActivity extends ActionBarActivity {

    private class ErrorHandler {
        public void onEventMainThread(ErrorEvent event) {
            ScuffException error = event.getError();
            DialogHelper.errorToast(getBaseContext(), error.getMessage()+": "+error.getReason());
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

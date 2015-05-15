package nz.co.scuff.android.ui;

import android.support.v4.app.FragmentActivity;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.event.ErrorEvent;
import nz.co.scuff.android.util.DialogHelper;

/**
 * Created by Callum on 15/05/2015.
 */
public class BaseFragmentActivity extends FragmentActivity {

    private class ErrorHandler {
        public void onEventMainThread(ErrorEvent event) {
            DialogHelper.errorToast(getBaseContext(), event.getError().getReason());
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

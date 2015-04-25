package nz.co.scuff.android.util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Dialog helper class
 */
public final class DialogHelper {

    /**
     * Display error toast
     *
     * @param ctx application context
     * @param text error text to display
     */
    public static void errorToast(Context ctx, String text) {

        Toast toast = Toast.makeText(ctx, text, Toast.LENGTH_LONG);
        ((TextView) toast.getView().findViewById(android.R.id.message)).
                setTextColor(Color.RED);
        toast.show();

    }

    /**
     * Display toast
     *
     * @param ctx application context
     * @param text text to display
     */
    public static void toast(Context ctx, String text) {

        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();

    }

    /**
     * Display dialog
     *
     * @param ctx application context
     * @param title dialog title
     * @param text dialog text
     */
    public static void dialog(Context ctx, String title, String text) {

        new AlertDialog.Builder(ctx).setTitle(title).
                setNegativeButton("Close", null).
                setMessage(text).show();

    }

}

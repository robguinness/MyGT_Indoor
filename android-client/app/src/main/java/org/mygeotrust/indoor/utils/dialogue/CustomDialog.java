package org.mygeotrust.indoor.utils.dialogue;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by mis on 7/24/2015.
 */
public abstract class CustomDialog {
    private static final String TAG = CustomDialog.class.getSimpleName();

    // constants
    public static final int OK_ONLY = 0;
    public static final int OK_CANCEL = 1;
    public static final int YES_NO = 2;

    /**
     * Generic method for showing a alert dialog
     *
     * @param context
     * @param title
     * @param message
     * @param type
     * @param listener
     *
     * created by mesbahul on 24th July '15
     */
    public static void showDialog(Context context, String title, String message, int type, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // TODO: add a custom view in drawable folder instead of creating dynamic TextView
        TextView titleText = new TextView(context);
        titleText.setText(title);
        titleText.setGravity(Gravity.CENTER_HORIZONTAL);
        titleText.setTextSize(20);
        titleText.setPadding(5, 5, 5, 5);
        //titleText.setTextColor(context.getResources().getColor(org.mygeotrust.service.R.color.cyan));

        builder.setCustomTitle(titleText);
        builder.setMessage(message);

        switch(type)
        {
            case OK_ONLY:
                builder.setPositiveButton(android.R.string.ok, listener);
                break;
            case OK_CANCEL:
                builder.setPositiveButton(android.R.string.ok, listener);
                builder.setNegativeButton(android.R.string.cancel, listener);
                break;
            case YES_NO:
                builder.setPositiveButton("YES", listener);
                builder.setNegativeButton("NO", listener);
                break;
        }

        builder.create().show();
    }
}
